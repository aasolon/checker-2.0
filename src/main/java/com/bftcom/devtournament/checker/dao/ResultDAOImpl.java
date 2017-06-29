package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResultDAOImpl implements ResultDAO {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;
  
  @Override
  public void saveResult(Result result) {
    String sql = "INSERT INTO Result(Task_Id, Team_Id, Tstamp, Lang_Id, Verdict, SourceCode, TestNumber, Runtime, Memory, OosKey) " +
        "VALUES(:task_id, :team_id, CURRENT_TIMESTAMP(), :lang_id, :verdict, :sourcecode, :testnumber, :runtime, :memory, :ooskey)";

    jdbcTemplate.update(sql, getSqlParamByModel(result));
  }

  @Override
  public void update(Result result) {
    String sql = "UPDATE Result SET Verdict = :verdict, TestNumber = :testnumber, Runtime = :runtime, Memory = :memory " +
        "WHERE Id = :id";
    jdbcTemplate.update(sql, getSqlParamByModel(result));
  }

  @Override
  public List<Result> findByTaskIdAndTeamId(long taskId, long teamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("task_id", taskId);
    params.put("team_id", teamId);
    String sql = "SELECT r.Id, r.Task_Id, r.Team_Id, r.Tstamp, r.Lang_Id, l.Name langName, r.Verdict, r.SourceCode, " +
        "r.TestNumber, r.Runtime, r.Memory, r.OosKey " +
        "FROM Result r " +
        "LEFT JOIN Language l on l.Id = r.Lang_Id " +
        "WHERE Task_Id = :task_id AND Team_Id = :team_id ORDER BY Id";
    return jdbcTemplate.query(sql, params, getResultRowMapper());
  }

  private static Map<String, Object> getSqlParamByModel(Result result) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", result.getId());
    params.put("task_id", result.getTaskId());
    params.put("team_id", result.getTeamId());
    params.put("lang_id", result.getLangId());
    params.put("verdict", result.getVerdict());
    params.put("sourcecode", result.getSourceCode());
    params.put("testnumber", result.getTestNumber());
    params.put("runtime", result.getRuntime());
    params.put("memory", result.getMemory());
    params.put("ooskey", result.getOosKey());
    return params;
  }

  private RowMapper<Result> getResultRowMapper() {
    return (rs, rowNum) -> {
      Result result = new Result();
      result.setId(rs.getLong("Id"));
      result.setTaskId(rs.getLong("Task_Id"));
      result.setTeamId(rs.getLong("Team_Id"));
      result.setTimestamp(rs.getTimestamp("Tstamp"));
      result.setLangId(rs.getLong("Lang_Id"));
      result.setLangName(rs.getString("langName"));
      result.setVerdict(rs.getString("Verdict"));
      result.setSourceCode(rs.getString("SourceCode"));
      int testNumber = rs.getInt("TestNumber");
      result.setTestNumber(testNumber > 0 ? testNumber : null);
      result.setRuntime(rs.getBigDecimal("Runtime"));
      result.setMemory(rs.getString("Memory"));
      result.setOosKey(rs.getLong("OosKey"));
      return result;
    };
  }
}
