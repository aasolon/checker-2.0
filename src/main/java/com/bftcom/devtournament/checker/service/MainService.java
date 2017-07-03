package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.controller.RequestData;
import com.bftcom.devtournament.checker.controller.SubmitRequestData;
import com.bftcom.devtournament.checker.dao.LangDAO;
import com.bftcom.devtournament.checker.dao.ResultDAO;
import com.bftcom.devtournament.checker.dao.TaskDAO;
import com.bftcom.devtournament.checker.dao.TeamDAO;
import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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

  public List<Result> refreshResultList(RequestData requestData) throws IOException {
    Task task = findTaskById(requestData.getTaskId());
    Team team = teamDao.findByToken(requestData.getToken());
    if (team == null)
      throw new UserException("Не найдена команда с указанным Token");
    List<OosResult> oosResultList = outgoingManager.getOosResultList(task.getOosKey(), team.getOosKey());
    List<Result> resultList = resultDao.findByTaskIdAndTeamId(task.getId(), team.getId());
    for (Result result : resultList) {
      OosResult oosResult = oosResultList.stream().filter(e -> e.getId() == result.getOosKey()).findFirst().orElse(null);
      if (oosResult != null && !StringUtils.equals(result.getVerdict(), oosResult.getVerdict())) {
        result.setVerdict(oosResult.getVerdict());
        result.setTestNumber(oosResult.getTestNumber());
        result.setRuntime(oosResult.getRuntime());
        result.setMemory(oosResult.getMemory());
        resultDao.update(result);
      }
    }
    return resultList;
  }

  public void submitResult(SubmitRequestData submitData) throws IOException {
    Team team = teamDao.findByToken(submitData.getToken());
    if (team == null)
      throw new UserException("Не найдена команда с указанным Token");
    Lang lang = langDao.findById(submitData.getLangId());
    Task task = findTaskById(submitData.getTaskId());
    OutgoingManager.SubmitResult submitResult = outgoingManager.sumbit(team.getJudgetId(), lang.getOosKey(), task.getOosKey(), submitData.getSourceCode());

    if (submitResult != OutgoingManager.SubmitResult.OK) {
      throw new UserException(submitResult.getMsg());
    } else {
      List<OosResult> oosResultList = outgoingManager.getOosResultList(task.getOosKey(), team.getOosKey());
      OosResult oosResult = oosResultList.get(0);
      Result result = createResult(submitData, team, oosResult);
      resultDao.saveResult(result);
    }
  }

  private Result createResult(SubmitRequestData submitData, Team team, OosResult oosResult) {
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
