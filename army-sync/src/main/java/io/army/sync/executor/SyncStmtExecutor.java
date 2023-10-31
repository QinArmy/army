package io.army.sync.executor;


import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.session.*;
import io.army.session.executor.StmtExecutor;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleSqlStmt;
import io.army.stmt.Stmt;
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


    <R> R insert(SimpleStmt stmt, SyncStmtOption option, Class<R> resultClass) throws DataAccessException;


    <R> R update(SimpleStmt stmt, SyncStmtOption option, Class<R> resultClass, Function<Option<?>, ?> optionFunc)
            throws DataAccessException;

    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    <R> List<R> batchUpdateList(BatchStmt stmt, IntFunction<List<R>> listConstructor, SyncStmtOption option,
                                Class<R> elementClass, @Nullable TableMeta<?> domainTable,
                                @Nullable List<R> rowsList) throws DataAccessException;

    /**
     * @return a unmodified list.
     * @throws OptimisticLockException when
     */
    <R> Stream<R> batchUpdate(BatchStmt stmt, SyncStmtOption option, Class<R> elementClass,
                              @Nullable TableMeta<?> domainTable, @Nullable List<R> rowsList)
            throws DataAccessException;


    @Nullable
    <R> R queryOne(SimpleStmt stmt, Class<R> resultClass, SyncStmtOption option)
            throws DataAccessException;

    @Nullable
    <R> R queryOneObject(SimpleStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException;

    @Nullable
    <R> R queryOneRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> query(SingleSqlStmt stmt, Class<R> resultClass, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option)
            throws DataAccessException;

    <R> Stream<R> pairQuery(Stmt.PairStmtSpec stmt, Class<R> resultClass, SyncStmtOption option, boolean firstIsQuery,
                            ChildTableMeta<?> childTable, boolean insert) throws DataAccessException;

    <R> Stream<R> pairQueryObject(Stmt.PairStmtSpec stmt, Supplier<R> constructor, SyncStmtOption option, boolean firstIsQuery,
                                  ChildTableMeta<?> childTable, boolean insert) throws DataAccessException;

    <R> Stream<R> pairQueryRecord(Stmt.PairStmtSpec stmt, Function<CurrentRecord, R> function, SyncStmtOption option, boolean firstIsQuery,
                                  ChildTableMeta<?> childTable, boolean insert) throws DataAccessException;

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
