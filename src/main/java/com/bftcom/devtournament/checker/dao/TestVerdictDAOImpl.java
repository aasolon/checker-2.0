package com.bftcom.devtournament.checker.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TestVerdictDAOImpl implements TestVerdictDAO {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public void merge(long teamId, long taskId, long resultId, Boolean verdict) {
    String sql = "MERGE INTO TestVerdict(Team_Id, Task_Id, Result_Id, Verdict) KEY(Team_Id, Task_Id, Result_Id) " +
        "VALUES(:team_id, :task_id, :result_id, :verdict)";
    MapSqlParameterSource params = new MapSqlParameterSource("team_id", teamId)
        .addValue("task_id", taskId)
        .addValue("result_id", resultId)
        .addValue("verdict", verdict);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public Map<Long, Integer> findByTeamIdAndTaskId(long teamId, long taskId) {
    String sql = "SELECT Result_Id, Verdict FROM TestVerdict WHERE Team_Id = :team_id AND Task_Id = :task_id";
    return jdbcTemplate.query(sql, new MapSqlParameterSource("team_id", teamId).addValue("task_id", taskId),
        rs -> {
          Map<Long, Integer> result = new HashMap<>();
          while (rs.next()) {
            Integer verdict = rs.getBoolean("Verdict") ? 1 : 0;
            if (rs.wasNull())
              verdict = null;
            result.put(rs.getLong("Result_Id"), verdict);
          }
          return result;
        });
  }
}
