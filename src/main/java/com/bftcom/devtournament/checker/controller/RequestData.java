package com.bftcom.devtournament.checker.controller;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class RequestData {
  long taskId;
  @NotEmpty(message = "Не заполнено поле \"Token\"")
  String token;
}
