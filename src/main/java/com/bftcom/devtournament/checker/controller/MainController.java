package com.bftcom.devtournament.checker.controller;

import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.Result;
import com.bftcom.devtournament.checker.model.Task;
import com.bftcom.devtournament.checker.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
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

  @RequestMapping("/submitResult")
  public ModelAndView submitResult(@Valid @RequestBody RequestSubmitData submitData, Errors errors, Model model) throws IOException {
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
    model.addAttribute("result", service.findResultById(id).sourceCode);
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
}
