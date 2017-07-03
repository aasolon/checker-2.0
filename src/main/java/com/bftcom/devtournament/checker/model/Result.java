package com.bftcom.devtournament.checker.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Result {
  private long id;
  private long taskId;
  private long teamId;
  private Timestamp timestamp;
  private long langId;
  private String langName;
  private String verdict;
  private String sourceCode;
  private Integer testNumber;
  private BigDecimal runtime;
  private String memory;
  private long oosKey;
}