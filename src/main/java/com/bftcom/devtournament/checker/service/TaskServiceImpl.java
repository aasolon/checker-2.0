package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.dao.TaskDAO;
import com.bftcom.devtournament.checker.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
@Service
public class TaskServiceImpl implements TaskService {

  @Autowired
  private TaskDAO taskDao;

  @Override
  public Task findById(long id) {
    return taskDao.finById(id);
  }

  @Override
  public List<Task> findAll() {
    return taskDao.findAll();
  }
}
