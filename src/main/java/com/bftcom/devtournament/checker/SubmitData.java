package com.bftcom.devtournament.checker;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author a.solonshchikov
 *         Date: 27.06.2017
 */
public class SubmitData {
  String taskId;
  String token;
  @NotBlank(message = "sourceCode can't empty!")
  String sourceCode;

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }
}
