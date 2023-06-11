package io.army.sync.executor;

import io.army.lang.Nullable;
import io.army.session.DataAccessException;
import io.army.session.OptimisticLockException;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface StmtExecutor {


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
    <T> List<T> returningUpdate(Stmt stmt, int txTimeout, Class<T> resultClass) throws DataAccessException;

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    List<Map<String, Object>> returnInsertAsMap(Stmt stmt, int txTimeout, Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException;

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    List<Map<String, Object>> returningUpdateAsMap(Stmt stmt, int txTimeout, Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException;

    /**
     * @throws io.army.session.DataAccessException when access database occur error.
     */
    long update(SimpleStmt stmt, int timeout) throws DataAccessException;

    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    List<Long> batchUpdate(BatchStmt stmt, int timeout) throws DataAccessException;

    <T> List<T> query(SimpleStmt stmt, int timeout, Class<T> resultClass, Supplier<List<T>> listConstructor)
            throws DataAccessException;

    List<Map<String, Object>> queryAsMap(SimpleStmt stmt, int timeout, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;


    <R> Stream<R> queryStream(SimpleStmt stmt, Class<R> resultClass,
                              boolean serverStream, int fetchSize,
                              @Nullable Comparable<? super R> comparator, boolean parallel);

    Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, Supplier<Map<String, Object>> mapConstructor,
                                               boolean serverStream, int fetchSize,
                                               @Nullable Comparable<Map<String, Object>> comparator, boolean parallel);


    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

    void close() throws DataAccessException;

}
