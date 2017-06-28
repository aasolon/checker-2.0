package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
@Repository
public class TeamDAOImpl implements TeamDAO {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Team finById(long id) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    String sql = "SELECT * FROM Team WHERE id=:id";
    return jdbcTemplate.queryForObject(sql, params, getTeamRowMapper());
  }

  @Override
  public Team findByToken(String token) {
    Map<String, Object> params = new HashMap<>();
    params.put("token", token);
    String sql = "SELECT * FROM Team WHERE token=:token";
    return jdbcTemplate.queryForObject(sql, params, getTeamRowMapper());
  }

  @Override
  public List<Team> findAll() {
    String sql = "SELECT * FROM Task";
    return jdbcTemplate.query(sql, getTeamRowMapper());
  }

  private RowMapper<Team> getTeamRowMapper() {
    return (rs, rowNum) -> {
      Team team = new Team();
      team.setId(rs.getLong("Id"));
      team.setName(rs.getString("Name"));
      team.setToken((UUID) rs.getObject("Token"));
      team.setJudgetId(rs.getString("Judge_ID"));
      team.setOosKey(rs.getLong("OosKey"));
      return team;
    };
  }
}
