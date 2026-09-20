package org.example.azoi.utils;

import org.example.azoi.utils.exception.BusinessException;

public class LangParser {
    public static String normalize(String language){
        return switch (language.toLowerCase()) {
            case "java" -> "java";
            case "c" -> "c";
            case "c++", "cpp" -> "cpp";
            case "py", "py3", "python" -> "py";
            default -> throw new BusinessException("不支持的语言: " + language);
        };
    }

    public static String toExtension(String language) {
        return switch (normalize(language)) {
            case "c" -> "c";
            case "cpp" -> "cpp";
            case "java" -> "java";
            case "py" -> "py";
            default -> throw new BusinessException("不支持的语言: " + language);
        };
    }
}
