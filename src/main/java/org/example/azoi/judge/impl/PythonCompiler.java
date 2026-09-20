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
public class PythonCompiler extends AbstractCompiler {

    private static final Logger log = LoggerFactory.getLogger(PythonCompiler.class);

    @Override
    public String supportedLanguage() {
        return "py";
    }

    @Override
    protected List<String> buildCompileCommand(Path source, Path output) {
        // Python 无需编译
        return List.of();
    }

    @Override
    protected Path getOutputPath(Path source) {
        return source;
    }
}