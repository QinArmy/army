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

package io.army.session;

import io.army.option.Option;
import io.army.transaction.HandleMode;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>This interface representing reactive local session that support database local transaction.
 * <p>The instance of this interface is create by {@link ReactiveSessionFactory.LocalSessionBuilder#build()}
 *
 * @see ReactiveSessionFactory
 * @since 0.6.0
 */
public sealed interface ReactiveLocalSession extends ReactiveSession, PackageSession.PackageLocalSession
        permits ArmyReactiveLocalSession {


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link ReactiveLocalSession}
     *             session.startTransaction(TransactionOption.option(),HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     * </pre>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    Mono<TransactionInfo> startTransaction();

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link ReactiveLocalSession}
     *             session.startTransaction(option,HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     * </pre>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    Mono<TransactionInfo> startTransaction(TransactionOption option);

    /**
     * <p>Start local/pseudo transaction.
     * <ul>
     *     <li>Local transaction is supported by database server.</li>
     *     <li>Pseudo transaction({@link TransactionOption#isolation()} is {@link Isolation#PSEUDO}) is supported only by army readonly session.
     *     Pseudo transaction is designed for some framework in readonly transaction,for example
     *     {@code org.springframework.transaction.PlatformTransactionManager}
     *     </li>
     * </ul>
     * <strong>NOTE</strong>: if option representing pseudo transaction,then this method don't access database server.
     *
     * <p>Army prefer to start local transaction with one sql statement or multi-statement( if driver support),because transaction starting should keep atomicity and reduce network overhead.
     * <pre>For example: {@code TransactionOption.option(Isolation.READ_COMMITTED)},MySQL database will execute following sql :
     *     <code><br/>
     *             SET TRANSACTION ISOLATION LEVEL READ COMMITTED ; START TRANSACTION READ WRITE
     *     </code>
     *     {@code TransactionOption.option()},MySQL database will execute following sql :
     *     <code><br/>
     *             SET @@transaction_isolation = @@SESSION.transaction_isolation ; SELECT @@SESSION.transaction_isolation AS txIsolationLevel ; START TRANSACTION READ WRITE
     *             // SET @@transaction_isolation = @@SESSION.transaction_isolation to  guarantee isolation is session isolation
     *     </code>
     * </pre>
     * <pre>For example : {@code TransactionOption.option(Isolation.READ_COMMITTED)},PostgreSQL database will execute following sql :
     *     <code><br/>
     *             START TRANSACTION ISOLATION LEVEL READ COMMITTED , READ WRITE
     *     </code>
     * </pre>
     *
     * @param option non-null,if {@link TransactionOption#isolation()} is {@link Isolation#PSEUDO},then start pseudo transaction.
     * @param mode   non-null,
     *               <ul>
     *                  <li>{@link HandleMode#ERROR_IF_EXISTS} :  if session exists transaction then throw {@link SessionException}</li>
     *                  <li>{@link HandleMode#COMMIT_IF_EXISTS} :  if session exists transaction then commit existing transaction.</li>
     *                  <li>{@link HandleMode#ROLLBACK_IF_EXISTS} :  if session exists transaction then rollback existing transaction.</li>
     *               </ul>
     * @throws IllegalArgumentException                  emit(not throw) when pseudo transaction {@link TransactionOption#isReadOnly()} is false.
     * @throws java.util.ConcurrentModificationException emit(not throw) when concurrent control transaction
     * @throws SessionException                          emit(not throw) when
     *                                                   <ul>
     *                                                       <li>session have closed</li>
     *                                                       <li>pseudo transaction and {@link #isReadonlySession()} is false</li>
     *                                                       <li>mode is {@link HandleMode#ERROR_IF_EXISTS} and {@link #hasTransactionInfo()} is true</li>
     *                                                       <li>mode is {@link HandleMode#COMMIT_IF_EXISTS} and commit failure</li>
     *                                                       <li>mode is {@link HandleMode#ROLLBACK_IF_EXISTS} and rollback failure</li>
     *                                                   </ul>
     */
    Mono<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode);

    Mono<ReactiveLocalSession> commit();

    Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

    Mono<ReactiveLocalSession> commitIfExists();

    Mono<Optional<TransactionInfo>> commitIfExists(Function<Option<?>, ?> optionFunc);

    Mono<ReactiveLocalSession> rollback();

    Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);

    Mono<ReactiveLocalSession> rollbackIfExists();

    Mono<Optional<TransactionInfo>> rollbackIfExists(Function<Option<?>, ?> optionFunc);

    @Override
    Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option);

    @Override
    Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint);


    @Override
    Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    @Override
    Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint);

    @Override
    Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);


}
