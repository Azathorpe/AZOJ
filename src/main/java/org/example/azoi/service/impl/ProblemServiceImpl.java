package org.example.azoi.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemSampleDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemTagDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.problem_model.*;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.ProblemService;
import org.example.azoi.utils.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemServiceImpl implements ProblemService {
    @Value("${azoi.storage.root}")
    private String storageRoot;
    @Value("${azoi.storage.problem-dir}")
    private String problemPath;

    private final ProblemRepository problemRepository;
    private final ProblemTagRepository problemTagRepository;
    private final ProblemSampleRepository problemSampleRepository;
    private final ProblemFileRepository problemFileRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public ProblemServiceImpl(ProblemRepository problemRepository, TagRepository tagRepository, ProblemTagRepository problemTagRepository, ProblemSampleRepository problemSampleRepository, ProblemFileRepository problemFileRepository, UserRepository userRepository) {
        this.problemRepository = problemRepository;
        this.tagRepository = tagRepository;
        this.problemTagRepository = problemTagRepository;
        this.problemSampleRepository = problemSampleRepository;
        this.problemFileRepository = problemFileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Result<List<ProblemSimpleInfoVO>> getProblems(ProblemQueryDTO query) {
        Specification<Problem> spec = (root, query1, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (StringUtils.hasText(query.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + query.getTitle() + "%"));
            }
            if (query.getDifficulty() != null) {
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), query.getDifficulty()));
            }
            if (query.getVisible() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isVisible"), query.getVisible()));
            }
            if (StringUtils.hasText(query.getTag())) {
                Tag tag = tagRepository.findByName(query.getTag()).orElse(null);
                if (tag == null) return criteriaBuilder.disjunction();   // 永远 false，空结果
                List<Long> ids = problemTagRepository.findById_TagId(tag.getId())
                        .stream().map(pt -> pt.getId().getProblemId()).toList();
                if (ids.isEmpty()) return criteriaBuilder.disjunction();
                predicates.add(root.get("id").in(ids));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                Math.max(query.getPage() - 1, 0),
                query.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return new Result<>(
                problemRepository.findAll(spec, pageable).stream().map(ProblemSimpleInfoVO::new).toList(),
                Result.SUCCESS,
                "ok");
    }

    @Override
    public Result<ProblemInfoVO> getProblem(Long problemId) {
        Optional<Problem> result = problemRepository.findById(problemId);

        if (result.isPresent()) {
            Problem problem = result.get();

            //除了获取题目 还需要手动获取创建者信息 如果没有 那就已注销
            Optional<User> creator = userRepository.findById(problem.getCreatedBy());
            UserInfoVO creatorInfo;
            creatorInfo = creator.map(UserInfoVO::new).orElseGet(() -> new UserInfoVO() {{
                this.setUsername("已注销");
            }});

            return new Result<>(new ProblemInfoVO(problem, creatorInfo), Result.SUCCESS, "ok");
        }
        return new Result<>(null, Result.FAIL, "Problem not found");
    }

    @Override
    public Result<ProblemInfoVO> createProblem(ProblemCreateDTO problemCreateDTO) {
        Problem problem = problemRepository.save(new Problem(problemCreateDTO));
        StringBuilder sb = new StringBuilder();

        //获取题目的id
        Long problemId = problem.getId();
        //通过Id注册samples,file和tags
        //samples
        List<ProblemSampleDTO> samples = problemCreateDTO.getSamples();
        int index = 0;
        for (ProblemSampleDTO sample : samples) {
            ProblemSample problemSample = new ProblemSample(sample);
            problemSample.setProblemId(problemId);
            problemSample.setSortOrder(problemSample.getSortOrder() == null ? index : problemSample.getSortOrder());
            index++;
            problemSampleRepository.save(problemSample);
        }

        //file
        //文件名以题目名字+测试点名字.in拼接而成
        List<ProblemFileDTO> problemFiles = problemCreateDTO.getProblemFiles();
        index = 0;
        for (ProblemFileDTO problemFileDTO : problemFiles) {
            Result<Void> fileResult = uploadFile(problemFileDTO, problem, index++);

            if (fileResult.getCode() == Result.FAIL) {
                sb.append("TestPoint ").append(index).append("FAILED, Reason: ").append(fileResult.getMsg()).append("\n");
                continue;
            }
            sb.append("TestPoint ").append(index).append("SUCCESS\n");
        }

        //TAGS
        List<ProblemTagDTO> tags = problemCreateDTO.getProblemTags();
        for(ProblemTagDTO tag : tags)
            problemTagRepository.save(new ProblemTag(tag));

        return new Result<>(new ProblemInfoVO(),
                Result.SUCCESS,
                sb.toString());
    }


    private Result<Void> uploadFile(ProblemFileDTO problemFileDTO, Problem problem, int testPoint) {
        //我们将problem的名字+测试点 后缀通过fileType拼接
        String fileName = problem.getTitle() + testPoint + "." + ProblemFile.parseType(problemFileDTO.getFileType());
        if (fileName.contains("/") || fileName.contains(".."))
            return new Result<>(null, Result.FAIL, "非法的文件名");

        Path dir = Paths.get(storageRoot + "/" + fileName);
        Path target = dir.resolve(fileName);

        try{
            Files.createDirectories(dir);

            //Write
            try(InputStream is = problemFileDTO.getFile().getInputStream()){
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }

            //MD5
            String md5 = DigestUtils.md5DigestAsHex(Files.newInputStream(target));

            //Write In mysql
            ProblemFile pf = new  ProblemFile();
            pf.setProblemId(problem.getId());
            pf.setFileType(problemFileDTO.getFileType());
            pf.setFileSize(problemFileDTO.getFile().getSize());
            pf.setFilename(fileName);
            pf.setStoragePath(target.toString());
            pf.setMd5(md5);
            problemFileRepository.save(pf);
        } catch (IOException e) {
            return new Result<>(null, Result.FAIL, "文件保存失败 Exception:" + e.getMessage());
        }

        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
