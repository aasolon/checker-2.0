package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskDAOImpl implements TaskDAO {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Task findById(long id) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    String sql = "SELECT * FROM Task WHERE id=:id";
    return jdbcTemplate.queryForObject(sql, params, getTaskRowMapper());
  }

  @Override
  public List<Task> findAll() {
    String sql = "SELECT * FROM Task";
    return jdbcTemplate.query(sql, getTaskRowMapper());
  }

  private RowMapper<Task> getTaskRowMapper() {
    return (rs, rowNum) -> {
      Task task = new Task();
      task.setId(rs.getLong("Id"));
      task.setName(rs.getString("Name"));
      task.setDescription(rs.getString("Description"));
      task.setInitialData(rs.getString("InitialData"));
      task.setResult(rs.getString("Result"));
      task.setExampleInitialData(rs.getString("Example_InitialData"));
      task.setExampleResult(rs.getString("Example_Result"));
      task.setOosKey(rs.getLong("OosKey"));
      return task;
    };
  }
}
