package io.army.stmt;

import java.util.List;

public interface GroupStmt extends Stmt {

    List<Stmt> stmtGroup();

}
