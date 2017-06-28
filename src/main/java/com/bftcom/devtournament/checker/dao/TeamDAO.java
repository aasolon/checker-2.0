package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Task;
import com.bftcom.devtournament.checker.model.Team;

import java.util.List;
import java.util.UUID;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
public interface TeamDAO {
  Team finById(long id);
  Team findByToken(String token);
  List<Team> findAll();
}
