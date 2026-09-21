package org.example.azoi.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileVO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemSampleDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemTagDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.problem_model.*;
import org.example.azoi.model.team_model.Role;
import org.example.azoi.model.user_model.User;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.service.ProblemService;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;

@Service
public class ProblemServiceImpl implements ProblemService {
    private static final Logger log = LoggerFactory.getLogger(ProblemServiceImpl.class);
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
    private final UserRoleRepository userRoleRepository;

    public ProblemServiceImpl(ProblemRepository problemRepository, TagRepository tagRepository, ProblemTagRepository problemTagRepository, ProblemSampleRepository problemSampleRepository, ProblemFileRepository problemFileRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.problemRepository = problemRepository;
        this.tagRepository = tagRepository;
        this.problemTagRepository = problemTagRepository;
        this.problemSampleRepository = problemSampleRepository;
        this.problemFileRepository = problemFileRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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
    public Result<ProblemInfoVO> getProblemById(Long problemId) {
        Optional<Problem> result = problemRepository.findById(problemId);

        if (result.isPresent()) {
            Problem problem = result.get();
            if (problem.getDeletedAt() != null)
                return new Result<>(null, Result.FAIL, "problem has been deleted");

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
    @Transactional
    public Result<ProblemInfoVO> createProblem(ProblemCreateDTO problemCreateDTO, Long requesterId) {
        if (problemCreateDTO.getCreatedBy() == null)
            problemCreateDTO.setCreatedBy(requesterId);
        else{
            //检查这个作者是否存在
            userRepository.findById(problemCreateDTO.getCreatedBy())
                    .orElseThrow(() -> new BusinessException("这个作者不存在"));
        }


        Problem problem = problemRepository.save(new Problem(problemCreateDTO));
        if (problem.getId() == null)
            throw new BusinessException("Problem has not been created...");


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

        //file (git 分支 separation 分离这个方法为创建题目 + 单独上传测试文件)
        //文件名以题目名字+测试点名字.in拼接而成
//        List<ProblemFileDTO> problemFiles = problemCreateDTO.getProblemFiles();
//        index = 0;
//        for (ProblemFileDTO problemFileDTO : problemFiles) {
//            index++;
//            Result<Void> fileResult = uploadFile(problemFileDTO, problem, index);
//
//            if (fileResult.getCode() == Result.FAIL) {
//                sb.append("TestPoint ").append(index).append("FAILED, Reason: ").append(fileResult.getMsg()).append("\n");
//                throw new BusinessException("文件上传失败: " + sb);
//            }else
//                sb.append("TestPoint ").append(index).append("SUCCESS\n");
//        }

        //TAGS FIXME:这里估计会有bug 记得修复一下
        List<ProblemTagDTO> tags = problemCreateDTO.getProblemTags();
        for (ProblemTagDTO tag : tags) {
            ProblemTag pt = new ProblemTag();
            pt.setId(new ProblemTagId(problemId, tag.getTagId()));
            problemTagRepository.save(pt);
        }

        return new Result<>(new ProblemInfoVO(problem),
                Result.SUCCESS,
                "ok");
    }

    @Override
    @Transactional
    public Result<List<ProblemFileVO>> createProblemFile(
            Long problemId,
            MultipartFile[] files,
            Byte[] fileTypes,
            Long requesterId
    ) {
        //检查问题文件是否存在
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isEmpty() || problem.get().getDeletedAt() != null)
            return new Result<>(null, Result.FAIL, "找不到问题记录: Can't find problem");

        //校验上传者是否是管理员或者作者
        Result<Role> checkerAdminOrCreator = checkerAdminOrCreator(problem.get(), requesterId);
        if(checkerAdminOrCreator.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, checkerAdminOrCreator.getMsg());

        List<ProblemFileVO> pfs = new ArrayList<>();

        int[] index = new int[ProblemFile.getTypeCount()];
        //获取已经上传的所有测试文件，/2得到新的索引
        //FIXME: 这里可能会影响效率
        int len = problemFileRepository.findAllByProblemId(problemId).size() >> 1;
        Arrays.fill(index, len + 1);

        for (int i = 0; i < files.length; i++) {
            Result<ProblemFile> result = uploadFile(fileTypes[i], files[i], problem.get(), index[fileTypes[i]]++);
            pfs.add(new ProblemFileVO(result.getObj().getFilename(), result.getObj().getFileType(), result.getCode(), result.getMsg()));
        }

        return new Result<>(pfs, Result.SUCCESS, "please check pfs details");
    }

    @Override
    @Transactional
    public Result<Void> removeProblem(Long problemId, Long requesterId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new BusinessException("题目不存在"));

        Result<Role> checkerAdminOrCreator = checkerAdminOrCreator(problem, requesterId);
        if(checkerAdminOrCreator.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, checkerAdminOrCreator.getMsg());

        problemRepository.findById(problemId).ifPresent(pro -> pro.setDeletedAt(Instant.now()));
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<Void> deleteProblem(Long problemId, Long requesterId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new BusinessException("题目不存在"));

        Result<Role> checkerAdminOrCreator = checkerAdminOrCreator(problem, requesterId);
        if(checkerAdminOrCreator.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, checkerAdminOrCreator.getMsg());

        StringBuilder msg = new StringBuilder();

        //删除problem的同时 也要删除problemFile samples problemTags
        //problemFile
        List<ProblemFile> allProblemFiles = problemFileRepository.findAllByProblemId(problemId);
        for (ProblemFile problemFile : allProblemFiles)
            if (deleteFile(problemFile).getCode() == Result.FAIL)
                msg.append("Delete FAIL: ").append(problemFile.getProblemId()).append("\n");

        //samples
        problemSampleRepository.deleteAllByProblemId(problemId);

        //problemTags
        problemTagRepository.deleteAllById_ProblemId(problemId);

        //problem
        problemRepository.deleteById(problemId);

        return new Result<>(null, Result.SUCCESS, msg.toString());
    }


    private Result<ProblemFile> uploadFile(ProblemFileDTO problemFileDTO, Problem problem, int testPoint) {
        return uploadFile(problemFileDTO.getFileType(), problemFileDTO.getFile(), problem, testPoint);
    }

    private Result<ProblemFile> uploadFile(Byte fileType, MultipartFile file, Problem problem, int testPoint) {
        //我们将problem的名字+测试点 后缀通过fileType拼接
        String fileName = "";
        try {
            fileName = testPoint + "." + ProblemFile.parseType(fileType);
        } catch (IllegalArgumentException e) {
            return new Result<>(null, Result.FAIL, "非法 fileType: " + fileType);
        }
        if (fileName.contains("/") || fileName.contains(".."))
            return new Result<>(null, Result.FAIL, "非法的文件名");

        if (file == null || file.isEmpty())
            return new Result<>(null, Result.FAIL, "文件为空");

        //相对路径
        String relativePath = problem.getId() + "/" + fileName;

        Path target = Paths.get(storageRoot, problemPath).resolve(relativePath);

        try {
            Files.createDirectories(target.getParent());

            //Write
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }

            //MD5
            String md5 = DigestUtils.md5DigestAsHex(Files.newInputStream(target));

            //Write In mysql
            ProblemFile pf = new ProblemFile();
            pf.setProblemId(problem.getId());
            pf.setFileType(fileType);
            pf.setFileSize(file.getSize());
            pf.setFilename(fileName);
            pf.setStoragePath(relativePath);
            pf.setMd5(md5);
            ProblemFile save = problemFileRepository.save(pf);
            return new Result<>(save, Result.SUCCESS, "ok");
        } catch (IOException e) {
            return new Result<>(null, Result.FAIL, "文件保存失败 Exception:" + e.getMessage());
        }
    }

    private Result<Void> deleteFile(ProblemFile problemFile) {
        Path target = Paths.get(storageRoot, problemPath).resolve(problemFile.getStoragePath());
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.info("物理文件删除失败: {}", target, e);
//            return new Result<>(null, Result.FAIL, "File delete failed");
        }
        problemFileRepository.delete(problemFile);
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    /**
     * 检查一个角色是否是admin或creator
     * @param problem 问题
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Role> checkerAdminOrCreator(Problem problem, Long requesterId) {
        //只有管理员才能能添加新的角色
        Optional<UserRole> requester = userRoleRepository.findById_UserId(requesterId);
        if (requester.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到您的信息: requester is empty");

        if (!Objects.equals(requesterId, problem.getCreatedBy()))
            if (!requester.get().getRole().getName().equals(Role.ROLE_ADMIN))
                return new Result<>(null, Result.FAIL, "您不是管理员或者作者");

        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
