package com.bftcom.devtournament.checker.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Team {
  private long id;
  private String name;
  private UUID token;
  private String judgetId;
  private long oosKey;
}
