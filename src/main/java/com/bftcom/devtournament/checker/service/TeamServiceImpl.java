package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.dao.TeamDAO;
import com.bftcom.devtournament.checker.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
@Service
public class TeamServiceImpl implements TeamService {
  @Autowired
  private TeamDAO teamDao;

  @Override
  public Team findById(long id) {
    return teamDao.finById(id);
  }

  @Override
  public Team findByToken(String token) {
    return teamDao.findByToken(token);
  }

  @Override
  public List<Team> findAll() {
    return teamDao.findAll();
  }
}
