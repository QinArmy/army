package io.army.sync.executor;


import io.army.meta.TableMeta;
import io.army.session.*;
import io.army.session.executor.StmtExecutor;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleSqlStmt;
import io.army.stmt.TwoStmtModeQuerySpec;
import io.army.sync.SyncStmtOption;

import javax.annotation.Nullable;
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
 * so all implementation of methods of this interface don't check underlying database session whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * <p>The instance of this interface is created by {@link SyncStmtExecutorFactory}.
 *
 * @see SyncStmtExecutorFactory
 * @since 1.0
 */
public interface SyncStmtExecutor extends StmtExecutor, AutoCloseable {


    /**
     * <p>Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * <br/>
     *
     * @return {@link io.army.env.SyncKey#SESSION_IDENTIFIER_ENABLE} : <ul>
     * <li>true :  session identifier </li>
     * <li>false (default) : always 0 , because JDBC spi don't support get server process id (or server thread id)</li>
     * </ul>
     * @throws DataAccessException throw when underlying database session have closed
     */
    @Override
    long sessionIdentifier() throws DataAccessException;

    TransactionInfo transactionInfo() throws DataAccessException;

    void setTransactionCharacteristics(TransactionOption option) throws DataAccessException;

    Object setSavePoint(Function<Option<?>, ?> optionFunc) throws DataAccessException;

    void releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) throws DataAccessException;

    void rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) throws DataAccessException;


    /**
     * <p>
     * Execute INSERT statement that possibly get auto generated key.
     * </p>
     *
     * @throws io.army.session.DataAccessException when access database occur error.
     * @throws io.army.ArmyException               throw when:<ul>
     *                                             <li>param error</li>
     *                                             </ul>
     * @throws IllegalArgumentException            throw when timeout negative.
     */
    long insertAsLong(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;

    ResultStates insert(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;

    /**
     * <p>
     * Executor non-insert dml.
     * </p>
     *
     * @throws io.army.session.DataAccessException when access database occur error.
     * @throws io.army.ArmyException               throw when:<ul>
     *                                             <li>param error</li>
     *                                             </ul>
     * @throws IllegalArgumentException            throw when timeout negative.
     */
    long updateAsLong(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;

    ResultStates update(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException;

    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    List<Long> batchUpdateList(BatchStmt stmt, IntFunction<List<Long>> listConstructor, SyncStmtOption option,
                               @Nullable TableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;

    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    Stream<Long> batchUpdate(BatchStmt stmt, IntFunction<List<Long>> listConstructor, SyncStmtOption option,
                             @Nullable TableMeta<?> domainTable, @Nullable List<Long> rowsList) throws DataAccessException;

    <T> Stream<T> query(SingleSqlStmt stmt, Class<T> resultClass, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> secondQuery(TwoStmtModeQuerySpec stmt, SyncStmtOption option, List<R> resultList);


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

        TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        int prepare(Xid xid, Function<Option<?>, ?> optionFunc);

        void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        void rollback(Xid xid, Function<Option<?>, ?> optionFunc);

        void forget(Xid xid, Function<Option<?>, ?> optionFunc);

        List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc);

        Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc);


    }


}
