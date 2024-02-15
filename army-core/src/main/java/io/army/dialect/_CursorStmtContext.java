package io.army.dialect;

import io.army.stmt.SimpleStmt;


public interface _CursorStmtContext extends _StmtContext {


    String cursorName();

    String safeCursorName();

    @Override
    SimpleStmt build();
}
