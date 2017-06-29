package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LangDAOImpl implements LangDAO {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Lang findById(long id) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    String sql = "SELECT * FROM Language WHERE id=:id";
    return jdbcTemplate.queryForObject(sql, params, getLangRowMapper());
  }

  @Override
  public List<Lang> findAll() {
    String sql = "SELECT * FROM Language";
    return jdbcTemplate.query(sql, getLangRowMapper());
  }

  private RowMapper<Lang> getLangRowMapper() {
    return (rs, rowNum) -> {
      Lang lang = new Lang();
      lang.setId(rs.getLong("Id"));
      lang.setName(rs.getString("Name"));
      lang.setOosKey(rs.getLong("OosKey"));
      return lang;
    };
  }
}
