package com.bftcom.devtournament.checker.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
      log.info(javacCommand);
      Process javacProcess = Runtime.getRuntime().exec(javacCommand);
      if (!javacProcess.waitFor(2, TimeUnit.SECONDS)) {
        log.info("Превышено время ожидания компиляции решения");
        javacProcess.destroyForcibly();
        return "Ошибка при выполнении программы";
      }

      // 5. Заупустим то, что получилось, и вернем результат
      String javaCommand = javaHomePath + "java -client -Xmx544m -Xss64m -DBFT_CHECKER -cp " + compilePath + " " + className;
      log.info(javaCommand);
      log.info("java input:\n" + input);
      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      ByteArrayOutputStream stderr = new ByteArrayOutputStream();
      ByteArrayInputStream stdin = new ByteArrayInputStream(input.getBytes());
      PumpStreamHandler streamHandler = new PumpStreamHandler(stdout, stderr, stdin);
      CommandLine cmdLine = CommandLine.parse(javaCommand);
      DefaultExecutor executor = new DefaultExecutor();
      executor.setStreamHandler(streamHandler);
      executor.setWatchdog(new ExecuteWatchdog(2 * 1000)); // даем 2 сек на запуск решения
      try {
        executor.execute(cmdLine);
      } catch (ExecuteException e) {
        log.info("Превышено время ожидания выполнения решения", e);
        return "Ошибка при выполнении программы";
      }
      String output = stdout.toString();
      String error = stderr.toString();


      if (StringUtils.isNotEmpty(error)) {
        log.info("java error:\n" + error);
        return "Ошибка при выполнении программы";
      } else {
        log.info("java output:\n" + output);
        return output;
      }
    } catch (Exception e) {
      log.info("Ошибка комиляции", e);
      return "Ошибка при выполнении программы";
    } finally {
      try {
        FileUtils.deleteDirectory(new File(compilePath));
      } catch (IOException e) {
        log.info("shit happens");
      }
    }
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
