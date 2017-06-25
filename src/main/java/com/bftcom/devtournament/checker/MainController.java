package com.bftcom.devtournament.checker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author a.solonshchikov
 *         Date: 21.06.2017
 */
@Controller
public class MainController {

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public void setNamedParameterJdbcTemplate(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

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
    model.addAttribute("id", id);
    return "/task";
  }

  class Task {
    public long id;
    public String name;
    public String description;
    public String initialData;
    public String result;
    public String exampleInitialData;
    public String exampleResult;

    public Task(long id, String name, String description, String initialData, String result, String exampleInitialData, String exampleResult) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.initialData = initialData;
      this.result = result;
      this.exampleInitialData = exampleInitialData;
      this.exampleResult = exampleResult;
    }
  }
}
