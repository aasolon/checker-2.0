package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Team;

import java.util.List;

public interface TeamDAO {
  Team findById(long id);
  Team findByToken(String token);
  List<Team> findAll();
}
