package io.army.stmt;

import io.army.criteria.SqlParam;

import java.util.List;

public interface BatchStmt extends GenericSimpleStmt {


    List<List<SqlParam>> groupList();

}
