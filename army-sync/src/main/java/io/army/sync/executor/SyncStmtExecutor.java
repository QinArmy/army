package io.army.sync.executor;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.*;
import io.army.session.executor.StmtExecutor;
import io.army.stmt.*;
import io.army.sync.MultiResult;
import io.army.sync.MultiStream;
import io.army.sync.StreamOptions;
import io.army.sync.SyncStmtOption;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link StmtExecutor}
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@link SyncLocalStmtExecutor}</li>
 *     <li>{@link SyncRmStmtExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * <p>The instance of this interface is created by {@link SyncStmtExecutorFactory}.
 *
 * @see SyncStmtExecutorFactory
 * @since 1.0
 */
public interface SyncStmtExecutor extends StmtExecutor, AutoCloseable {


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
    long insert(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;

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
    long update(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;


    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    List<Long> batchUpdate(BatchStmt stmt, IntFunction<List<Long>> listConstructor, SyncStmtOption option,
                           @Nullable TableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;


    <T> List<T> query(SimpleStmt stmt, Class<T> resultClass, Supplier<List<T>> listConstructor, SyncStmtOption option)
            throws DataAccessException;

    <R> List<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option)
            throws DataAccessException;

    <R> List<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option) throws DataAccessException;

    <R> int secondQuery(TwoStmtQueryStmt stmt, SyncStmtOption option, List<R> resultList);

    <R> int secondBatchQuery(TwoStmtBatchQueryStmt stmt, int timeout, List<R> resultList);

    <R> List<R> batchQuery(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;

    <R> List<R> batchQueryObject(BatchStmt stmt, int timeout, Supplier<R> Constructor, R terminator,
                                 Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;

    <R> List<R> batchQueryRecord(BatchStmt stmt, int timeout, Function<CurrentRecord, R> function, R terminator,
                                 Supplier<List<R>> listConstructor, boolean useMultiStmt) throws DataAccessException;


    <R> Stream<R> queryStream(SimpleStmt stmt, Class<R> resultClass, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryObjectStream(SimpleStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryRecordStream(SimpleStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option) throws DataAccessException;

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

    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface LocalTransactionSpec {

        TransactionInfo startTransaction(TransactionOption option);

        @Nullable
        TransactionInfo commit(Function<Option<?>, ?> optionFunc);

        @Nullable
        TransactionInfo rollback(Function<Option<?>, ?> optionFunc);

    }

    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface XaTransactionSpec {

        TransactionInfo start(Xid xid, int flags, TransactionOption option);

        void end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        int prepare(Xid xid, Function<Option<?>, ?> optionFunc);

        void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        void rollback(Xid xid, Function<Option<?>, ?> optionFunc);

        void forget(Xid xid, Function<Option<?>, ?> optionFunc);

        List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc);

        Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc);


    }


}
