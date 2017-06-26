package com.bftcom.devtournament.checker;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author a.solonshchikov
 *         Date: 26.06.2017
 */
public class Result {
  public Timestamp timestamp;
  public String language;
  public String verdict;
  public int testNumber;
  public BigDecimal runtime;
  public String memory;

  public Result(Timestamp timestamp, String language, String verdict, int testNumber, BigDecimal runtime, String memory) {
    this.timestamp = timestamp;
    this.language = language;
    this.verdict = verdict;
    this.testNumber = testNumber;
    this.runtime = runtime;
    this.memory = memory;
  }
}
