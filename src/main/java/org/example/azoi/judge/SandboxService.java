package org.example.azoi.judge;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class SandboxService {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    /** isolate 可执行文件路径 */
    @Value("${azoi.judge.isolate-bin:/usr/local/bin/isolate}")
    private String isolateBin;

    /** 判题临时目录根路径 */
    @Value("${azoi.judge.tmp-root:/var/azoi-judge}")
    private String tmpRoot;

    /** 调试开关：保留临时目录 */
    @Value("${azoi.judge.keep-sandbox-files:false}")
    private boolean keepSandboxFiles;

    /** 并发 box id 分配 */
    private final AtomicInteger boxCounter = new AtomicInteger(0);
    private static final int MAX_BOXES = 64;

    /** 编译时间限制（秒） */
    private static final double COMPILE_TIME_LIMIT = 15.0;
    /** 编译内存限制（KB），512MB */
    private static final int COMPILE_MEMORY_LIMIT = 524288;

    private Path tmpRootPath;

    // ==================== 初始化 ====================

    @PostConstruct
    public void init() {
        tmpRootPath = Paths.get(tmpRoot);
        try {
            if (!Files.exists(tmpRootPath)) {
                Files.createDirectories(tmpRootPath);
                log.info("创建判题临时根目录: {}", tmpRootPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建判题临时根目录失败: " + tmpRootPath, e);
        }

        // 用 chmod 设 1777（粘滞位），PosixFilePermissions 不支持粘滞位
        if (!setPermissionsWithChmod(tmpRootPath.toString(), "1777")) {
            // chmod 失败，降级用 Java API 设 777
            try {
                Files.setPosixFilePermissions(tmpRootPath,
                        PosixFilePermissions.fromString("rwxrwxrwx"));
                log.warn("chmod 1777 失败，已降级为 777: {}", tmpRootPath);
            } catch (UnsupportedOperationException | IOException e) {
                log.warn("设置临时根目录权限失败: {}", tmpRootPath, e);
            }
        }

        log.info("判题临时根目录初始化完成: {}", tmpRootPath);
    }

    /**
     * 用 chmod 命令设权限（支持粘滞位等特殊位）
     */
    private boolean setPermissionsWithChmod(String path, String mode) {
        try {
            Process p = new ProcessBuilder("chmod", mode, path)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = p.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            log.debug("chmod {} {} 失败: {}", mode, path, e.getMessage());
            return false;
        }
    }

    /**
     * 每天凌晨 3 点清理超过 24 小时的临时目录
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldTempDirs() {
        if (tmpRootPath == null || !Files.exists(tmpRootPath)) return;

        long cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L;

        try (Stream<Path> dirs = Files.list(tmpRootPath)) {
            dirs.filter(Files::isDirectory)
                    .filter(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis() < cutoff;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(p -> {
                        log.info("清理过期临时目录: {}", p);
                        deleteRecursively(p);
                    });
        } catch (IOException e) {
            log.warn("清理临时目录失败", e);
        }
    }

    // ==================== 主流程 ====================

    public List<SandboxResult> compileAndRun(
            String sourceCode,
            String language,
            List<String> inputs,
            int timeLimit,
            int memoryLimit) {

        List<SandboxResult> results = new ArrayList<>();
        Path hostDir = null;
        int boxId = acquireBoxId();
        boolean boxInited = false;

        try {
            // 1. 初始化沙盒（先 cleanup 防残留）
            initIsolate(boxId);
            boxInited = true;

            // 2. 准备宿主机临时目录
            hostDir = createHostDir();

            // 3. 写源代码
            String sourceFileName = getSourceFileName(language);
            Files.writeString(hostDir.resolve(sourceFileName), sourceCode, StandardCharsets.UTF_8);

            // 4. 编译
            SandboxResult compileResult = compile(boxId, hostDir, sourceFileName, language);
            if (compileResult.getStatus() != SandboxStatus.SUCCESS) {
                results.add(compileResult);
                return results;
            }

            // 5. 逐个测试点运行
            for (int i = 0; i < inputs.size(); i++) {
                SandboxResult runResult = runTestCase(
                        boxId, hostDir, sourceFileName, language,
                        inputs.get(i), i + 1,
                        timeLimit, memoryLimit);
                results.add(runResult);

                if (runResult.getStatus() == SandboxStatus.TIME_LIMIT_EXCEEDED
                        || runResult.getStatus() == SandboxStatus.MEMORY_LIMIT_EXCEEDED
                        || runResult.getStatus() == SandboxStatus.RUNTIME_ERROR) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error("沙盒执行异常", e);
            SandboxResult error = new SandboxResult();
            error.setStatus(SandboxStatus.SYSTEM_ERROR);
            error.setMessage(e.getMessage());
            results.add(error);
        } finally {
            if (boxInited) {
                cleanupIsolate(boxId);
            }
            if (hostDir != null && !keepSandboxFiles) {
                deleteRecursively(hostDir);
            } else if (hostDir != null) {
                log.info("保留临时目录供调试: {}", hostDir);
            }
        }

        return results;
    }

    // ==================== 沙盒生命周期 ====================

    private int acquireBoxId() {
        return boxCounter.getAndIncrement() % MAX_BOXES;
    }

    private void initIsolate(int boxId) throws IOException, InterruptedException {
        // 先尝试清理，忽略错误
        try {
            Process cleanup = new ProcessBuilder(isolateBin, "--cleanup", "--cg", "-b", String.valueOf(boxId))
                    .redirectErrorStream(true)
                    .start();
            cleanup.waitFor(5, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }

        Process process = new ProcessBuilder(isolateBin, "--init", "--cg", "-b", String.valueOf(boxId))
                .redirectErrorStream(true)
                .start();

        boolean finished = process.waitFor(10, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            process.waitFor();
            throw new RuntimeException("isolate --init 超时 (box=" + boxId + ")");
        }

        String output = readStream(process.getInputStream()).trim();
        int exitCode = process.exitValue();

        if (exitCode != 0) {
            throw new RuntimeException("isolate --init 失败 (box=" + boxId + ", exit=" + exitCode + "): " + output);
        }

        Path boxRoot = Paths.get(output);
        if (!Files.exists(boxRoot)) {
            throw new RuntimeException("isolate --init 返回的目录不存在: " + output);
        }

        log.debug("沙盒初始化成功: box={}, dir={}", boxId, boxRoot);
    }

    private void cleanupIsolate(int boxId) {
        try {
            Process process = new ProcessBuilder(isolateBin, "--cleanup", "--cg", "-b", String.valueOf(boxId))
                    .redirectErrorStream(true)
                    .start();
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("isolate --cleanup 失败 (box={})", boxId, e);
        }
    }

    // ==================== 临时目录 ====================

    private Path createHostDir() throws IOException {
        Path dir = Files.createTempDirectory(tmpRootPath, "judge-");
        try {
            Files.setPosixFilePermissions(dir,
                    PosixFilePermissions.fromString("rwxrwxrwx"));
        } catch (UnsupportedOperationException | IOException e) {
            log.warn("设置临时目录权限失败: {}", dir, e);
        }
        return dir;
    }

    // ==================== 编译 ====================

    private SandboxResult compile(int boxId, Path hostDir, String sourceFileName, String language)
            throws IOException, InterruptedException {

        List<String> compileCmd = getCompileCommand(language, sourceFileName, hostDir);
        if (compileCmd.isEmpty()) {
            SandboxResult result = new SandboxResult();
            result.setStatus(SandboxStatus.SUCCESS);
            return result;
        }

        Path metaFile = hostDir.resolve("compile.meta");

        List<String> cmd = buildIsolateCommand(
                boxId, hostDir, metaFile,
                COMPILE_TIME_LIMIT, COMPILE_MEMORY_LIMIT,
                compileCmd
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        boolean finished = process.waitFor((long) COMPILE_TIME_LIMIT + 10, TimeUnit.SECONDS);
        String output = readStream(process.getInputStream());

        if (!finished) {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            process.waitFor();
            SandboxResult result = new SandboxResult();
            result.setStatus(SandboxStatus.COMPILE_ERROR);
            result.setMessage("编译超时");
            return result;
        }

        int exitCode = process.exitValue();
        SandboxMeta meta = parseMetaFile(metaFile);

        SandboxResult result = new SandboxResult();
        result.setTime(meta.time);
        result.setMemory(meta.memory);

        if (exitCode == 0) {
            result.setStatus(SandboxStatus.SUCCESS);
        } else {
            result.setStatus(SandboxStatus.COMPILE_ERROR);
            result.setMessage(output.isEmpty() ? "编译失败" : output);
        }
        log.info("isolate 编译命令: {}", String.join(" ", cmd));
        return result;
    }

    private List<String> getCompileCommand(String language, String sourceFileName, Path hostDir) {
        String src = hostDir.resolve(sourceFileName).toString();
        String out = hostDir.resolve("Main").toString();
        return switch (language.toLowerCase()) {
            case "cpp", "c++" -> List.of("/usr/bin/g++", "-O2", "-std=c++17", "-o", out, src);
            case "c" -> List.of("/usr/bin/gcc", "-O2", "-o", out, src);
            case "java" -> List.of("/usr/bin/javac", "-encoding", "UTF-8",
                    "-d", hostDir.toString(), src);
            case "go" -> List.of("/usr/bin/go", "build", "-o", out, src);
            case "py", "python" -> List.of();
            default -> List.of();
        };
    }

    // ==================== 运行测试点 ====================

    private SandboxResult runTestCase(
            int boxId, Path hostDir, String sourceFileName, String language,
            String input, int testPoint,
            int timeLimit, int memoryLimit) throws IOException, InterruptedException {

        String inputName = "input" + testPoint + ".txt";
        String outputName = "output" + testPoint + ".txt";
        String errorName = "error" + testPoint + ".txt";
        String metaName = "run" + testPoint + ".meta";

        Files.writeString(hostDir.resolve(inputName), input, StandardCharsets.UTF_8);

        Path metaFile = hostDir.resolve(metaName);

        List<String> runCmd = getRunCommand(language, sourceFileName, hostDir);

        List<String> cmd = new ArrayList<>();
        cmd.add(isolateBin);
        cmd.add("--run");
        cmd.add("--cg");
        cmd.add("-b"); cmd.add(String.valueOf(boxId));
        cmd.add("-M"); cmd.add(metaFile.toString());
        cmd.add("-t"); cmd.add(String.format("%.3f", timeLimit / 1000.0));
        cmd.add("--cg-mem=" + memoryLimit);
        cmd.add("-i"); cmd.add(hostDir.resolve(inputName).toString());
        cmd.add("-o"); cmd.add(hostDir.resolve(outputName).toString());
        cmd.add("-r"); cmd.add(hostDir.resolve(errorName).toString());
        cmd.add("--processes=1");    // 改前：cmd.add("-p"); cmd.add("1");
        cmd.add("-E"); cmd.add("PATH=/usr/bin:/bin");
        cmd.add("--dir=" + hostDir + ":rw");
        cmd.add("--");
        cmd.addAll(runCmd);
        log.info("isolate 编译命令: {}", String.join(" ", cmd));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        long wallTimeout = timeLimit + 5000L;
        boolean finished = process.waitFor(wallTimeout, TimeUnit.MILLISECONDS);

        if (!finished) {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            process.waitFor();
        }

        int exitCode = process.exitValue();
        SandboxMeta meta = parseMetaFile(metaFile);

        SandboxResult result = new SandboxResult();
        result.setTestPoint(testPoint);
        result.setTime(meta.time);
        result.setMemory(meta.memory);
        result.setExitCode(exitCode);

        if (meta.status != null) {
            switch (meta.status) {
                case "TO" -> {
                    result.setStatus(SandboxStatus.TIME_LIMIT_EXCEEDED);
                    return result;
                }
                case "ML" -> {
                    result.setStatus(SandboxStatus.MEMORY_LIMIT_EXCEEDED);
                    return result;
                }
                case "RE" -> {
                    result.setStatus(SandboxStatus.RUNTIME_ERROR);
                    result.setMessage(readFileIfExists(hostDir.resolve(errorName)));
                    return result;
                }
                case "SG" -> {
                    result.setStatus(SandboxStatus.RUNTIME_ERROR);
                    result.setMessage("信号终止");
                    return result;
                }
                default -> { }
            }
        }

        if (meta.time != null && meta.time >= timeLimit) {
            result.setStatus(SandboxStatus.TIME_LIMIT_EXCEEDED);
        } else if (meta.memory != null && meta.memory >= memoryLimit) {
            result.setStatus(SandboxStatus.MEMORY_LIMIT_EXCEEDED);
        } else if (exitCode != 0) {
            result.setStatus(SandboxStatus.RUNTIME_ERROR);
            result.setMessage(readFileIfExists(hostDir.resolve(errorName)));
        } else {
            result.setStatus(SandboxStatus.SUCCESS);
            result.setOutput(readFileIfExists(hostDir.resolve(outputName)));
        }

        return result;
    }

    private List<String> getRunCommand(String language, String sourceFileName, Path hostDir) {
        String main = hostDir.resolve("Main").toString();
        String py = hostDir.resolve(sourceFileName).toString();
        return switch (language.toLowerCase()) {
            case "cpp", "c++", "c", "go" -> List.of(main);
            case "java" -> List.of("/usr/bin/java", "-cp", hostDir.toString(), "Main");
            case "py", "python" -> List.of("/usr/bin/python3", py);
            default -> List.of(main);
        };
    }

    // ==================== 辅助方法 ====================

    private List<String> buildIsolateCommand(
            int boxId, Path hostDir, Path metaFile,
            double timeLimit, int memoryLimit,
            List<String> innerCmd) {

        List<String> cmd = new ArrayList<>();
        cmd.add(isolateBin);
        cmd.add("--run");
        cmd.add("--cg");
        cmd.add("-b"); cmd.add(String.valueOf(boxId));
        cmd.add("-M"); cmd.add(metaFile.toString());
        cmd.add("-t"); cmd.add(String.format("%.3f", timeLimit));
        cmd.add("--cg-mem=" + memoryLimit);
//        cmd.add("--processes=1");
        cmd.add("-E"); cmd.add("PATH=/usr/bin:/bin");
        cmd.add("--dir=" + hostDir + ":rw");
        cmd.add("--");
        cmd.addAll(innerCmd);
        return cmd;
    }

    private String getSourceFileName(String language) {
        return switch (language.toLowerCase()) {
            case "cpp", "c++" -> "Main.cpp";
            case "c" -> "Main.c";
            case "java" -> "Main.java";
            case "py", "python" -> "Main.py";
            case "go" -> "Main.go";
            default -> "Main.txt";
        };
    }

    private SandboxMeta parseMetaFile(Path metaFile) {
        SandboxMeta meta = new SandboxMeta();
        if (!Files.exists(metaFile)) return meta;

        try {
            for (String line : Files.readAllLines(metaFile)) {
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "time" -> meta.time = (int) (Double.parseDouble(value) * 1000);
                    case "max-rss", "cg-mem" -> meta.memory = Integer.parseInt(value);
                    case "exitcode" -> meta.exitCode = Integer.parseInt(value);
                    case "status" -> meta.status = value;
                }
            }
        } catch (Exception e) {
            log.warn("解析元数据失败: {}", metaFile, e);
        }
        return meta;
    }

    private String readFileIfExists(Path path) {
        try {
            return Files.exists(path) ? Files.readString(path) : "";
        } catch (IOException e) {
            return "";
        }
    }

    private String readStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private void deleteRecursively(Path dir) {
        try {
            if (!Files.exists(dir)) return;
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        } catch (IOException e) {
            log.warn("删除临时目录失败: {}", dir, e);
        }
    }

    // ==================== 内部类 ====================

    private static class SandboxMeta {
        Integer time;
        Integer memory;
        Integer exitCode;
        String status;
    }

    public static class SandboxResult {
        private int testPoint;
        private SandboxStatus status;
        private Integer time;
        private Integer memory;
        private Integer exitCode;
        private String output;
        private String message;

        @Override
        public String toString() {
            return "SandboxResult{" +
                    "testPoint=" + testPoint +
                    ", status=" + status +
                    ", time=" + time +
                    ", memory=" + memory +
                    ", exitCode=" + exitCode +
                    ", output='" + output + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }

        public int getTestPoint() { return testPoint; }
        public void setTestPoint(int testPoint) { this.testPoint = testPoint; }
        public SandboxStatus getStatus() { return status; }
        public void setStatus(SandboxStatus status) { this.status = status; }
        public Integer getTime() { return time; }
        public void setTime(Integer time) { this.time = time; }
        public Integer getMemory() { return memory; }
        public void setMemory(Integer memory) { this.memory = memory; }
        public Integer getExitCode() { return exitCode; }
        public void setExitCode(Integer exitCode) { this.exitCode = exitCode; }
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public enum SandboxStatus {
        SUCCESS,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        RUNTIME_ERROR,
        COMPILE_ERROR,
        SYSTEM_ERROR
    }
}