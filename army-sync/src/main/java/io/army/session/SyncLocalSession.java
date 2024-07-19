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

import io.army.executor.StmtExecutor;
import io.army.option.Option;
import io.army.transaction.HandleMode;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing local {@link SyncSession} that support database local transaction.
 * <p>The instance of this interface is created by {@link SyncSessionFactory.LocalSessionBuilder}.
 * <p>This interface's directly underlying api is {@link StmtExecutor}.
 * <p>This interface representing high-level database session. This interface's underlying database session is one of
 * <ul>
 *     <li>{@code java.sql.Connection}</li>
 *     <li>other database driver spi</li>
 * </ul>
 *
 * @see SyncSessionFactory
 * @since 0.6.0
 */
public sealed interface SyncLocalSession extends SyncSession, PackageSession.PackageLocalSession permits ArmySyncLocalSession {


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.startTransaction(TransactionOption.option(),HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     * </pre>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    TransactionInfo startTransaction();

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.startTransaction(option,HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     * </pre>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    TransactionInfo startTransaction(TransactionOption option);

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
     * <p><strong>NOTE</strong>:
     * <ul>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always non-null.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#DEFAULT_ISOLATION} always non-null.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with option</li>
     * </ul>
     * @param option non-null,if {@link TransactionOption#isolation()} is {@link Isolation#PSEUDO},then start pseudo transaction.
     * @param mode   non-null,
     *               <ul>
     *                  <li>{@link HandleMode#ERROR_IF_EXISTS} :  if session exists transaction then throw {@link SessionException}</li>
     *                  <li>{@link HandleMode#COMMIT_IF_EXISTS} :  if session exists transaction then commit existing transaction.</li>
     *                  <li>{@link HandleMode#ROLLBACK_IF_EXISTS} :  if session exists transaction then rollback existing transaction.</li>
     *               </ul>
     * @throws IllegalArgumentException                  throw when pseudo transaction {@link TransactionOption#isReadOnly()} is false.
     * @throws java.util.ConcurrentModificationException throw when concurrent control transaction
     * @throws SessionException                          throw when
     *                                                   <ul>
     *                                                       <li>session have closed</li>
     *                                                       <li>pseudo transaction and {@link #isReadonlySession()} is false</li>
     *                                                       <li>mode is {@link HandleMode#ERROR_IF_EXISTS} and {@link #hasTransactionInfo()} is true</li>
     *                                                       <li>mode is {@link HandleMode#COMMIT_IF_EXISTS} and commit failure</li>
     *                                                       <li>mode is {@link HandleMode#ROLLBACK_IF_EXISTS} and rollback failure</li>
     *                                                   </ul>
     */
    TransactionInfo startTransaction(TransactionOption option, HandleMode mode);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.commit(Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #commit(Function)
     */
    void commit();


    /**
     * <p>Execute COMMIT command with dialect option or clear pseudo transaction
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     * <ul>
     *    <li>{@link Option#CHAIN}</li>
     *    <li>{@link Option#RELEASE}</li>
     * </ul>
     * <ul>
     *     <li>If session exist pseudo transaction ,then this method clear pseudo transaction only and don't access database server.</li>
     *     <li>Else this method always execute COMMIT command with dialect option,even if no transaction,because army is high-level database driver.</li>
     * </ul>
     * <p>You can use {@link #commitIfExists(Function)} instead of this method.
     *
     * @param optionFunc non-null, dialect option function. see {@link Option#EMPTY_FUNC}
     * @return <ul>
     *     <li>new transaction info :  {@link Option#CHAIN} is {@link Boolean#TRUE},new transaction info contain new {@link Option#START_MILLIS} value.</li>
     *     <li>null : {@link Option#CHAIN} isn't {@link Boolean#TRUE}</li>
     * </ul>
     * @throws IllegalArgumentException throw when
     *                                  <ul>
     *                                      <li>{@link Option#CHAIN} is {@link Boolean#TRUE} and {@link Option#RELEASE} is {@link Boolean#TRUE}</li>
     *                                      <li>{@link Option#CHAIN} isn't null and database server don't support that.</li>
     *                                      <li>{@link Option#RELEASE} isn't null and database server don't support that.</li>
     *                                  </ul>
     * @throws SessionException         throw when
     *                                  <ul>
     *                                      <li>session have closed</li>
     *                                      <li>{@link #isRollbackOnly()} is true</li>
     *                                      <li>commit failure</li>
     *                                  </ul>
     */
    @Nullable
    TransactionInfo commit(Function<Option<?>, ?> optionFunc);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.commitIfExists(Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #commitIfExists(Function)
     */
    void commitIfExists();

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             if(session.hasTransactionInfo()){
     *                  session.commit(Option.EMPTY_FUNC) ;
     *             }
     *         </code>
     * </pre>
     *
     * @see #commit(Function)
     */
    @Nullable
    TransactionInfo commitIfExists(Function<Option<?>, ?> optionFunc);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.rollback(Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #rollback(Function)
     */
    void rollback();

    /**
     * <p>Execute ROLLBACK command with dialect option or clear pseudo transaction
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     * <ul>
     *    <li>{@link Option#CHAIN}</li>
     *    <li>{@link Option#RELEASE}</li>
     * </ul>
     * <ul>
     *     <li>If session exist pseudo transaction ,then this method clear pseudo transaction only and don't access database server.</li>
     *     <li>Else this method always execute ROLLBACK command with dialect option,even if no transaction,because army is high-level database driver.</li>
     * </ul>
     * <p>You can use {@link #rollbackIfExists(Function)} instead of this method.
     *
     * @param optionFunc non-null, dialect option function. see {@link Option#EMPTY_FUNC}
     * @return <ul>
     *     <li>new transaction info :  {@link Option#CHAIN} is {@link Boolean#TRUE},new transaction info contain new {@link Option#START_MILLIS} value.</li>
     *     <li>null : {@link Option#CHAIN} isn't {@link Boolean#TRUE}</li>
     * </ul>
     * @throws IllegalArgumentException throw when
     *                                  <ul>
     *                                      <li>{@link Option#CHAIN} is {@link Boolean#TRUE} and {@link Option#RELEASE} is {@link Boolean#TRUE}</li>
     *                                      <li>{@link Option#CHAIN} isn't null and database server don't support that.</li>
     *                                      <li>{@link Option#RELEASE} isn't null and database server don't support that.</li>
     *                                  </ul>
     * @throws SessionException         throw when
     *                                  <ul>
     *                                      <li>session have closed</li>
     *                                      <li>rollback failure</li>
     *                                  </ul>
     */
    @Nullable
    TransactionInfo rollback(Function<Option<?>, ?> optionFunc);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             session.rollbackIfExists(Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #rollbackIfExists(Function)
     */
    void rollbackIfExists();

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncLocalSession}
     *             if(session.hasTransactionInfo()){
     *                  session.rollback(Option.EMPTY_FUNC) ;
     *             }
     *         </code>
     * </pre>
     *
     * @see #rollback(Function)
     */
    @Nullable
    TransactionInfo rollbackIfExists(Function<Option<?>, ?> optionFunc);


}
