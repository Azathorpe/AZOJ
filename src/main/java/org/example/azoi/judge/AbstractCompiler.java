package org.example.azoi.judge;

import org.example.azoi.dto.Result;
import org.example.azoi.model.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.example.azoi.utils.exception.*;

public abstract class AbstractCompiler implements Compiler {

    private static final Logger log = LoggerFactory.getLogger(AbstractCompiler.class);

    @Value("${azoi.storage.problem-dir}")
    protected String problemDir;
    @Value("${azoi.compiler.timeout-seconds}")
    protected int timeout;
    @Value("${azoi.storage.root}")
    protected String rootPath;
    @Value("${azoi.storage.submit-dir}")
    protected String submitPath;
    @Value("${azoi.storage.compile-dir}")
    protected String compilePath;

    @Value("${azoi.compiler.timeout-seconds:10}")
    protected int compileTimeoutSeconds;

    /**
     * 子类实现：返回编译命令
     */
    protected abstract List<String> buildCompileCommand(Path source, Path output);

    /**
     * 子类实现：返回编译后产物的路径（相对 storageRoot）
     * 大多数语言是 output，Python 可能返回源文件本身
     */
    protected abstract Path getOutputPath(Path source);

    @Override
    public Result<String> run(String outputFile, String input) {
        Path inputPath = Paths.get(rootPath, problemDir, input);
        Path programPath = Paths.get(rootPath, outputFile);
        Path ans = programPath.getParent().getParent().resolve(input + ".ans");

        ProcessBuilder pb;
        if(outputFile.endsWith(".py"))
            pb = new ProcessBuilder("python3" ,String.valueOf(programPath));
        else
            pb = new ProcessBuilder(String.valueOf(programPath));

        pb.redirectInput(inputPath.toFile());
        pb.redirectOutput(ans.toFile());

        log.info("File was running.. The answer Path:{}, The input Path:{}, The Compiled File:{}", ans, inputPath, programPath);

        Process p = null;
        try {
            p = pb.start();
            boolean finished = p.waitFor(timeout, TimeUnit.MILLISECONDS);
            if (!finished) {
                p.descendants().forEach(ProcessHandle::destroyForcibly);
                p.destroyForcibly();
                p.waitFor();
                throw new BusinessException("时间超限");
            }

            log.info("running success");
            int exitCode = p.exitValue();
            if (exitCode != 0) {
                throw new BusinessException("运行时错误，退出码 " + exitCode);
            }
            return new Result<>(Files.readString(ans), Result.SUCCESS, "ok");

        } catch (IOException | InterruptedException e) {
            throw new BusinessException("运行时出错: " + e);
        }
    }

    @Override
    public String compile(String sourceFile, String folderName) {
        Path source = Paths.get(rootPath, submitPath).resolve(sourceFile);
        if (!Files.exists(source)) {
            throw new CompileException("源文件不存在: " + sourceFile);
        }

        Path outputPath = Paths.get(rootPath, compilePath).resolve(sourceFile);
        Path output = getOutputPath(outputPath);

        List<String> cmd = buildCompileCommand(source, output);
        if (cmd == null || cmd.isEmpty()) {
            // Python 等解释型语言，不需要编译 但是需要复制到compiled
            output = output.getParent().resolve("python.out.py");
            try {
                Files.copy(source, output, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return relativize(output);
        }

        log.info("编译: {}", String.join(" ", cmd));
        runProcess(cmd);

        if (!Files.exists(output)) {
            throw new CompileException("编译产物不存在: " + output);
        }

        return relativize(output);
    }

    protected void runProcess(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();

            // 读输出，防止缓冲区满导致进程阻塞
            String output = new String(p.getInputStream().readAllBytes());
            boolean finished = p.waitFor(compileTimeoutSeconds, TimeUnit.SECONDS);

            if (!finished) {
                p.destroyForcibly();
                throw new CompileException("编译超时（超过 " + compileTimeoutSeconds + " 秒）");
            }

            int exitCode = p.exitValue();
            if (exitCode != 0) {
                throw new CompileException("编译失败，退出码 " + exitCode + ":\n" + output);
            }
        } catch (IOException | InterruptedException e) {
            throw new CompileException("编译异常: " + e.getMessage(), e);
        }
    }

    protected String relativize(Path absolute) {
        return Paths.get(rootPath).relativize(absolute).toString();
    }
}