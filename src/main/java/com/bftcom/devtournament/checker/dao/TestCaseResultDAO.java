package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.TestCaseResult;

import java.util.List;

public interface TestCaseResultDAO {
  List<TestCaseResult> findByTestCaseId(long tastCaseId);
  void save(TestCaseResult testCaseResult);
}
