package org.example.azoi.judge.impl;

import org.example.azoi.judge.Compiler;
import org.example.azoi.utils.LangParser;
import org.example.azoi.utils.exception.CompileException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CompilerFactory {

    private final Map<String, Compiler> compilerMap = new HashMap<>();

    public CompilerFactory(List<Compiler> compilers) {
        for (Compiler c : compilers) {
            compilerMap.put(c.supportedLanguage(), c);
        }
    }

    public Compiler get(String language) {
        Compiler c = compilerMap.get(LangParser.toExtension(language));
        if (c == null) {
            throw new CompileException("不支持的语言: " + language);
        }
        return c;
    }
}