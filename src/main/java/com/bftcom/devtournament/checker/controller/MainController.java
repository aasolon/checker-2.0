package com.bftcom.devtournament.checker.controller;

import com.bftcom.devtournament.checker.model.*;
import com.bftcom.devtournament.checker.service.OutgoingManager;
import com.bftcom.devtournament.checker.service.SubmitResult;
import com.bftcom.devtournament.checker.service.TaskService;
import com.bftcom.devtournament.checker.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  @Autowired
  private OutgoingManager outgoingManager;
  @Autowired
  private TaskService taskService;
  @Autowired
  private TeamService teamService;

  @RequestMapping("/")
  public String index() {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String taskList(Model model) {
    model.addAttribute("taskList", taskService.findAll());
    return "tasklist";
  }

  @RequestMapping("/tasklist/{id}")
  public String task(@PathVariable("id") long id, Model model) {
    model.addAttribute("task", taskService.findById(id));
    return "task";
  }

  @RequestMapping("/submit")
  public ModelAndView submit(@Valid @RequestBody SubmitData submitData, Errors errors, Model model) throws IOException {
    if (errors.hasErrors()) {
      List<String> errorList = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      ModelAndView mav = new ModelAndView("fragments :: errorlist");
      mav.addObject("errorList", errorList);
      return mav;
    }
    Team team = teamService.findByToken(submitData.getToken());
    SubmitResult submitResult = outgoingManager.sumbit(team.getJudgetId(), 32, 1000, submitData.getSourceCode());
//    SubmitResult submitResult = outgoingManager.sumbit(judgeID, sourceCode);
//    SubmitResult submitResult = SubmitResult.OK;
    List<Result> resultList = new ArrayList<>();
    
    if (submitResult != SubmitResult.OK) {
      resultList.add(new Result(
          new Timestamp(System.currentTimeMillis()),
          "111",
          "111",
          2,
          new BigDecimal("0.111"),
          "111"));
    } else {
      List<OosResult> oosResultList = outgoingManager.getOosResultList("1000", "230361");
//      List<OosResult> oosResultList = outgoingManager.getOosResultList();
      resultList.add(new Result(
          new Timestamp(System.currentTimeMillis()),
          "Java 8",
          "asd",
          2,
          new BigDecimal("0.111"),
          "53 123 КБ"));
    }

    model.addAttribute("resultList", resultList);
    return new ModelAndView("fragments :: resultlist");
  }

  @RequestMapping("/refreshResultList")
  public String refreshResultList(@RequestParam String token, @RequestParam long taskId, Model model) throws IOException {
//    Result lastResult = outgoingManager.getLastResult();
    List<Result> resultList = new ArrayList<>();
    resultList.add(
        new Result(
            new Timestamp(System.currentTimeMillis()),
            "Java 8",
            "asd",
            2,
            new BigDecimal("0.111"),
            "53 123 КБ"
        )
    );
    model.addAttribute("resultList", resultList);
    return "fragments :: resultlist";
  }
}
