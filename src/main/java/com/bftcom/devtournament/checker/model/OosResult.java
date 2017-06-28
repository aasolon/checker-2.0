package com.bftcom.devtournament.checker.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OosResult {
  public long id;
  public String verdict;
  public Integer testNumber;
  public BigDecimal runtime;
  public String memory;

  public OosResult(long id, String verdict, Integer testNumber, BigDecimal runtime, String memory) {
    this.id = id;
    this.verdict = verdict;
    this.testNumber = testNumber;
    this.runtime = runtime;
    this.memory = memory;
  }
}
