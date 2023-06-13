package io.army.stmt;

import java.util.List;

public interface MultiStmt extends Stmt {

    String multiSql();

    List<Boolean> resultItemList();


}
