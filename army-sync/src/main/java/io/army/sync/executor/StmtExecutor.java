package io.army.sync.executor;

import io.army.session.DataAccessException;
import io.army.session.Executor;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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

    <T> List<T> select(SimpleStmt stmt, int timeout, Class<T> resultClass, Supplier<List<T>> listConstructor)
            throws DataAccessException;

    List<Map<String, Object>> selectAsMap(SimpleStmt stmt, int timeout, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;

    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

    void close() throws DataAccessException;

}
