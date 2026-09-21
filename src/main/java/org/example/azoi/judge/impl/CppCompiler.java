package org.example.azoi.judge.impl;

import org.example.azoi.judge.AbstractCompiler;
import org.example.azoi.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CppCompiler extends AbstractCompiler {

    private static final Logger log = LoggerFactory.getLogger(CppCompiler.class);

    @Override
    public String supportedLanguage() {
        return "cpp";
    }

    @Override
    protected List<String> buildCompileCommand(Path source, Path output) {
        return List.of("g++", "-O2", "-std=c++17",
                "-o", output.toString(),
                source.toString());
    }

    @Override
    protected Path getOutputPath(Path source) {
        return source.resolveSibling("a.out");
    }
}