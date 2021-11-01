package io.army.sync.executor;

import io.army.stmt.Stmt;
import io.army.stmt.StmtOption;

import java.util.List;

public interface StmtExecutor extends Executor{


    /**
     * @throws io.army.session.DataAccessException
     */
    int valueInsert(Stmt stmt,int txTimeout);




}
