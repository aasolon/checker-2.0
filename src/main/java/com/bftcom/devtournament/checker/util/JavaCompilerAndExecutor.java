package com.bftcom.devtournament.checker.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaCompilerAndExecutor {
  private static final Logger log = LoggerFactory.getLogger(JavaCompilerAndExecutor.class);

  public static String compileAndExecute(String sourceCode, String input) {
    String uuid = UUID.randomUUID().toString();
    String compilePath = System.getProperty("user.dir") + File.separator + "compile" + File.separator + uuid;
    try {
      // 1. Почистим исходник класса
      sourceCode = removePackage(sourceCode);

      // 2. Настроим пути
      // если исходник невалидный, то exception вывалится уже в getClassName(), благодаря библиотеке для парсинга JAVA-классов
      // com.github.javaparser.JavaParser
      String className = getClassName(sourceCode);
      String pathToJavaFile = compilePath + File.separator + className + ".java";
      String javaHomePath = System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator;

      // 3. Запишем исходник в файл
      File f = new File(pathToJavaFile);
      f.getParentFile().mkdirs();
      f.createNewFile();
      Files.write(Paths.get(pathToJavaFile), sourceCode.getBytes());

      // 4. Компильнем
      String javacCommand = javaHomePath + "javac " + pathToJavaFile;
      log.debug(javacCommand);
      Process javacProcess = Runtime.getRuntime().exec(javacCommand);
      javacProcess.waitFor();

      // 5. Заупустим то, что получилось, и вернем результат
      String javaCommand = javaHomePath + "java -client -Xmx544m -Xss64m -DBFT_CHECKER -cp " + compilePath + " " + className;
      log.debug(javaCommand);
      Process javaProcess = Runtime.getRuntime().exec(javaCommand);
      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(javaProcess.getOutputStream()))) {
        writer.write(input, 0, input.length());
      }
      String output = getProcessOutput(javaProcess.getInputStream());
      String error = getProcessOutput(javaProcess.getErrorStream());
      javaProcess.waitFor();

      if (StringUtils.isNotEmpty(error))
        return "Ошибка при выполнении программы";
      return output;
    } catch (Exception e) {
      return "Ошибка комиляции";
    } finally {
      try {
        FileUtils.deleteDirectory(new File(compilePath));
      } catch (IOException e) {
        log.debug("shit happens");
      }
    }
  }

  private static String getProcessOutput(InputStream ins) throws Exception {
    List<String> lines = new ArrayList<>();
    String line;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
      lines.add(line);
    }
    return lines.stream().collect(Collectors.joining("\n"));
  }

  private static String removePackage(String sourceCode) {
    String firstLine = sourceCode.substring(0, sourceCode.indexOf("\n") + 1);
    if (StringUtils.containsIgnoreCase(firstLine, "package"))
      return sourceCode.substring(sourceCode.indexOf("\n") + 1);
    return sourceCode;
  }

  private static String getClassName(String sourceCode) {
    CompilationUnit compilationUnit = JavaParser.parse(sourceCode);
    String className = compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream()
        .filter(NodeWithPublicModifier::isPublic).findFirst().get().getName().toString();
    return className;
  }
}
