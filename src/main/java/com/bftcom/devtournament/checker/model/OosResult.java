package com.bftcom.devtournament.checker.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OosResult {
  private long id;
  private String verdict;
  private Integer testNumber;
  private BigDecimal runtime;
  private String memory;
}
