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

package io.army.reactive;

import io.army.criteria.*;
import io.army.session.*;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This interface representing a reactive database session.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link ReactiveLocalSession}</li>
 *     <li>{@link ReactiveRmSession}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface ReactiveSession extends Session, ReactiveCloseable {

    @Override
    ReactiveSessionFactory sessionFactory();

    /**
     * <p>Get current transaction info
     * <p><strong>NOTE</strong> : this method don't check session whether closed or not .
     *
     * @return current transaction info or null
     */
    @Nullable
    TransactionInfo currentTransactionInfo();

    /**
     * <p>Get current transaction info,If session in transaction block,then return current transaction info; else equivalent to {@link #sessionTransactionCharacteristics()}.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @throws SessionException emit(not throw) when database driver emit error.
     */
    Mono<TransactionInfo> transactionInfo();

    /**
     * <p>Query session-level transaction characteristics info
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @return the {@link Publisher} emit just one {@link TransactionInfo} or {@link Throwable}
     * <p><strong>NOTE</strong> : the {@link TransactionInfo#inTransaction()} always is false,even if session in transaction block.
     * @throws SessionException emit(not throw) when
     *                           <ul>
     *                              <li>network error</li>
     *                              <li>sever response error message,see {@link ServerException}</li>
     *                          </ul>
     * @see #setTransactionCharacteristics(TransactionOption)
     */
    Mono<TransactionInfo> sessionTransactionCharacteristics();


    /**
     * <p>Set session level transaction characteristics:
     * <ul>
     *     <li>These characteristics applies to all subsequent transactions performed within the current session,if you use appropriate default characteristic.</li>
     *     <li>This method is permitted within transactions ,but does not affect the current ongoing transaction.</li>
     *     <li>If you don't use appropriate default value,then appropriate characteristic does not affect new transaction,for example : {@link TransactionOption#isolation()} not null.</li>
     * </ul>
     * <pre>For example:
     *     <code><br/>
     *              TransactionOption.option(Isolation.REPEATABLE_READ)
     *              MySQL database will execute following sql :
     *              SET SESSION TRANSACTION READ WRITE , ISOLATION LEVEL REPEATABLE READ
     *     </code>
     * </pre>
     * <pre>For example:
     *     <code><br/>
     *              TransactionOption.option(Isolation.REPEATABLE_READ)
     *              PostgreSQL database will execute following sql :
     *              SET SESSION CHARACTERISTICS AS TRANSACTION READ WRITE, ISOLATION LEVEL REPEATABLE READ
     *     </code>
     * </pre>
     *
     * @see ReactiveLocalSession#startTransaction(TransactionOption, HandleMode)
     * @see ReactiveRmSession#start(Xid, int, TransactionOption)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/set-transaction.html">MySQL SET TRANSACTION Statement</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-set-transaction.html">PostgreSQL SET TRANSACTION Statement</a>
     */
    Mono<? extends ReactiveSession> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint();

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

    <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option);

    <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option);

    /*-------------------below query methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> query(DqlStatement statement, Class<R> resultClass);

    <R> Flux<R> query(DqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);


    /*-------------------below queryObject methods-------------------*/

    <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option);


    /*-------------------below queryRecord methods-------------------*/

    <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option);


    /*-------------------below save methods-------------------*/

    <T> Mono<ResultStates> save(T domain);

    <T> Mono<ResultStates> save(T domain, ReactiveStmtOption option);

    /*-------------------below update methods-------------------*/

    Mono<ResultStates> update(SimpleDmlStatement statement);

    Mono<ResultStates> update(SimpleDmlStatement statement, ReactiveStmtOption option);


    /*-------------------below batchSave methods-------------------*/

    <T> Mono<ResultStates> batchSave(List<T> domainList);

    <T> Mono<ResultStates> batchSave(List<T> domainList, LiteralMode literalMode);

    <T> Mono<ResultStates> batchSave(List<T> domainList, ReactiveStmtOption option);

    <T> Mono<ResultStates> batchSave(List<T> domainList, LiteralMode literalMode, ReactiveStmtOption option);


    /*-------------------below batchUpdate methods-------------------*/

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement);

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement, ReactiveStmtOption option);


    /*-------------------below batchQuery methods-------------------*/

//    QueryResults batchQueryResults(BatchDqlStatement statement);
//
//    QueryResults batchQueryResults(BatchDqlStatement statement, ReactiveOption option);


    /*-------------------below multiStmt methods-------------------*/

//    MultiResult multiStmt(MultiResultStatement statement);

//    Flux<ResultItem> execute(DqlStatement statement);


//    Mono<ReactiveCursor> declareCursor(DeclareCursor statement);


}
