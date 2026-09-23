package org.example.azoi.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.problemtransmit.othertransmit.PageVO;
import org.example.azoi.dto.submittransmit.SubmitDTO;
import org.example.azoi.dto.submittransmit.SubmitQueryDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.example.azoi.judge.JudgeTaskProducer;
import org.example.azoi.model.Submission;
import org.example.azoi.model.problem_model.Problem;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.SubmissionService;
import org.example.azoi.utils.LangParser;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.ProblemRepository;
import org.example.azoi.utils.repository.SubmissionRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);
    @Value("${azoi.storage.root}")
    private String rootPath;
    @Value("${azoi.storage.submit-dir}")
    private String submitPath;

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final JudgeTaskProducer judgeTaskProducer;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, ProblemRepository problemRepository, UserRepository userRepository, JudgeTaskProducer judgeTaskProducer) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.judgeTaskProducer = judgeTaskProducer;
    }

    @Override
    public Result<PageVO<SubmitVO>> getSubmits(SubmitQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Submission> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), query.getUserId()));
            }
            if (query.getProblemId() != null) {
                predicates.add(cb.equal(root.get("problemId"), query.getProblemId()));
            }
            if (query.getContestId() != null) {
                predicates.add(cb.equal(root.get("contestId"), query.getContestId()));
            }
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus().byteValue()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Submission> page = submissionRepository.findAll(spec, pageable);

        // 批量查 username 和 problemTitle
        Set<Long> userIds = page.getContent().stream().map(Submission::getUserId).collect(Collectors.toSet());
        Set<Long> problemIds = page.getContent().stream().map(Submission::getProblemId).collect(Collectors.toSet());

        Map<Long, String> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        Map<Long, String> problemMap = problemRepository.findAllById(problemIds).stream()
                .collect(Collectors.toMap(Problem::getId, Problem::getTitle));

        List<SubmitVO> content = page.getContent().stream()
                .map(s -> {
                    SubmitVO vo = new SubmitVO(s);
                    vo.setUsername(userMap.get(s.getUserId()));
                    vo.setProblemTitle(problemMap.get(s.getProblemId()));
                    return vo;
                })
                .toList();

        return Result.ok(PageVO.of(content, page.getTotalElements(),
                query.getPage(), query.getSize()));
    }

    @Override
    @Transactional
    public Result<SubmitVO> submitCode(SubmitDTO submitDTO, Long requesterId) {
        SubmitVO result = new SubmitVO();

        Long problemId = submitDTO.getProblemId();
        Long contestId = submitDTO.getContestId();
        String language = submitDTO.getLanguage();

        Submission saver = new Submission();

        Optional<User> user = userRepository.findById(requesterId);
        if (user.isEmpty())
            throw new BusinessException("为找到用户，请注册或者联系管理员");
        saver.setUserId(requesterId);

        //检查这个Problem是否存在
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isEmpty())
            throw new BusinessException("没有找到这道题目");
        result.setProblemTitle(problem.get().getTitle());
        saver.setProblemId(problemId);
        //TODO ：后面也许这里可以添加校验contest的
        saver.setContestId(contestId);

        if (language == null || language.isBlank())
            throw new BusinessException("语言不能为空");
        //校验language是否合法
        LangParser.normalize(language);
        saver.setLanguage(language);

        saver.setCode(submitDTO.getCode());

        Submission saved = submissionRepository.save(saver);

        // 事务提交后再推队列
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        judgeTaskProducer.push(saved.getId());
                    }
                }
        );
        //顺便把用户的提交次数+1
        userRepository
                .findById(saved.getUserId())
                .ifPresent(u -> u.setSubmitCount(u.getSubmitCount() + 1));
        //这个题也要+1
        problemRepository
                .findById(problemId)
                .ifPresent(pro -> pro.setSubmitCount(pro.getSubmitCount() + 1));

        return new Result<>(result.from(saved), Result.SUCCESS, "ok");
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
    public Result<SubmitVO> submitAnswer(
            Long userId,
            Long problemId,
            Long contestId,
            String language,
            MultipartFile file) {
        SubmitVO result = new SubmitVO();

        Submission saver = new Submission();

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new BusinessException("为找到用户，请注册或者联系管理员");
        saver.setUserId(userId);

        //检查这个Problem是否存在
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isEmpty())
            throw new BusinessException("没有找到这道题目");
        result.setProblemTitle(problem.get().getTitle());
        saver.setProblemId(problemId);
        //TODO ：后面也许这里可以添加校验contest的
        saver.setContestId(contestId);

        if (language == null || language.isBlank())
            throw new BusinessException("语言不能为空");
        //校验language是否合法
        LangParser.normalize(language);
        saver.setLanguage(language);

        if (file == null || file.isEmpty())
            throw new BusinessException("文件不能为空");

        Submission saved = submissionRepository.save(saver);

        Result<Void> res = uploadFile(saver, file);

        // 事务提交后再推队列
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        judgeTaskProducer.push(saved.getId());
                    }
                }
        );

        //顺便把用户的提交次数+1
        userRepository.findById(saved.getUserId())
                .ifPresent(u -> u.setSubmitCount(u.getSubmitCount() + 1));
        //这个题也要+1
        problemRepository
                .findById(problemId)
                .ifPresent(pro -> pro.setSubmitCount(pro.getSubmitCount() + 1));

        if (res.getCode() == Result.SUCCESS)
            return new Result<>(result.from(saved), Result.SUCCESS, "ok");
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
            //创建大文件夹
            Files.createDirectories(folderPath);

            //补全文件名
            //通过lang获取后缀
            String suff = LangParser.toExtension(sub.getLanguage());
            //如果是Java 那就单独创建一个Main.java吧，没办法bro,在评测完之后，再改名成${submissionId}.${filetype}
            if (LangParser.toExtension(sub.getLanguage()).equals("java"))
                filePath = filePath.resolve("Main.java");
            else
                filePath = filePath.resolve(sub.getId() + "." + suff);
            sub.setAnswerFilePath(String.valueOf(filePath));
            filePath = folderPath.resolve(filePath);

            //创建小文件夹
            Files.createDirectories(filePath.getParent());

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
