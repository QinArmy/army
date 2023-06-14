package io.army.sync.executor;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.DataAccessException;
import io.army.session.OptimisticLockException;
import io.army.stmt.BatchStmt;
import io.army.stmt.MultiStmt;
import io.army.stmt.SimpleStmt;
import io.army.sync.MultiResult;
import io.army.sync.MultiStream;
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

    boolean isSupportClientStream();


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
                           @Nullable TableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;

    /**
     * @param domainTable <ul>
     *                    <li>null : multi-table batch update or the batch update which WITH clause support sub dml</li>
     *                    <li>{@link io.army.meta.ChildTableMeta} child batch update,now {@link MultiStmt#stmtItemList()} size must be even.</li>
     *                    <li>{@link io.army.meta.SingleTableMeta} single table batch update</li>
     *                    </ul>
     */
    List<Long> multiStmtBatchUpdate(MultiStmt stmt, int timeout, Supplier<List<Long>> listConstructor,
                                    @Nullable TableMeta<?> domainTable);

    <T> List<T> query(SimpleStmt stmt, int timeout, Class<T> resultClass, Supplier<List<T>> listConstructor)
            throws DataAccessException;

    List<Map<String, Object>> queryAsMap(SimpleStmt stmt, int timeout, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;

    <R> List<R> batchQuery(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor) throws DataAccessException;

    List<Map<String, Object>> batchQueryAsMap(BatchStmt stmt, int timeout, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator,
                                              Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;

    <R> List<R> multiStmtBatchQuery(MultiStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                    Supplier<List<R>> listConstructor) throws DataAccessException;

    List<Map<String, Object>> multiStmtBatchQueryAsMap(MultiStmt stmt, int timeout,
                                                       Supplier<Map<String, Object>> mapConstructor,
                                                       Map<String, Object> terminator,
                                                       Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;


    <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass, StreamOptions options)
            throws DataAccessException;

    Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                               Supplier<Map<String, Object>> mapConstructor, StreamOptions options)
            throws DataAccessException;

    <R> Stream<R> batchQueryStream(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                   StreamOptions options) throws DataAccessException;

    Stream<Map<String, Object>> batchQueryMapStream(BatchStmt stmt, int timeout,
                                                    Supplier<Map<String, Object>> mapConstructor,
                                                    Map<String, Object> terminator, StreamOptions options)
            throws DataAccessException;

    <R> Stream<R> multiStmtBatchQueryStream(MultiStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                            StreamOptions options) throws DataAccessException;

    Stream<Map<String, Object>> multiStmtBatchQueryMapStream(MultiStmt stmt, int timeout,
                                                             Supplier<Map<String, Object>> mapConstructor,
                                                             Map<String, Object> terminator, StreamOptions options)
            throws DataAccessException;

    MultiResult multiStmt(MultiStmt stmt, int timeout, @Nullable StreamOptions options);

    MultiStream multiStmtStream(MultiStmt stmt, int timeout, @Nullable StreamOptions options);


    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

    void close() throws DataAccessException;

}
