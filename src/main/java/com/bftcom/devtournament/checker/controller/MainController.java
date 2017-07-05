package com.bftcom.devtournament.checker.controller;

import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.Result;
import com.bftcom.devtournament.checker.model.Task;
import com.bftcom.devtournament.checker.model.TestCase;
import com.bftcom.devtournament.checker.service.MainService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private MainService service;



  //****************************************** Обработка запросов разработки ******************************************
  @RequestMapping("/")
  public String index() {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String showTaskList(Model model) {
    model.addAttribute("taskList", service.findAllTasks());
    return "tasklist";
  }

  @RequestMapping("/tasklist/{id}")
  public String showTask(@PathVariable("id") long id, Model model) {
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
  public String showResultSourceCode(@PathVariable("id") long taskId, Model model) {
    model.addAttribute("result", service.findResultById(taskId).getSourceCode());
    return "result";
  }

  @RequestMapping("/compilation-error/{id}")
  public String showResultCompilationError(@PathVariable("id") long taskId, Model model) {
    model.addAttribute("compilationError", service.findResultById(taskId).getCompilationError());
    return "compilation-error";
  }







  //***************************************** Обработка запросов тестирования *****************************************
  @RequestMapping("/test-tasklist")
  public String showTestTaskList(Model model) {
    model.addAttribute("taskList", service.findAllTasks());
    return "test-tasklist";
  }

  @GetMapping("/test-tasklist/{id}")
  public String showTestTask(@PathVariable("id") long id, Model model, @ModelAttribute TestCase testCase) {
    if (!fillTestModel(id, model, testCase))
      return "error/404";

    return "test-task";
  }

  @PostMapping(path = "/test-tasklist/{id}", params = "add")
  public String submitTestCase(@PathVariable("id") long taskId, Model model,
                               @Validated(TestCase.GroupSubmitTestCase.class) @ModelAttribute TestCase testCase,
                               BindingResult bindingResult, final RedirectAttributes redirectAttributes) throws Exception {
    if (bindingResult.hasErrors()) {
      fillTestModel(taskId, model, testCase);

      return "test-task";
    }

    testCase.setTaskId(taskId);
    service.saveAndCompileTestCase(testCase);

    testCase.setInput(null);
    redirectAttributes.addFlashAttribute("testCase", testCase);
    return "redirect:/test-tasklist/" + taskId;
  }

  @PostMapping(path = "/test-tasklist/{id}", params = "delete")
  public String deleteTestCase(@PathVariable("id") long taskId, Model model,
                                    @Validated(TestCase.GroupRefreshTestCase.class) @ModelAttribute TestCase testCase,
                                    BindingResult bindingResult) throws Exception {
    if (!bindingResult.hasErrors()) {
      service.deleteTestCase(testCase.getToken(), testCase.getDeleteId());
    }

    fillTestModel(taskId, model, testCase);

    return "test-task";
  }

  @PostMapping(path = "/test-tasklist/{id}", params = "refresh")
  public String refreshTestCaseList(@PathVariable("id") long taskId, Model model,
                                    @Validated(TestCase.GroupRefreshTestCase.class) @ModelAttribute TestCase testCase,
                                    BindingResult bindingResult) throws Exception {
    if (!bindingResult.hasErrors() && testCase.getResultIdWithVerdict() != 0) {
      service.saveTestVerdict(testCase.getToken(), taskId, testCase.getTestVerdictList(), testCase.getResultIdWithVerdict());
    }

    fillTestModel(taskId, model, testCase);

    return "test-task";
  }

  private boolean fillTestModel(long taskId, Model model, TestCase testCase) {
    Task task = service.findTaskById(taskId);
    if (task == null)
      return false;
    model.addAttribute("task", task);
    
    List<Result> resultList = service.findResultsByTaskIdAndAccepted(taskId);
    model.addAttribute("resultList", resultList);

    if (StringUtils.isNotEmpty(testCase.getToken())) {
      testCase.setTestVerdictList(service.findTestVerdictsByTokenAndTaskId(testCase.getToken(), taskId));

      List<TestCase> testCaseList = service.refreshTestCaseList(testCase.getToken(), taskId, resultList);
      model.addAttribute("testCaseList", testCaseList);
    }

    testCase.setResultIdWithVerdict(0);
    testCase.setDeleteId(0);

    return true;
  }






  //************************************************* Обработка ошибок *************************************************
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
    mav.addObject("errorList", "Ошибка удаленного вызова, повторите попытку или обратиесть к администратору системы");
    return mav;
  }
}
