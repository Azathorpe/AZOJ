package org.example.azoi.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.submittransmit.SubmitDTO;
import org.example.azoi.dto.submittransmit.SubmitQueryDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.example.azoi.model.Submission;
import org.example.azoi.model.problem_model.Problem;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.SubmissionService;
import org.example.azoi.utils.LangParser;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.ProblemRepository;
import org.example.azoi.utils.repository.SubmissionRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Value("${azoi.storage.root}")
    private String rootPath;
    @Value("${azoi.storage.submit-dir}")
    private String submitPath;

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, ProblemRepository problemRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Result<SubmitVO> submitCode(SubmitDTO submitDTO) {
        SubmitVO result = new SubmitVO();

        Long userId = submitDTO.getUserId();
        Long problemId = submitDTO.getProblemId();
        Long contestId = submitDTO.getContestId();
        String language = submitDTO.getLanguage();

        Submission saver = new Submission();

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new BusinessException("为找到用户，请注册或者联系管理员");
        saver.setUserId(userId);

        //检查这个Problem是否存在
        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isEmpty())
            throw new BusinessException("没有找到这道题目");
        result.setProblemTitle(problem.get().getTitle());
        saver.setProblemId(problemId);
        //TODO ：后面也许这里可以添加校验contest的
        saver.setContestId(contestId);

        if(language == null || language.isBlank())
            throw new BusinessException("语言不能为空");
        //校验language是否合法
        LangParser.normalize(language);
        saver.setLanguage(language);

        saver.setCode(submitDTO.getCode());

        saver = submissionRepository.save(saver);

        return new Result<>(result.from(saver), Result.SUCCESS, "ok");
    }

    @Override
    public Result<List<SubmitVO>> getSubmits(SubmitQueryDTO query) {
        Specification<Submission> spec = (root, query1, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(query.getUserId() != null){
                predicates.add(criteriaBuilder.equal(root.get("userId").as(Long.class), query.getUserId()));
            }

            if(query.getProblemId() != null){
                predicates.add(criteriaBuilder.equal(root.get("problemId").as(Long.class), query.getProblemId()));
            }

            if(query.getContestId() != null){
                predicates.add(criteriaBuilder.equal(root.get("contestId").as(Long.class), query.getContestId()));
            }

            if(query.getStatus() != -1){
                predicates.add(criteriaBuilder.equal(root.get("status").as(Byte.class), query.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                Math.max(query.getPage() - 1, 0),
                query.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return new Result<>(
                submissionRepository.findAll(spec, pageable).stream().map(SubmitVO::new).toList(),
                Result.SUCCESS,
                "ok"
        );
    }

    @Override
    public Result<SubmitVO> getSubmit(Long submissionId) {
        Optional<Submission> submission = submissionRepository.findById(submissionId);
        return submission
                .map(value -> new Result<>(new SubmitVO(value), Result.SUCCESS, "ok"))
                .orElseGet(() -> new Result<>(null, Result.FAIL, "未找到这个提交"));
    }

    @Override
    @Transactional
    public Result<SubmitVO> submitAnswer(Long userId, Long problemId, Long contestId, String language, MultipartFile file) {
        SubmitVO result = new SubmitVO();

        Submission saver = new Submission();

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new BusinessException("为找到用户，请注册或者联系管理员");
        saver.setUserId(userId);

        //检查这个Problem是否存在
        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isEmpty())
            throw new BusinessException("没有找到这道题目");
        result.setProblemTitle(problem.get().getTitle());
        saver.setProblemId(problemId);
        //TODO ：后面也许这里可以添加校验contest的
        saver.setContestId(contestId);

        if(language == null || language.isBlank())
            throw new BusinessException("语言不能为空");
        //校验language是否合法
        LangParser.normalize(language);
        saver.setLanguage(language);

        if(file == null || file.isEmpty())
            throw new BusinessException("文件不能为空");

        saver = submissionRepository.save(saver);
        Result<Void> res = uploadFile(saver, file);
        if (res.getCode() == Result.SUCCESS)
            return new Result<>(result.from(saver), Result.SUCCESS, "ok");
        throw new BusinessException(res.getMsg());
    }

    private Result<Void> uploadFile(Submission sub, MultipartFile file) {
        //文件路径如何拼接呢？
        //root + submit/${userId}/${problemId}/${submissionId}.${filetype}

        Path filePath = Paths.get(
                Long.toString(sub.getUserId()),
                Long.toString(sub.getProblemId()));
        Path folderPath = Paths.get(
                rootPath,
                submitPath
        );

        sub.setAnswerFileSize(file.getSize());

        try {
            //创建文件夹
            Files.createDirectories(folderPath);

            //补全文件名
            //通过lang获取后缀
            String suff = LangParser.toExtension(sub.getLanguage());
            filePath = filePath.resolve(sub.getId() + "." + suff);
            sub.setAnswerFilePath(file.toString());
            filePath = folderPath.resolve(filePath);

            //Write
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            //MD5
            String md5 = DigestUtils.md5DigestAsHex(Files.newInputStream(filePath));

            //Rewrite in mysql
            sub.setAnswerMd5(md5);

            submissionRepository.save(sub);

        } catch (IOException e) {
            throw new BusinessException("文件写入错误: " + e);
        }
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
