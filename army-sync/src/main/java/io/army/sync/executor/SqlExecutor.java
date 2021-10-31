package io.army.sync.executor;

import io.army.stmt.Stmt;

import java.util.List;

public interface SqlExecutor  extends Executor{


    int valueInsert(List<Stmt> stmtList);




}
