package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Result;

import java.util.List;

public interface ResultDAO {
  List<Result> findByTaskIdAndTeamId(long taskId, long teamId);
  void saveResult(Result result);
  void update(Result result);
}