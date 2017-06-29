package com.bftcom.devtournament.checker.model;

import lombok.Data;

@Data
public class Task {
  private long id;
  private String name;
  private String description;
  private String initialData;
  private String result;
  private String exampleInitialData;
  private String exampleResult;
  private long oosKey;
}
