package com.bftcom.devtournament.checker.dao;

import java.util.Map;

public interface TestVerdictDAO {
  void merge(long teamId, long taskId, long resultId, Boolean verdict);
  Map<Long, Integer> findByTeamIdAndTaskId(long teamId, long taskId);
}
