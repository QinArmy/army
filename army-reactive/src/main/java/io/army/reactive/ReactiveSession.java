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

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.DqlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.session.*;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
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
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @throws SessionException emit(not throw) when database driver emit error.
     */
    Mono<TransactionInfo> transactionInfo();

    Mono<? extends ReactiveSession> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint();

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

    <R> Mono<Optional<R>> queryOneNullable(SimpleDqlStatement statement, Class<R> resultClass);

    /**
     * <p>Query at most one nullable row that consist of single column.
     *
     * @param statement   simple(non-batch) query statement
     * @param resultClass result class ,for example : {@code  String.class}, {@code Long.class}
     * @param <R>         the java type of row
     * @throws NonSingleRowException emit(not throw) when server response row count more than one row.
     */
    <R> Mono<Optional<R>> queryOneNullable(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

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


    /*-------------------below queryOptional methods-------------------*/


    <R> Flux<Optional<R>> queryNullable(DqlStatement statement, Class<R> resultClass);

    <R> Flux<Optional<R>> queryNullable(DqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

    /*-------------------below queryObject methods-------------------*/

    <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option);


    /*-------------------below queryRecord methods-------------------*/

    <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option);


    /*-------------------below save methods-------------------*/

    Mono<ResultStates> save(Object domain);

    /*-------------------below update methods-------------------*/

    Mono<ResultStates> update(SimpleDmlStatement statement);

    Mono<ResultStates> update(SimpleDmlStatement statement, ReactiveStmtOption option);


    /*-------------------below batchSave methods-------------------*/

    <T> Mono<ResultStates> batchSave(List<T> domainList);


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
