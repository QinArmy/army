package io.army.sync.executor;

import io.army.session.DataAccessException;
import io.army.stmt.Stmt;

import java.util.List;

public interface StmtExecutor extends Executor {


    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    long insert(Stmt stmt, int timeout) throws DataAccessException;

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    <T> List<T> returnInsert(Stmt stmt, int txTimeout, Class<T> resultClass) throws DataAccessException;

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    int update(Stmt stmt, int txTimeout) throws DataAccessException;

    <T> List<T> select(Stmt stmt, int txTimeout, Class<T> resultClass) throws DataAccessException;

    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

}
