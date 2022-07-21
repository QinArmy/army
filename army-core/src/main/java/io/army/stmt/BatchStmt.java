package io.army.stmt;

import java.util.List;

public interface BatchStmt extends GenericSimpleStmt {


    List<List<SqlParam>> groupList();

}
