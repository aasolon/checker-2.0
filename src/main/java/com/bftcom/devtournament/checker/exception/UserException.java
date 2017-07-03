package com.bftcom.devtournament.checker.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserException extends RuntimeException {
  private List<String> errorMsgList = new ArrayList<>();

  public UserException(String message) {
    super(message);
    errorMsgList.add(message);
  }

  public UserException(List<String> message) {
    super(message.stream().collect(Collectors.joining("\n")));
    errorMsgList.addAll(message);
  }
}
