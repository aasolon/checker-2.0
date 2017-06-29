package com.bftcom.devtournament.checker.controller;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class RequestSubmitData extends RequestData {
  long langId;
  @NotEmpty(message = "Не заполнено поле \"Исходный код решения\"")
  String sourceCode;
}
