package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Result;

import java.util.List;

public interface ResultDAO {
  List<Result> findByTaskIdAndTeamId(long taskId, long teamId);
  List<Result> findByTaskIdAndAccepted(long taskId);
  Result findById(long id);
  void save(Result result);
  void update(Result result);
}
