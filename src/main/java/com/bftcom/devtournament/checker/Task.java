package com.bftcom.devtournament.checker;

/**
 * @author a.solonshchikov
 *         Date: 26.06.2017
 */
public class Task {
  public long id;
  public String name;
  public String description;
  public String initialData;
  public String result;
  public String exampleInitialData;
  public String exampleResult;

  public Task(long id, String name, String description, String initialData, String result, String exampleInitialData, String exampleResult) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.initialData = initialData;
    this.result = result;
    this.exampleInitialData = exampleInitialData;
    this.exampleResult = exampleResult;
  }
}
