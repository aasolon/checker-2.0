package com.bftcom.devtournament.checker.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TestCase {

  public interface GroupSubmitTestCase {}
  public interface GroupRefreshTestCase {}

  private long id;
  private long taskId;
  @NotEmpty(message = "Не заполнено поле \"Token\"", groups = {GroupSubmitTestCase.class, GroupRefreshTestCase.class})
  private String token;
  private long teamId;
  @NotEmpty(message = "Не заполнено поле \"Test case\"", groups = {GroupSubmitTestCase.class})
  private String input;
  private Map<Long, TestCaseResult> resultList;
  private Map<Long, Integer> testVerdictList;
  private long resultIdWithVerdict;
}
