package io.army.sync.executor;

import io.army.stmt.Stmt;

import java.util.List;

public interface StmtExecutor extends Executor {


    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    int valueInsert(Stmt stmt, int txTimeout);

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    <T> List<T> returnInsert(Stmt stmt, int txTimeout, Class<T> resultClass);

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    int update(Stmt stmt, int txTimeout);


}
