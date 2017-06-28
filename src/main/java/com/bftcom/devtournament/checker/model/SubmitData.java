package com.bftcom.devtournament.checker.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class SubmitData {
  String taskId;
  String token;
  @NotEmpty(message = "Не заполнено поле \"Исходный код решения\"")
  String sourceCode;
}
