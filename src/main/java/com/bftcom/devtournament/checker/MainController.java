package com.bftcom.devtournament.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author a.solonshchikov
 *         Date: 21.06.2017
 */
@Controller
public class MainController {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  @Autowired
  private OutgoingManager outgoingManager;

  private RowMapper<Task> getTaskRowMapper() {
    return (rs, rowNum) -> new Task(rs.getLong("Id"), rs.getString("Name"),
            rs.getString("Description"), rs.getString("InitialData"), rs.getString("Result"),
            rs.getString("Example_InitialData"), rs.getString("Example_Result"));
  }

  @RequestMapping("/")
  public String index() {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String taskList(Model model) {
    String sql = "SELECT Id, Name, Description, InitialData, Result, Example_InitialData, Example_Result FROM Task";
    List<Task> taskList = namedParameterJdbcTemplate.query(sql, getTaskRowMapper());
    model.addAttribute("taskList", taskList);
    return "tasklist";
  }

  @RequestMapping("/tasklist/{id}")
  public String task(@PathVariable("id") long id, Model model) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    String sql = "SELECT Id, Name, Description, InitialData, Result, Example_InitialData, Example_Result FROM Task WHERE id=:id";
    Task task = namedParameterJdbcTemplate.queryForObject(sql, params, getTaskRowMapper());
    model.addAttribute("task", task);
    return "task";
  }

  @RequestMapping("/submit")
//  public String submit(@RequestParam Long taskId, @RequestParam String token, @RequestParam String sourceCode, Model model) throws IOException {
  public String submit(@Valid @RequestBody SubmitData submitData, Errors errors, Model model) throws IOException {
//    Map<String, Object> params = new HashMap<>();
//    params.put("id", taskId);
//    String sql = "SELECT Id, Name, Description, InitialData, Result, Example_InitialData, Example_Result FROM Task WHERE id=:id";
//    Task task = namedParameterJdbcTemplate.queryForObject(sql, params, getTaskRowMapper());
//    model.addAttribute("task", task);
    if (true) {
      model.addAttribute("errorList", Arrays.asList("error 1", "error 2"));
      return "result :: errorlist";
//      return "task";

    }
//    SubmitResult submitResult = outgoingManager.sumbit(judgeID, sourceCode);
    SubmitResult submitResult = SubmitResult.OK;
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
    return "result :: resultlist";
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
    return "result :: resultlist";
  }
}
