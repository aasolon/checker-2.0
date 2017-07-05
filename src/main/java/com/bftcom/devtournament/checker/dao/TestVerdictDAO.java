package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.TestCaseResult;

import java.util.Map;

/**
 * @author a.solonshchikov
 *         Date: 04.07.2017
 */
public interface TestVerdictDAO {
  void merge(long teamId, long taskId, long resultId, Boolean verdict);
  Map<Long, Integer> findByTeamIdAndTaskId(long teamId, long taskId);
}
