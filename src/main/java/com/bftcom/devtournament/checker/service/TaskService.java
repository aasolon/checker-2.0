package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.model.Task;

import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
public interface TaskService {
  Task findById(long id);
  List<Task> findAll();
}
