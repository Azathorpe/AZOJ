package org.example.azoi.judge;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.ResultC;
import org.example.azoi.utils.exception.*;

public interface Compiler {

    /**
     * 编译源文件
     *
     * @param sourceFile 源文件相对路径（相对 storageRoot）
     * @return 编译后的可执行文件路径（相对路径）
     * @throws CompileException 编译失败
     */
    ResultC compile(String sourceFile, Long userId);

    /**
     * 判断这个编译器支持哪种语言
     *
     * @return 语言标识，如 "cpp" / "py" / "java"
     */
    String supportedLanguage();

    ResultC run(String outputFile, String input);
}