package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Task;

import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 28.06.2017
 */
public interface TaskDAO {
  Task finById(long id);
  List<Task> findAll();
}
