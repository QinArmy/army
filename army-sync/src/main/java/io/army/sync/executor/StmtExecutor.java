package io.army.sync.executor;


import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.session.DataAccessException;
import io.army.session.OptimisticLockException;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.sync.StreamOptions;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>
 * This interface representing sql statement executor with blocking way.
 * </p>
 *
 * @since 1.0
 */
public interface StmtExecutor {


    /**
     * <p>
     * Execute INSERT statement that possibly get auto generated key.
     * </p>
     *
     * @param timeout seconds
     * @throws io.army.session.DataAccessException when access database occur error.
     * @throws io.army.ArmyException               throw when:<ul>
     *                                             <li>param error</li>
     *                                             </ul>
     * @throws IllegalArgumentException            throw when timeout negative.
     */
    long insert(SimpleStmt stmt, int timeout) throws DataAccessException;

    /**
     * <p>
     * Executor non-insert dml.
     * </p>
     *
     * @param timeout seconds
     * @throws io.army.session.DataAccessException when access database occur error.
     * @throws io.army.ArmyException               throw when:<ul>
     *                                             <li>param error</li>
     *                                             </ul>
     * @throws IllegalArgumentException            throw when timeout negative.
     */
    long update(SimpleStmt stmt, int timeout) throws DataAccessException;


    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    List<Long> batchUpdate(BatchStmt stmt, int timeout, Supplier<List<Long>> listConstructor,
                           @Nullable ChildTableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;

    <T> List<T> query(SimpleStmt stmt, int timeout, Class<T> resultClass, Supplier<List<T>> listConstructor)
            throws DataAccessException;

    List<Map<String, Object>> queryAsMap(SimpleStmt stmt, int timeout, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;


    <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass, StreamOptions options);

    Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                               Supplier<Map<String, Object>> mapConstructor, StreamOptions options);


    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

    void close() throws DataAccessException;

}
