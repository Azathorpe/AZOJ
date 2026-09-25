package org.example.azoi.judge;

import com.alibaba.fastjson.JSON;
import jakarta.persistence.PrePersist;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.ResultC;
import org.example.azoi.dto.submittransmit.JudgePointVO;
import org.example.azoi.dto.submittransmit.TestPoint;
import org.example.azoi.judge.impl.CompilerFactory;
import org.example.azoi.model.Submission;
import org.example.azoi.model.problem_model.Problem;
import org.example.azoi.model.problem_model.ProblemFile;
import org.example.azoi.utils.LangParser;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.exception.CompileException;
import org.example.azoi.utils.repository.ProblemFileRepository;
import org.example.azoi.utils.repository.ProblemRepository;
import org.example.azoi.utils.repository.SubmissionRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Service
public class JudgeService {
    @Value("${azoi.storage.root}")
    private String rootPath;
    @Value("${azoi.storage.compile-dir}")
    private String compileDir;
    @Value("${azoi.storage.problem-dir}")
    private String problemDir;
    @Value("${azoi.storage.submit-dir}")
    private String submitDir;
    @Value("${azoi.judge.runningOnSandBox}")
    private boolean runningOnSandBox;

    private static final Logger log = LoggerFactory.getLogger(JudgeService.class);

    private final ProblemFileRepository problemFileRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final CompilerFactory compilerFactory;
    private final SandboxService sandboxService;

    public JudgeService(ProblemFileRepository problemFileRepository, SubmissionRepository submissionRepository, UserRepository userRepository, ProblemRepository problemRepository, CompilerFactory compilerFactory, SandboxService sandboxService) {
        this.problemFileRepository = problemFileRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.compilerFactory = compilerFactory;
        this.sandboxService = sandboxService;
    }

    public void onCreated() {
        //在实例化这个Service之前，就创建好编译文件的文件夹
        Path path = Paths.get(rootPath, compileDir);
        try {
            if (!Files.exists(path))
                Files.createDirectory(path);
        } catch (IOException e) {
            throw new BusinessException("编译文件夹创建失败: " + e);
        }
    }

    @Transactional
    public void judge(Long submissionId) {
        if (runningOnSandBox)
            judge_sandbox(submissionId);
        else
            judge_local(submissionId);
    }

    private void judge_local(Long submissionId) {
        log.info("We are running on Local mode...");


        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("Submission 不存在: " + submissionId));

        Problem problem = problemRepository.findById(submission.getProblemId())
                .orElseThrow(() -> new BusinessException("Problem 不存在: " + submission.getProblemId()));

        // 1. 标记为 Judging
        submission.setStatus(Submission.STATUS_JUDGING);
        submissionRepository.save(submission);

        try {
            //Create User Compile Path
            Path path = Paths.get(rootPath, compileDir, submission.getUserId().toString(), submission.getProblemId().toString());
            if (!Files.exists(path)) {
                log.info("{} 不存在，正在创建", path);
                Files.createDirectories(path);
            }
            //获取所有测试例
            List<ProblemFile> testFiles = problemFileRepository
                    .findAllByProblemId(submission.getProblemId());

            log.info("一共找到了{}个测试文件", testFiles.size());
            List<ProblemFile> testList = testFiles.
                    stream()
                    .filter((t) -> t.getFileType() == ProblemFile.FILE_TYPE_IN)
                    .toList();
            List<ProblemFile> ansList = testFiles
                    .stream()
                    .filter((t) -> t.getFileType() == ProblemFile.FILE_TYPE_OUT)
                    .toList();

            TestPoint[] tp = new TestPoint[testList.size()];
            JudgePointVO[] jp = new JudgePointVO[testList.size()];
            LinkedHashMap<Byte, Integer> statusPQ = new LinkedHashMap<>();

            int pass = 0;

            //编译 并且获得编译出的结果
            Compiler compiler = compilerFactory.get(submission.getLanguage());

            ResultC compiledPath;
            if (submission.getCode() == null)
                compiledPath = compiler.compile(submission.getAnswerFilePath(), submission.getUserId());
            else {
                //如果是存在数据库里面，那我们就先写到用户文件夹的根下，编译完就丢掉
                Path userFolder = Paths.get(rootPath, compileDir, submission.getUserId().toString());
                if (!Files.exists(userFolder))
                    Files.createDirectories(userFolder);
                userFolder = userFolder.resolve("Main." + LangParser.toExtension(submission.getLanguage()));

                //把数据库内的文件写下来
                String code = submission.getCode();
                try (FileWriter fw = new FileWriter(userFolder.toFile())) {
                    fw.write(code);
                }

                compiledPath = compiler.compile(String.valueOf(userFolder), submission.getUserId());
            }

            if (compiledPath.getStatus() == Submission.STATUS_OK) {
                //运行
                log.info("共有: {} 个文件等待测试.", tp.length);
                for (int i = 0; i < testList.size(); i++) {
                    String input = testList.get(i).getStoragePath(), answer = ansList.get(i).getStoragePath();
                    log.info("当前测试文件名: {}, 答案文件名: {}", input, answer);
                    //使用流输入读取文件
                    ResultC out = compiler.run(compiledPath.getAns(), input);

                    if (out.getStatus() != Submission.STATUS_OK) {
                        log.info("out is FAIL, Reason: {}", Submission.parseStatus(out.getStatus()));
                        tp[i] = new TestPoint(out.getStatus(), out.getLog());
                        jp[i] = new JudgePointVO(i, out.getStatus(), problem.getTimeLimit(), problem.getMemoryLimit(), out.getLog());
                        if (statusPQ.containsKey(out.getStatus()))
                            statusPQ.put(out.getStatus(), statusPQ.get(out.getStatus()) + 1);
                        else
                            statusPQ.put(out.getStatus(), 1);
                        continue;
                    }

                    String myAnswer = out.getAns();
                    Path standardAnswerPath = Paths.get(rootPath, problemDir).resolve(answer);
                    String standardAnswer = Files.readString(standardAnswerPath);

                    //判断答案是否正确
                    if (myAnswer.trim().equals(standardAnswer.trim())) {
                        tp[i] = new TestPoint(Submission.STATUS_AC, "");
                        jp[i] = new JudgePointVO(i, Submission.STATUS_AC, problem.getTimeLimit(), problem.getMemoryLimit(), "AC");
                        pass++;

                        if (statusPQ.containsKey(Submission.STATUS_AC))
                            statusPQ.put(Submission.STATUS_AC, statusPQ.get(Submission.STATUS_AC) + 1);
                        else
                            statusPQ.put(Submission.STATUS_AC, 1);
                    } else {
                        tp[i] = new TestPoint(out.getStatus(), out.getLog());
                        jp[i] = new JudgePointVO(i, Submission.STATUS_WA, problem.getTimeLimit(), problem.getMemoryLimit(), "WA");

                        if (statusPQ.containsKey(Submission.STATUS_WA))
                            statusPQ.put(Submission.STATUS_WA, statusPQ.get(Submission.STATUS_WA) + 1);
                        else
                            statusPQ.put(Submission.STATUS_WA, 1);
                    }
                }
            } else {
                log.info("编译出错: {}", compiledPath.getLog());
            }

            // 3. 回写结果
            if (pass != tp.length) {
                //这个状态是最大的状态，我希望让整体代替剩余的，所以我们使用HashMap
                submission.setStatus(statusPQ.entrySet().iterator().next().getKey());
//                submission.setStatus(Submission.STATUS_WA);
            } else {
                submission.setStatus(Submission.STATUS_AC);
                //将user的通过次数+1
                userRepository
                        .findById(submission.getUserId())
                        .ifPresent(user -> user.setSolvedCount(user.getSolvedCount() + 1));
                //这个题也要+1
                problemRepository
                        .findById(submission.getProblemId())
                        .ifPresent(pro -> pro.setAcceptedCount(pro.getAcceptedCount() + 1));
            }

            //如果编译出错了，那就CE
            if (compiledPath.getStatus() != Submission.STATUS_OK) {
                submission.setStatus(compiledPath.getStatus());
                for(int i = 0;i < testList.size();i++)
                    jp[i] = new JudgePointVO(i, Submission.STATUS_CE, problem.getTimeLimit(), problem.getMemoryLimit(), compiledPath.getLog());
            }

            submission.setScore((int) Math.round(100.0 * pass / tp.length));
            submission.setTimeUsed(45);
            submission.setMemoryUsed(2048);

            submission.setJudgedAt(Instant.now());
            //JudgeLog是每个测试点Log的List

            submission.setJudgeLog(JSON.toJSONString(jp));

        } catch (Exception e) {
            log.error("判题失败: submissionId={}", submissionId, e);
            submission.setStatus(Submission.STATUS_UKE);   // Unknown Error
            submission.setJudgedAt(Instant.now());
        }

        submissionRepository.save(submission);
        log.info("判题完成: submissionId={}, status={}", submissionId, submission.getStatus());
    }

    private void judge_sandbox(Long submissionId) {
        log.info("We are running on Sandbox mode...");

        //检查问题是否存在
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("未找到提交记录"));
        Problem problem = problemRepository.findById(submission.getProblemId())
                .orElseThrow(() -> new BusinessException("未找到问题记录"));

        Path testFileRootPath = Paths.get(rootPath, problemDir);

        //获取所有测试文件
        List<ProblemFile> testFile = problemFileRepository.findByProblemId(problem.getId());

        List<ProblemFile> inputList = testFile
                .stream()
                .filter(f -> f.getFileType() == ProblemFile.FILE_TYPE_IN)
                .toList();

        List<ProblemFile> outputList = testFile
                .stream()
                .filter(f -> f.getFileType() == ProblemFile.FILE_TYPE_OUT)
                .toList();

        List<String> inputs = inputList.stream()
                .map(f -> {
                    try {
                        return Files.readString(testFileRootPath.resolve(f.getStoragePath()));
                    } catch (IOException e) {
                        throw new BusinessException("读取文件失败: " + e);
                    }
                })
                .toList();

        // 2. 调沙盒执行
        List<SandboxService.SandboxResult> results = sandboxService.compileAndRun(
                submission.getCode(),
                submission.getLanguage(),
                inputs,
                problem.getTimeLimit(),
                problem.getMemoryLimit() * 1024  // MB → KB
        );

        for (SandboxService.SandboxResult result : results) {
            System.out.println(result.toString());
        }

        submissionRepository.save(submission);
    }
}