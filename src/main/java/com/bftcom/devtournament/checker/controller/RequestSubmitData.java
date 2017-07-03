package com.bftcom.devtournament.checker.controller;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class RequestSubmitData extends RequestData {
  long langId;
  @NotEmpty(message = "Не заполнено поле \"Исходный код решения\"")
  String sourceCode;
}
