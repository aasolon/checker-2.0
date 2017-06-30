package com.bftcom.devtournament.checker.controller;

import com.bftcom.devtournament.checker.model.Result;
import com.bftcom.devtournament.checker.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
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
    model.addAttribute("task", service.findTaskById(id));
    model.addAttribute("langList", service.findAllLangs());
    return "task";
  }

  @RequestMapping("/submitResult")
  public ModelAndView submitResult(@Valid @RequestBody RequestSubmitData submitData, Errors errors, Model model) throws IOException {
    if (errors.hasErrors()) {
      List<String> errorList = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      ModelAndView mav = new ModelAndView("fragments :: errorlist");
      mav.addObject("errorList", errorList);
      return mav;
    }

    List<String> errorList = service.submitResult(submitData);

    if (!errorList.isEmpty()) {
      ModelAndView mav = new ModelAndView("fragments :: errorlist");
      mav.addObject("errorList", errorList);
      return mav;
    }

    List<Result> resultList = service.refreshResultList(submitData);
    ModelAndView mav = new ModelAndView("fragments :: resultlist");
    mav.addObject("resultList", resultList);
    return mav;
  }

  @RequestMapping("/refreshResultList")
  public ModelAndView refreshResultList(@Valid @RequestBody RequestData requestData, Errors errors) throws IOException {
    if (errors.hasErrors()) {
      List<String> errorList = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      ModelAndView mav = new ModelAndView("fragments :: errorlist");
      mav.addObject("errorList", errorList);
      return mav;
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
}
