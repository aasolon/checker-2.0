package com.bftcom.devtournament.checker.controller;

import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.Result;
import com.bftcom.devtournament.checker.model.Task;
import com.bftcom.devtournament.checker.service.MainService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.tools.*;
import javax.validation.Valid;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private MainService service;

  @RequestMapping("/")
  public String index() {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String taskList(Model model) {
    model.addAttribute("taskList", service.findAllTasks());
    return "tasklist";
  }

  @RequestMapping("/tasklist/{id}")
  public String task(@PathVariable("id") long id, Model model) {
    Task task = service.findTaskById(id);
    if (task == null)
      return "error/404";
    model.addAttribute("task", task);
    model.addAttribute("langList", service.findAllLangs());
    return "task";
  }

  @RequestMapping("/submit-result")
  public ModelAndView submitResult(@Valid @RequestBody SubmitRequestData submitData, Errors errors, Model model) throws IOException {
    if (errors.hasErrors()) {
      List<String> errorList = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      throw new UserException(errorList);
    }

    service.submitResult(submitData);

    List<Result> resultList = service.refreshResultList(submitData);
    ModelAndView mav = new ModelAndView("fragments :: resultlist");
    mav.addObject("resultList", resultList);
    return mav;
  }

  @RequestMapping("/refreshResultList")
  public ModelAndView refreshResultList(@Valid @RequestBody RequestData requestData, Errors errors) throws IOException {
    if (errors.hasErrors()) {
      List<String> errorList = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      throw new UserException(errorList);
    }
    
    List<Result> resultList = service.refreshResultList(requestData);
    ModelAndView mav = new ModelAndView("fragments :: resultlist");
    mav.addObject("resultList", resultList);
    return mav;
  }

  @RequestMapping("/result/{id}")
  public String result(@PathVariable("id") long id, Model model) {
    model.addAttribute("result", service.findResultById(id).getSourceCode());
    return "result";
  }

  @ExceptionHandler(UserException.class)
  public ModelAndView handleError(UserException ex) {
    log.error("Ошибка удаленного вызова", ex);
    ModelAndView mav = new ModelAndView("fragments :: errorlist");
    mav.addObject("errorList", ex.getErrorMsgList());
    return mav;
  }

  @ExceptionHandler(Throwable.class)
  public ModelAndView handleError(Throwable ex) {
    log.error("Ошибка удаленного вызова", ex);
    ModelAndView mav = new ModelAndView("fragments :: errorlist");
    mav.addObject("errorList", "Ошибка удаленного вызова, обратитесь к администратору системы");
    return mav;
  }



  @RequestMapping("/test-tasklist/{id}")
  public String taskTest(@PathVariable("id") long id, Model model, @ModelAttribute TestRequestData testRequestData) {
    Task task = service.findTaskById(id);
    if (task == null)
      return "error/404";
    model.addAttribute("task", task);
    testRequestData.setTaskId(id);
    testRequestData.setTestCase(null);
    model.addAttribute("testRequestData", testRequestData);
    return "test-task";
  }

  @RequestMapping("/test-submit-result")
  public String submitResultTest(@ModelAttribute TestRequestData testRequestData, final RedirectAttributes redirectAttributes) throws Exception {
    redirectAttributes.addFlashAttribute("testRequestData", testRequestData);
    Result result = service.findResultById(1);
    String sourceCode = result.getSourceCode();
    String firstLine = sourceCode.substring(0, sourceCode.indexOf("\n") + 1);
    if (StringUtils.containsIgnoreCase(firstLine, "package"))
      sourceCode = sourceCode.substring(sourceCode.indexOf("\n") + 1);
    String fileName = "ReverseRoot";
    String pathToJavaFile = System.getProperty("user.dir") + File.separator + "compiled" + File.separator + fileName + ".java";
    String pathToClassFile = System.getProperty("user.dir") + File.separator + "compiled";
    FileUtils.cleanDirectory(new File(System.getProperty("user.dir") + File.separator + "compiled"));
    File f = new File(pathToJavaFile);
    f.getParentFile().mkdirs();
    f.createNewFile();
    Files.write(Paths.get(pathToJavaFile), sourceCode.getBytes());

    Process pro = Runtime.getRuntime().exec(System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator + "javac " + pathToJavaFile);
    pro.waitFor();
    System.out.println(System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator + "java -cp " + pathToClassFile + " " + fileName);
    Process pro2 = Runtime.getRuntime().exec(System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator + "java -cp " + pathToClassFile + " " + fileName);
    String result1 = " 1427  0   \n" +
        "\n" +
        "   876652098643267843 \n" +
        "5276538";
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(pro2.getOutputStream()))) {
      writer.write(result1, 0, result1.length());
    }
    printLines(pro2.getInputStream());
    printLines(pro2.getErrorStream());
    pro2.waitFor();


    String j = "";
    for (int i = 0; i < 100000; i++) {
      j += "a";
    }

    return "redirect:/test-tasklist/" + testRequestData.getTaskId();
  }

  private static void printLines(InputStream ins) throws Exception {
    String line;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
      System.out.println(line);
    }
  }
}
