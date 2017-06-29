package com.bftcom.devtournament.checker.dao;

import com.bftcom.devtournament.checker.model.Lang;

import java.util.List;

public interface LangDAO {
  Lang findById(long id);
  List<Lang> findAll();
}
