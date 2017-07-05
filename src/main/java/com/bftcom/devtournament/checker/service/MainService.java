package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.controller.RequestData;
import com.bftcom.devtournament.checker.controller.SubmitRequestData;
import com.bftcom.devtournament.checker.dao.*;
import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.*;
import com.bftcom.devtournament.checker.util.JavaCompilerAndExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainService {

  @Autowired
  private TaskDAO taskDao;
  @Autowired
  private TeamDAO teamDao;
  @Autowired
  private ResultDAO resultDao;
  @Autowired
  private LangDAO langDao;
  @Autowired
  private TestCaseDAO testCaseDao;
  @Autowired
  private TestCaseResultDAO testCaseResultDao;
  @Autowired
  private TestVerdictDAO testVerdictDao;
  @Autowired
  private OutgoingManager outgoingManager;

  public Task findTaskById(long id) {
    return taskDao.findById(id);
  }

  public List<Task> findAllTasks() {
    return taskDao.findAll();
  }

  public List<Lang> findAllLangs() {
    return langDao.findAll();
  }

  public Result findResultById(long id) {
    return resultDao.findById(id);
  }

  public List<Result> findResultsByTaskIdAndAccepted(long taskId) {
    return resultDao.findByTaskIdAndAccepted(taskId);
  }

  public List<Result> refreshResultList(RequestData requestData) throws IOException {
    Task task = findTaskById(requestData.getTaskId());
    Team team = findTeamByToken(requestData.getToken());
    List<OosResult> oosResultList = outgoingManager.getOosResultList(task.getOosKey(), team.getOosKey());
    List<Result> resultList = resultDao.findByTaskIdAndTeamId(task.getId(), team.getId());
    for (Result result : resultList) {
      OosResult oosResult = oosResultList.stream().filter(e -> e.getId() == result.getOosKey()).findFirst().orElse(null);
      if (oosResult != null && !StringUtils.equals(result.getVerdict(), oosResult.getVerdict())) {
        result.setVerdict(oosResult.getVerdict());
        result.setTestNumber(oosResult.getTestNumber());
        result.setRuntime(oosResult.getRuntime());
        result.setMemory(oosResult.getMemory());
        if ("Compilation error".equalsIgnoreCase(oosResult.getVerdict())) {
          String compilationError = outgoingManager.getCompilationError(team.getJudgetId(), oosResult.getId());
          result.setCompilationError(compilationError);
        }
        resultDao.update(result);
      }
    }
    return resultList;
  }

  public void submitResult(SubmitRequestData submitData) throws IOException {
    Team team = findTeamByToken(submitData.getToken());
    Lang lang = langDao.findById(submitData.getLangId());
    Task task = findTaskById(submitData.getTaskId());
    OutgoingManager.SubmitResult submitResult = outgoingManager.sumbit(team.getJudgetId(), lang.getOosKey(), task.getOosKey(), submitData.getSourceCode());

    if (submitResult != OutgoingManager.SubmitResult.OK) {
      throw new UserException(submitResult.getMsg());
    } else {
      List<OosResult> oosResultList = outgoingManager.getOosResultList(task.getOosKey(), team.getOosKey());
      OosResult oosResult = oosResultList.get(0);
      Result result = createResult(submitData, team, oosResult);
      resultDao.save(result);
    }
  }

  public void saveAndCompileTestCase(TestCase testCase) {
    Team team = findTeamByToken(testCase.getToken());
    testCase.setTeamId(team.getId());
    testCaseDao.save(testCase);

    List<Result> resultList = resultDao.findByTaskIdAndAccepted(testCase.getTaskId());
    for (Result result : resultList) {
      createAndSaveTestCaseResult(testCase, result);
    }
  }

  public void deleteTestCase(TestCase testCase) {
    Team team = findTeamByToken(testCase.getToken());
    testCaseDao.delete(testCase);
  }

  public List<TestCase> refreshTestCaseList(String token, long taskId, List<Result> resultList) {
    Team team = findTeamByToken(token);

    List<TestCase> testCaseList = testCaseDao.findByTeamAndTaskId(team.getId(), taskId);
    for (TestCase testCase : testCaseList) {
      // сначала вытянем из БД уже вычисленные варианты
      List<TestCaseResult> testCaseResultList = testCaseResultDao.findByTestCaseId(testCase.getId());
      Map<Long, TestCaseResult> testCaseResultMap =
          testCaseResultList.stream().collect(Collectors.toMap(TestCaseResult::getResultId, e -> e));

      // затем проверим не добавились ли новые решения
      List<Result> notProcessedResultList = resultList.stream()
          .filter(e -> !testCaseResultMap.containsKey(e.getId())).collect(Collectors.toList());
      if (!notProcessedResultList.isEmpty()) {
        for (Result notProcessedResult : notProcessedResultList)
          testCaseResultMap.put(notProcessedResult.getId(), createAndSaveTestCaseResult(testCase, notProcessedResult));
      }

      testCase.setResultList(testCaseResultMap);
    }

    return testCaseList;
  }

  public void saveTestVerdict(String token, long taskId, Map<Long, Integer> testVerdictList, long resultIdWithVerdict) {
    Team team = findTeamByToken(token);
    Integer vredict = testVerdictList.get(resultIdWithVerdict);
    testVerdictDao.merge(team.getId(), taskId, resultIdWithVerdict, vredict == null ? null : vredict == 1);
  }

  public Map<Long, Integer> findTestVerdictsByTokenAndTaskId(String token, long taskId) {
    Team team = findTeamByToken(token);
    return testVerdictDao.findByTeamIdAndTaskId(team.getId(), taskId);
  }

  private Team findTeamByToken(String token) {
    Team team = teamDao.findByToken(token);
    if (team == null)
      throw new UserException("Не найдена команда с указанным Token");
    return team;
  }

  private TestCaseResult createAndSaveTestCaseResult(TestCase testCase, Result result) {
    String output = JavaCompilerAndExecutor.compileAndExecute(result.getSourceCode(), testCase.getInput());
    TestCaseResult testCaseResult = createTestCaseResult(testCase, result, output);
    testCaseResultDao.save(testCaseResult);
    return testCaseResult;
  }

  private static TestCaseResult createTestCaseResult(TestCase testCase, Result result, String output) {
    TestCaseResult testCaseResult = new TestCaseResult();
    testCaseResult.setTestCaseId(testCase.getId());
    testCaseResult.setResultId(result.getId());
    testCaseResult.setOutput(output);
    return testCaseResult;
  }

  private static Result createResult(SubmitRequestData submitData, Team team, OosResult oosResult) {
    Result result = new Result();
    result.setTaskId(submitData.getTaskId());
    result.setTeamId(team.getId());
    result.setLangId(submitData.getLangId());
    result.setVerdict(oosResult.getVerdict());
    result.setSourceCode(submitData.getSourceCode());
    result.setTestNumber(oosResult.getTestNumber());
    result.setRuntime(oosResult.getRuntime());
    result.setMemory(oosResult.getMemory());
    result.setOosKey(oosResult.getId());
    return result;
  }
}
