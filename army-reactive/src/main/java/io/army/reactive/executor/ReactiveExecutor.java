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

package io.army.reactive.executor;


import io.army.executor.StmtExecutor;
import io.army.reactive.ReactiveCloseable;
import io.army.reactive.ReactiveStmtOption;
import io.army.record.CurrentRecord;
import io.army.record.ResultStates;
import io.army.session.*;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleSqlStmt;
import io.army.stmt.TwoStmtQueryStmt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This interface representing reactive {@link StmtExecutor}.
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@link ReactiveLocalExecutor}</li>
 *     <li>{@link ReactiveRmExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @see ReactiveExecutorFactory
 * @since 0.6.0
 */
public interface ReactiveExecutor extends StmtExecutor, ReactiveCloseable {

    Mono<TransactionInfo> transactionInfo();

    Mono<TransactionInfo> sessionTransactionCharacteristics(Function<Option<?>, ?> optionFunc);

    Mono<Void> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<ResultStates> insert(SimpleStmt stmt, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    Mono<ResultStates> update(SimpleStmt stmt, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    Flux<ResultStates> batchUpdate(BatchStmt stmt, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    <R> Flux<R> query(SingleSqlStmt stmt, Class<R> resultClass, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    <R> Flux<Optional<R>> queryOptional(SingleSqlStmt stmt, Class<R> resultClass, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    <R> Flux<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    <R> Flux<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    <R> Flux<R> secondQuery(TwoStmtQueryStmt stmt, ReactiveStmtOption option, List<R> resultList, Function<Option<?>, ?> optionFunc);



    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 0.6.0
     */
    interface LocalTransactionSpec {

        Mono<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode);

        Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

        Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);

    }


    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 0.6.0
     */
    interface XaTransactionSpec {

        Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option);

        Mono<TransactionInfo> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc);

        Mono<Void> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        Mono<Void> rollback(Xid xid, Function<Option<?>, ?> optionFunc);


        Mono<Void> forget(Xid xid, Function<Option<?>, ?> optionFunc);

        Flux<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


    }


}
