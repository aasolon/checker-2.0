package com.bftcom.devtournament.checker.model;

import lombok.Data;

import java.util.UUID;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
@Data
public class Team {
  private long id;
  private String name;
  private UUID token;
  private String judgetId;
  private long oosKey;
}
