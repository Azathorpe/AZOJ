package org.example.azoi.judge;

import org.example.azoi.dto.ResultC;
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

    public static String COMPILE_STATUE_OK = "OK";
    public static String COMPILE_STATUE_COMPILE_ERROR = "ERROR";

    /**
     * 子类实现：返回编译命令
     */
    protected abstract List<String> buildCompileCommand(Path source, Path output);

    /**
     * 子类实现：返回编译后产物的路径（相对 storageRoot）
     * 大多数语言是 output，Python 可能返回源文件本身
     */
    protected abstract Path getOutputPath(Path source);

    /**
     * 运行的逻辑很简单
     * 不同语言的运行时不一样，共同点都在于需要源文件，所以我们还是构造一个方法，让不同语言来重写
     *
     * @param sourceFile
     * @param input
     * @return
     */
//    @Override
//    public ResultC run(String sourceFile, String input) {
//        Path inputPath = Paths.get(rootPath, problemDir, input);
//        Path programPath = Paths.get(rootPath, sourceFile);
//        Path ans = programPath.getParent().getParent().resolve(input + ".ans");
//
//        ProcessBuilder pb = new ProcessBuilder(String.valueOf(programPath));
//
//        pb.redirectInput(inputPath.toFile());
//        pb.redirectOutput(ans.toFile());
//
//        log.info("File was running.. The answer Path:{}, The input Path:{}, The Compiled File:{}", ans, inputPath, programPath);
//
//        Process p = null;
//        try {
//            p = pb.start();
//            boolean finished = p.waitFor(timeout, TimeUnit.MILLISECONDS);
//            if (!finished) {
//                p.descendants().forEach(ProcessHandle::destroyForcibly);
//                p.destroyForcibly();
//                p.waitFor();
//                return new ResultC("", "Time Limit Exceeded", Submission.STATUS_TLE);
//            }
//
//            log.info("running success");
//            int exitCode = p.exitValue();
//            if (exitCode != 0) {
//                return new ResultC("", "Runtime Error: " + exitCode, Submission.STATUS_RE);
//            }
//            return new ResultC(Files.readString(ans), "", Submission.STATUS_AC);
//
//        } catch (IOException | InterruptedException e) {
//            return new ResultC("", "Runtime Error: " + e, Submission.STATUS_RE);
//        }
//    }

    /**
     * 我们要将源文件sourceFile编译，导出到编译文件夹中/compiled/{userId}
     * 在此之前 我们要拼接源文件即: {rootPath}/{submitPath}/sourceFile
     * 接下来是导出的位置 我们目标导出位置/compiled/{userId}
     * 所以我们把source回到父文件夹(即刚好是{userId})
     * 导出到那里
     * 最后我们都知道了，那就构建编译指令，通过不同的语言，构建不同的指令
     *
     * @param sourceFile 源文件相对路径（相对 storageRoot）
     * @param userId     用户Id
     * @return
     */
    @Override
    public ResultC compile(String sourceFile, Long userId) {
        //代码文件的位置
        Path source = Paths.get(rootPath, submitPath).resolve(sourceFile);
        if (!Files.exists(source))
            return new ResultC(null, "源文件不存在", Submission.STATUS_CE);

        Path outputPath = Paths.get(rootPath, compilePath).resolve(String.valueOf(userId));
        //编译产物的位置
        Path output = getOutputPath(outputPath);
        //连接编译指令
        log.info("Compiling " + sourceFile + " to " + outputPath);
        List<String> cmd = buildCompileCommand(source, output);

        if (cmd == null || cmd.isEmpty()) {
            // Python 等解释型语言，不需要编译 但是需要复制到compiled
            output = output.getParent().resolve("python.out.py");
            try {
                Files.copy(source, output, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return new ResultC(null, "复制文件失败, 请联系管理员 错误代码: CE101", Submission.STATUS_CE);
            }
            return new ResultC(relativize(output), "", Submission.STATUS_OK);
        }

        //开始编译
        log.info("编译: {}", String.join(" ", cmd));
        runCompileProcess(cmd);

        if (!Files.exists(output)) {
            return new ResultC("", "编译产物不存在: " + output, Submission.STATUS_CE);
        }


        return new ResultC(relativize(output), "", Submission.STATUS_OK);
    }

    protected void runCompileProcess(List<String> cmd) {
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