package com.bftcom.devtournament.checker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseResult {
  private long id;
  private long testCaseId;
  private long resultId;
  private String output;
}
