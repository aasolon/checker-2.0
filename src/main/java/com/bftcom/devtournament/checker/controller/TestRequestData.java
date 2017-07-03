package com.bftcom.devtournament.checker.controller;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author a.solonshchikov
 *         Date: 03.07.2017
 */
@Getter
@Setter
public class TestRequestData extends RequestData {
  @NotEmpty(message = "Не заполнено поле \"Test case\"")
  String testCase;
}
