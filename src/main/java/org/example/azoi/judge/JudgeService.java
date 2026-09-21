package org.example.azoi.judge;

import jakarta.persistence.PrePersist;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.submittransmit.TestPoint;
import org.example.azoi.judge.impl.CompilerFactory;
import org.example.azoi.model.Submission;
import org.example.azoi.model.problem_model.ProblemFile;
import org.example.azoi.utils.LangParser;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.exception.CompileException;
import org.example.azoi.utils.repository.ProblemFileRepository;
import org.example.azoi.utils.repository.SubmissionRepository;
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

    private static final Logger log = LoggerFactory.getLogger(JudgeService.class);

    private final ProblemFileRepository problemFileRepository;
    private final SubmissionRepository submissionRepository;
    private final CompilerFactory compilerFactory;

    public JudgeService(ProblemFileRepository problemFileRepository, SubmissionRepository submissionRepository, CompilerFactory compilerFactory) {
        this.problemFileRepository = problemFileRepository;
        this.submissionRepository = submissionRepository;
        this.compilerFactory = compilerFactory;
    }

    public void onCreated(){
        //在实例化这个Service之前，就创建好编译文件的文件夹
        Path path = Paths.get(rootPath, compileDir);
        try {
            if(!Files.exists(path))
                Files.createDirectory(path);
        } catch (IOException e) {
            throw new BusinessException("编译文件夹创建失败: " + e);
        }
    }

    @Transactional
    public void judge(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission 不存在: " + submissionId));

        // 1. 标记为 Judging
        submission.setStatus(Submission.STATUS_JUDGING);
        submissionRepository.save(submission);

        try {
            //Create User Compile Path
            Path path = Paths.get(rootPath, compileDir, submission.getUserId().toString(), submission.getProblemId().toString());
            if(!Files.exists(path)) {
                log.info("{} 不存在，正在创建", path);
                Files.createDirectory(path);
            }
            //获取所有测试例
            ArrayList<String> testList = new ArrayList<>();
            List<ProblemFile> files = problemFileRepository.findAllByProblemIdOrderByCreatedAtAsc(submission.getProblemId());
            HashSet<String> dep = new HashSet<>();
            for (ProblemFile problemFile : files) {
                if (dep.contains(problemFile.getFilename().split("\\.")[0]))
                    testList.add(problemFile.getStoragePath().split("\\.")[0]);
                dep.add(problemFile.getFilename().split("\\.")[0]);
            }

            TestPoint[] tp = new TestPoint[testList.size()];
            int pass = 0;

            //编译 并且获得编译出的结果
            Compiler compiler = compilerFactory.get(submission.getLanguage());

            String compiledPath = "";
            if(submission.getCode() == null)
                compiledPath = compiler.compile(submission.getAnswerFilePath(), String.valueOf(submission.getUserId()));
            else {
                //如果是存在数据库里面，那我们就先写到用户文件夹的根下，编译完就丢掉
                Path userFolder = Paths.get(rootPath, compileDir, submission.getUserId().toString()).resolve("defaultPath");
                if(!Files.exists(userFolder))
                    Files.createDirectory(userFolder);
                userFolder = userFolder.resolve("main." + LangParser.toExtension(submission.getLanguage()));

                //把数据库内的文件写下来
                String code = submission.getCode();
                try(FileWriter fw = new FileWriter(userFolder.toFile())) {
                    fw.write(code);
                }

                compiledPath = compiler.compile(String.valueOf(userFolder), String.valueOf(submission.getUserId()));
            }

            //运行
            log.info("共有: {} 个文件等待测试.", tp.length);
            for (int i = 0; i < testList.size(); i++) {
                String input = testList.get(i) + ".in", output = testList.get(i) + ".out";
                log.info("当前测试文件名: {}, 答案文件名: {}", input, output);
                //使用流输入读取文件
                Result<String> out = compiler.run(compiledPath, input);
                if(out.getCode() == Result.FAIL)
                    log.info("out is FAIL, Reason: {}", Submission.parseStatus(out.getMsg()));
                log.info("out: {}", out);
                String myAnswer = out.getObj();
                Path standardAnswerPath = Paths.get(rootPath, problemDir).resolve(output);
                String standardAnswer = Files.readString(standardAnswerPath);

                if (myAnswer.trim().equals(standardAnswer.trim())) {
                    tp[i] = new TestPoint(Submission.STATUS_AC, "");
                    pass++;
                }
                else {
                    tp[i] = new TestPoint(
                            Submission.toStatus(out.getMsg()) == Submission.STATUS_JUDGING
                                    ? Submission.STATUS_WA
                                    : Submission.toStatus(out.getMsg()),
                            out.getObj());
                }
            }


            //如果是Java 那么把Java的输入文件改名为${submissionId}.java
            if(LangParser.toExtension(submission.getLanguage()).equals("java")){
                Path javaFile = Paths.get(
                        rootPath,
                        submitDir,
                        Long.toString(submission.getUserId()),
                        Long.toString(submission.getProblemId()));
                Files.move(javaFile.resolve("Main.java"),
                        javaFile.resolve(submission.getId() + ".java"));
            }

            // 3. 回写结果
            submission.setStatus(pass != tp.length ? Submission.STATUS_WA : Submission.STATUS_AC);
            submission.setScore((int) Math.round(100.0 * pass / tp.length));
            submission.setTimeUsed(45);
            submission.setMemoryUsed(2048);

            submission.setJudgedAt(Instant.now());
            submission.setJudgeLog(Arrays.toString(tp));

        } catch (Exception e) {
            log.error("判题失败: submissionId={}", submissionId, e);
            submission.setStatus(Submission.STATUS_UKE);   // Unknown Error
            submission.setJudgedAt(Instant.now());
        }

        submissionRepository.save(submission);
        log.info("判题完成: submissionId={}, status={}", submissionId, submission.getStatus());
    }
}