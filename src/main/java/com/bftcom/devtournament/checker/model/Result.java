package com.bftcom.devtournament.checker.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Result {
  public long id;
  public long taskId;
  public long teamId;
  public Timestamp timestamp;
  public long langId;
  public String langName;
  public String verdict;
  public String sourceCode;
  public Integer testNumber;
  public BigDecimal runtime;
  public String memory;
  private long oosKey;
}