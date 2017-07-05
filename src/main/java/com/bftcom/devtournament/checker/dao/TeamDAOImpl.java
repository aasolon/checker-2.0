package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class TeamDAOImpl implements TeamDAO {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Team findById(long id) {
    String sql = "SELECT * FROM Team WHERE id=:id";
    return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), getTeamRowMapper());
  }

  @Override
  public Team findByToken(String token) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("token", token);
      String sql = "SELECT * FROM Team WHERE token=:token";
      return jdbcTemplate.queryForObject(sql, params, getTeamRowMapper());
    } catch (EmptyResultDataAccessException ex) {
      return null;
    }
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
