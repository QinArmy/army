package io.army.sync.executor;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.CurrentRecord;
import io.army.session.DataAccessException;
import io.army.session.OptimisticLockException;
import io.army.stmt.BatchStmt;
import io.army.stmt.MultiStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.TwoStmtQueryStmt;
import io.army.sync.MultiResult;
import io.army.sync.MultiStream;
import io.army.sync.StreamOptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
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
    List<Long> batchUpdate(BatchStmt stmt, int timeout, IntFunction<List<Long>> listConstructor,
                           @Nullable TableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;

    /**
     * @param domainTable <ul>
     *                    <li>null : multi-table batch update or the batch update which WITH clause support sub dml</li>
     *                    <li>{@link io.army.meta.ChildTableMeta} child batch update,now {@link MultiStmt#stmtItemList()} size must be even.</li>
     *                    <li>{@link io.army.meta.SingleTableMeta} single table batch update</li>
     *                    </ul>
     */
    List<Long> multiStmtBatchUpdate(BatchStmt stmt, int timeout, IntFunction<List<Long>> listConstructor,
                                    @Nullable TableMeta<?> domainTable);

    <T> List<T> query(SimpleStmt stmt, int timeout, Class<T> resultClass, Supplier<List<T>> listConstructor)
            throws DataAccessException;

    <R> List<R> queryObject(SimpleStmt stmt, int timeout, Supplier<R> constructor, Supplier<List<R>> listConstructor)
            throws DataAccessException;

    <R> List<R> queryRecord(SimpleStmt stmt, int timeout, Function<CurrentRecord, R> function,
                            Supplier<List<R>> listConstructor) throws DataAccessException;

    <R> int secondQuery(TwoStmtQueryStmt stmt, int timeout, Class<R> resultClass, List<R> resultList);

    <R> List<R> batchQuery(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;

    <R> List<R> batchQueryObject(BatchStmt stmt, int timeout, Supplier<R> Constructor, R terminator,
                                 Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;

    <R> List<R> batchQueryRecord(BatchStmt stmt, int timeout, Function<CurrentRecord, R> function, R terminator,
                                 Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;


    <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass, StreamOptions options)
            throws DataAccessException;

    <R> Stream<R> queryObjectStream(SimpleStmt stmt, int timeout, Supplier<R> constructor, StreamOptions options)
            throws DataAccessException;

    <R> Stream<R> queryRecordStream(SimpleStmt stmt, int timeout, Function<CurrentRecord, R> function,
                                    StreamOptions options) throws DataAccessException;

    <R> Stream<R> batchQueryStream(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                   StreamOptions options, boolean useMultiStmt) throws DataAccessException;

    <R> Stream<R> batchQueryObjectStream(BatchStmt stmt, int timeout, Supplier<R> constructor, R terminator,
                                         StreamOptions options, boolean useMultiStmt) throws DataAccessException;

    <R> Stream<R> batchQueryRecordStream(BatchStmt stmt, int timeout, Function<CurrentRecord, R> function, R terminator,
                                         StreamOptions options, boolean useMultiStmt) throws DataAccessException;


    MultiResult multiStmt(MultiStmt stmt, int timeout, StreamOptions options);

    MultiStream multiStmtStream(MultiStmt stmt, int timeout, StreamOptions options);


    Object createSavepoint() throws DataAccessException;

    void rollbackToSavepoint(Object savepoint) throws DataAccessException;

    void releaseSavepoint(Object savepoint) throws DataAccessException;

    void executeBatch(List<String> stmtList) throws DataAccessException;

    void execute(String stmt) throws DataAccessException;

    void close() throws DataAccessException;

}
