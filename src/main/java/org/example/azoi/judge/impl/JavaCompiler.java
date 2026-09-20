package org.example.azoi.judge.impl;

import org.example.azoi.judge.AbstractCompiler;
import org.example.azoi.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
}