package com.bftcom.devtournament.checker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author a.solonshchikov
 *         Date: 21.06.2017
 */
@Controller
public class MainController {

  @RequestMapping("/")
  public String index(Model model) {
    return "redirect:/tasklist";
  }

  @RequestMapping("/tasklist")
  public String greeting(Model model) {
    model.addAttribute("id", 3);
    return "tasklist";
  }

  @RequestMapping("/task")
  public String showUser(@RequestParam("id") long id, Model model) {
    model.addAttribute("id", id);
    return "/task";

  }
}
