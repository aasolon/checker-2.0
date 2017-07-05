package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.TestCaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestCaseResultDAOImpl implements TestCaseResultDAO {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;
  
  @Override
  public List<TestCaseResult> findByTestCaseId(long tastCaseId) {
    String sql = "SELECT * FROM TestCaseResult WHERE TestCase_Id = :testcase_id";
    return jdbcTemplate.query(sql, new MapSqlParameterSource("testcase_id", tastCaseId), getTestCaseResultRowMapper());
  }

  @Override
  public void save(TestCaseResult testCaseResult) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO TestCaseResult(TestCase_Id, Result_Id, Output) " +
        "VALUES(:testcase_id, :result_id, :output)";
    jdbcTemplate.update(sql, getSqlParamByModel(testCaseResult), keyHolder);
    testCaseResult.setId(keyHolder.getKey().longValue());
  }

  private static SqlParameterSource getSqlParamByModel(TestCaseResult testCaseResult) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("testcase_id", testCaseResult.getTestCaseId());
    params.addValue("result_id", testCaseResult.getResultId());
    params.addValue("output", testCaseResult.getOutput());
    return params;
  }

  private RowMapper<TestCaseResult> getTestCaseResultRowMapper() {
    return (rs, rowNum) -> {
      TestCaseResult testCaseResult = new TestCaseResult();
      testCaseResult.setId(rs.getLong("Id"));
      testCaseResult.setTestCaseId(rs.getLong("TestCase_Id"));
      testCaseResult.setResultId(rs.getLong("Result_Id"));
      testCaseResult.setOutput(rs.getString("Output"));
      return testCaseResult;
    };
  }
}
