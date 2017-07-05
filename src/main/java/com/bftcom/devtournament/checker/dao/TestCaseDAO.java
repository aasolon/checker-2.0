package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.TestCase;

import java.util.List;

public interface TestCaseDAO {
  TestCase findById(long id);
  List<TestCase> findAll();
  List<TestCase> findByTeamAndTaskId(long teamId, long taskId);
  void save(TestCase testCase);
  void delete(long id);
}
