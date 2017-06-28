package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.model.Task;
import com.bftcom.devtournament.checker.model.Team;

import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
public interface TeamService {
  Team findById(long id);
  Team findByToken(String token);
  List<Team> findAll();
}
