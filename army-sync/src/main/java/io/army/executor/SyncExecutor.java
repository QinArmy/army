/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.executor;


import io.army.lang.Nullable;
import io.army.option.Option;
import io.army.result.CurrentRecord;
import io.army.result.ResultStates;
import io.army.result.SyncBatchQuery;
import io.army.session.RmSession;
import io.army.session.RmSessionException;
import io.army.session.StreamOption;
import io.army.session.SyncStmtOption;
import io.army.spec.CloseableSpec;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleSqlStmt;
import io.army.transaction.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link StmtExecutor}
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@link SyncLocalExecutor}</li>
 *     <li>{@link SyncRmExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link CloseableSpec},
 * so all implementation of methods of this interface don't check underlying database session whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 * <p>The instance of this interface is created by {@link SyncExecutorFactory}.
 *
 * @see SyncExecutorFactory
 * @since 0.6.0
 */
public interface SyncExecutor extends StmtExecutor, AutoCloseable {


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
    long sessionIdentifier(Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    TransactionInfo transactionInfo(Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    TransactionInfo sessionTransactionCharacteristics(Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    void setTransactionCharacteristics(TransactionOption option, Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    Object setSavePoint(Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    void releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws DataAccessException;

    void rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws DataAccessException;


    <R> R update(SimpleStmt stmt, SyncStmtOption option, Class<R> resultClass, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException;

    /**
     * <p>This method is designed to be compatible with jdbc.
     * <p>If listConstructor is null ,then this method always return {@link Collections#emptyList()}.
     *
     * @return a unmodified list
     */
    List<Long> batchUpdateList(BatchStmt stmt, @Nullable IntFunction<List<Long>> listConstructor, SyncStmtOption option,
                               @Nullable LongConsumer consumer, Function<Option<?>, ?> sessionFunc) throws DataAccessException;


    Stream<ResultStates> batchUpdate(BatchStmt stmt, SyncStmtOption option, Function<Option<?>, ?> optionFunc);


    <R> Stream<R> query(SingleSqlStmt stmt, Function<? super CurrentRecord, R> function, SyncStmtOption option, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException;

    SyncBatchQuery batchQuery(BatchStmt stmt, SyncStmtOption option, Function<Option<?>, ?> sessionFunc);


    @Override
    void close() throws DataAccessException;

    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 0.6.0
     */
    interface LocalTransactionSpec {

        TransactionInfo startTransaction(TransactionOption option, HandleMode mode, Function<Option<?>, ?> sessionFunc);

        @Nullable
        TransactionInfo commit(Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc);

        @Nullable
        TransactionInfo rollback(Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc);

    }

    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 0.6.0
     */
    interface XaTransactionSpec {

        TransactionInfo start(Xid xid, int flags, TransactionOption option, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        /**
         * @param xid        target transaction xid
         * @param optionFunc dialect option function
         * @return flags :
         * <ul>
         *     <li>{@link RmSession#XA_OK} :  prepared</li>
         *     <li>{@link RmSession#XA_RDONLY} : appropriate transaction is readonly and have committed with one phase</li>
         * </ul>
         * @throws RmSessionException throw when
         *                            <ol>
         *                                <li>xid and appropriate transaction not match</li>
         *                                <li>appropriate transaction {@link XaStates} isn't {@link XaStates#IDLE}</li>
         *                                <li>appropriate transaction is rollback only ,for example : current transaction's {@link RmSession#TM_FAIL} is set </li>
         *                                <li>database server response error message</li>
         *                            </ol>
         */
        int prepare(Xid xid, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        void rollback(Xid xid, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        void forget(Xid xid, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws RmSessionException;

        Stream<Xid> recover(int flags, Function<Option<?>, ?> optionFunc, StreamOption option, Function<Option<?>, ?> sessionFunc) throws RmSessionException;


    }


}
