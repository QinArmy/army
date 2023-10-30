package io.army.stmt;

import io.army.criteria.SQLParam;

import java.util.List;

public interface BatchStmt extends SingleSqlStmt {


    List<List<SQLParam>> groupList();

}
