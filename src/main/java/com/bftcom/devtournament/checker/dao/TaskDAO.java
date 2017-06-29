package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Task;

import java.util.List;

public interface TaskDAO {
  Task findById(long id);
  List<Task> findAll();
}
