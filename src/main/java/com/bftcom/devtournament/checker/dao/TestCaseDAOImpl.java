package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TestCaseDAOImpl implements TestCaseDAO {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public TestCase findById(long id) {
    String sql = "SELECT * FROM TestCase WHERE Actual = True AND id=:id";
    return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), getTestCaseRowMapper());
  }

  @Override
  public List<TestCase> findAll() {
    String sql = "SELECT * FROM TestCase WHERE Actual = True";
    return jdbcTemplate.query(sql, getTestCaseRowMapper());
  }

  @Override
  public List<TestCase> findByTeamAndTaskId(long teamId, long taskId) {
    String sql = "SELECT * FROM TestCase WHERE Actual = True AND Team_Id = :team_id AND Task_Id = :task_id";
    return jdbcTemplate.query(sql, new MapSqlParameterSource("team_id", teamId).addValue("task_id", taskId),
        getTestCaseRowMapper());
  }

  @Override
  public void save(TestCase testCase) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO TestCase(Task_Id, Team_Id, Tstamp, Input) " +
        "VALUES(:task_id, :team_id, CURRENT_TIMESTAMP(), :input)";
    jdbcTemplate.update(sql, getSqlParamByModel(testCase), keyHolder);
    testCase.setId(keyHolder.getKey().longValue());
  }

  @Override
  public void delete(TestCase testCase) {
    String sql = "UPDATE TestCase SET Actual = False AND Id = :id";
    jdbcTemplate.update(sql, new MapSqlParameterSource("id", testCase.getId()));
  }

  private static SqlParameterSource getSqlParamByModel(TestCase testCase) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("task_id", testCase.getTaskId());
    params.addValue("team_id", testCase.getTeamId());
    params.addValue("input", testCase.getInput());
    return params;
  }

  private RowMapper<TestCase> getTestCaseRowMapper() {
    return (rs, rowNum) -> {
      TestCase testCase = new TestCase();
      testCase.setId(rs.getLong("Id"));
      testCase.setTaskId(rs.getLong("Task_Id"));
      testCase.setInput(rs.getString("Input"));
      return testCase;
    };
  }
}
