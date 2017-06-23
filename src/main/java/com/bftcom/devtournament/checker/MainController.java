package com.bftcom.devtournament.checker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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

  @RequestMapping("/")
  public String index() {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String taskList(Model model) {
    String sql = "SELECT Id, InitialData FROM Task";
    List<Task> taskList = namedParameterJdbcTemplate
        .query(sql, (rs, rowNum) -> new Task(rs.getLong("Id"), rs.getString("InitialData")));
    model.addAttribute("taskList", taskList);
    return "tasklist";
  }

  @RequestMapping("/tasklist/{id}")
  public String task(@PathVariable("id") long id, Model model) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    String sql = "SELECT Id, Description FROM Task WHERE id=:id";
    Task task = namedParameterJdbcTemplate
        .queryForObject(sql, params, (rs, rowNum) -> new Task(rs.getLong("Id"), rs.getString("Description")));
    model.addAttribute("task", task);
    model.addAttribute("id", id);
    return "/task";
  }

  class Task {
    public long id;
    public String description;

    public Task(long id, String description) {
      this.id = id;
      this.description = description;
    }
  }
}
