package org.example.azoi.judge.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.ResultC;
import org.example.azoi.judge.AbstractCompiler;
import org.example.azoi.model.Submission;
import org.example.azoi.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class JavaCompiler extends AbstractCompiler {

    private static final Logger log = LoggerFactory.getLogger(JavaCompiler.class);

    @Override
    public String supportedLanguage() {
        return "java";
    }

    @Override
    protected List<String> buildCompileCommand(Path source, Path output) {
        // javac Main.java -d /path/to/output
        return List.of("javac", "-d", output.getParent().toString(), source.toString());
    }

    @Override
    protected Path getOutputPath(Path source) {
        // Java 编译产物是 .class，路径是 output/Main.class
        String className = source.getFileName().toString().replace(".java", "");
        return source.resolveSibling(className + ".class");
    }

    @Override
    public ResultC run(String sourceFile, String input) {
        try {
            //拼接根路径
            Path outputPath = Paths.get(rootPath, sourceFile);
            Path inputPath = Paths.get(rootPath, problemDir, input);

            log.info("Input Path: {}, Output Path: {}", inputPath, outputPath);

            Process process = new ProcessBuilder("java", "-cp", outputPath.getParent().toString(), "Main").start();

            new Thread(() -> {
                try (OutputStream stdin = process.getOutputStream()) {
                    stdin.write(Files.readAllBytes(inputPath));
                } catch (IOException ignored) {
                }
            }).start();

            CompletableFuture<String> stdoutFuture = CompletableFuture.supplyAsync(() -> {
                try (var is = process.getInputStream()) {
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return "";
                }
            });

            CompletableFuture<String> stderrFuture = CompletableFuture.supplyAsync(() -> {
                try (var es = process.getErrorStream()) {
                    return new String(es.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return "";
                }
            });

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ResultC(null, "TLE", Submission.STATUS_TLE);
            }
            String output = stdoutFuture.get(5, TimeUnit.SECONDS);
            String errors = stderrFuture.get(5, TimeUnit.SECONDS);

            log.info("Result: {}, Errors: {}",output, errors);

            return new ResultC(output, "", Submission.STATUS_OK);
        } catch (IOException e) {
            return new ResultC("", e.getMessage(), Submission.STATUS_RE);
        } catch (InterruptedException | java.util.concurrent.ExecutionException | TimeoutException e) {
            log.error(e.toString());
            return new ResultC("", e.toString(), Submission.STATUS_TLE);
        }
    }
}