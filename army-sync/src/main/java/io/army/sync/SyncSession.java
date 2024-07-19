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

package io.army.sync;

import io.army.criteria.*;
import io.army.executor.ServerException;
import io.army.option.Option;
import io.army.result.NonMonoException;
import io.army.session.*;
import io.army.result.CurrentRecord;
import io.army.result.ResultStates;
import io.army.transaction.*;

import java.io.Closeable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link Session}.
 * <p>This interface is base interface of below interface:
 * <ul>
 *     <li>{@link SyncLocalSession}</li>
 *     <li>{@link SyncRmSession}</li>
 * </ul>
 * <p>This interface's directly underlying api is {@link io.army.sync.executor.SyncExecutor}.
 * <p>This interface representing high-level database session. This interface's underlying database session is one of
 * <ul>
 *     <li>{@code java.sql.Connection}</li>
 *     <li>other database driver spi</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SyncSession extends Session, Closeable {


    /**
     * <p>Session identifier(non-unique, for example : database server cluster),probably is following :
     * <ul>
     *    <li>server process id</li>
     *    <li>server thread id</li>
     *    <li>other identifier</li>
     * </ul>
     * <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     *
     * @return {@link io.army.env.SyncKey#SESSION_IDENTIFIER_ENABLE} : <ul>
     * <li>true :  session identifier </li>
     * <li>false (default) : always 0 , because JDBC spi don't support get server process id (or server thread id)</li>
     * </ul>
     * @throws SessionException throw when underlying database session have closed
     */
    @Override
    long sessionIdentifier() throws SessionException;

    /**
     * <p>Get the {@link SyncSessionFactory} that create this session instance.
     * <p>This method don't check session whether closed or not.
     *
     * @return session factory of this instance.
     */
    @Override
    SyncSessionFactory sessionFactory();

    /**
     * <p>Get current transaction info,If session in transaction block,then return current transaction info; else equivalent to {@link #sessionTransactionCharacteristics()}.
     * <ul>
     *     <li>If session exists transaction ,then return transaction info</li>
     *     <li>Else query session level transaction info ,for example : isolation ,readonly etc.</li>
     * </ul>
     * <pre>
     *     The implementation of this method like following :
     *     <code><br/>
     *          TransactionInfo info = this.transactionInfo;
     *         if(info == null){
     *              // this.executor is a instance of {@link io.army.sync.executor.SyncExecutor}
     *             info = this.executor.transactionInfo(); // query session level transaction info
     *         }
     *         return info;
     *     </code>
     * </pre>
     *
     * @return session transaction info session have closed
     * @throws SessionException throw when
     */
    TransactionInfo transactionInfo();

    /**
     * <p>Query session-level transaction characteristics info
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @return {@link TransactionInfo}
     * <p><strong>NOTE</strong> : the {@link TransactionInfo#inTransaction()} always is false,even if session in transaction block.
     * @throws SessionException emit(not throw) when
     *                           <ul>
     *                              <li>network error</li>
     *                              <li>sever response error message,see {@link ServerException}</li>
     *                          </ul>
     * @see #setTransactionCharacteristics(TransactionOption)
     */
    TransactionInfo sessionTransactionCharacteristics();

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncSession}
     *             session.setSavePoint(Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #setSavePoint(Function)
     */
    Object setSavePoint();

    /**
     * <p>Set a save point.
     * <p>If {@link Option#NAME} non-null,then use name value set a save point
     * <p>If session exists pseudo transaction ,then this method don't access database server.
     *
     * @param optionFunc option function,see {@link Option#EMPTY_FUNC}
     * @return save point :
     * <ul>
     *     <li>real save point : real transaction</li>
     *     <li>pseudo save point : pseudo transaction</li>
     * </ul>
     * @throws SessionException throw when
     *                          <ul>
     *                              <li>session have closed</li>
     *                              <li>session not in transaction(real/pseudo)</li>
     *                              <li>set save point failure</li>
     *                          </ul>
     */
    Object setSavePoint(Function<Option<?>, ?> optionFunc);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncSession}
     *             session.releaseSavePoint(savepoint,Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #releaseSavePoint(Object, Function)
     */
    void releaseSavePoint(Object savepoint);

    /**
     * <p>Release a save point.
     * <p>If session exists pseudo transaction ,then this method don't access database server.
     *
     * @param optionFunc option function,see {@link Option#EMPTY_FUNC}
     * @throws IllegalArgumentException throw when savepoint is unknown
     * @throws SessionException         throw when
     *                                  <ul>
     *                                      <li>session have closed</li>
     *                                      <li>session not in transaction(real/pseudo)</li>
     *                                      <li>release point failure</li>
     *                                  </ul>
     */
    void releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncSession}
     *             session.rollbackToSavePoint(savepoint,Option.EMPTY_FUNC) ;
     *         </code>
     * </pre>
     *
     * @see #rollbackToSavePoint(Object, Function)
     */
    void rollbackToSavePoint(Object savepoint);

    /**
     * <p>Rollback a save point.
     * <p>If session exists pseudo transaction ,then this method don't access database server.
     *
     * @param optionFunc option function,see {@link Option#EMPTY_FUNC}
     * @throws IllegalArgumentException throw when savepoint is unknown
     * @throws SessionException         throw when
     *                                  <ul>
     *                                      <li>session have closed</li>
     *                                      <li>session not in transaction(real/pseudo)</li>
     *                                      <li>rollback point failure</li>
     *                                  </ul>
     */
    void rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

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
     * @see SyncLocalSession#startTransaction(TransactionOption, HandleMode)
     * @see SyncRmSession#start(Xid, int, TransactionOption)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/set-transaction.html">MySQL SET TRANSACTION Statement</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-set-transaction.html">PostgreSQL SET TRANSACTION Statement</a>
     */
    void setTransactionCharacteristics(TransactionOption option);


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link SyncSession}
     *             session.queryOne(statement,resultClass,defaultOption()) ; // defaultOption() is private method of the implementation of {@link SyncSession}.
     *         </code>
     * </pre>
     *
     * @throws NoSuchElementException throw when no row
     * @throws NonMonoException       throw when more than one row.
     * @see #queryOne(SimpleDqlStatement, Class, SyncStmtOption)
     */
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass);

    /**
     * <p>Execute a simple(non-batch) statement to query one row.
     * <p>This method don't support {@link java.util.Map},but you can use {@link #queryOneObject(SimpleDqlStatement, Supplier, SyncStmtOption)} instead of this method.
     * <p>statement will be parsed as {@link io.army.stmt.Stmt} by {@link io.army.dialect.DialectParser} and {@link io.army.stmt.Stmt} will be executed by {@link io.army.sync.executor.SyncExecutor}.
     *
     * <pre>
     *     This method is equivalent to following :
     *     <code><br/>
     *          final List&lt;R> resultList;
     *          resultList = this.queryList(statement,resultClass,option);
     *          final R result;
     *          switch (resultList.size()) {
     *              case 1:
     *                  result = resultList.get(0);
     *                  break;
     *              case 0:
     *                  result = null;
     *                  break;
     *              default:
     *                  throw _Exceptions.nonUnique(resultList);
     *          }
     *          return result;
     *     </code>
     * </pre>
     *
     * @param statement   simple(non-batch) query statement
     * @param resultClass result class is one of
     *                    <ul>
     *                        <li>simple value class ,for example : {@link Integer},{@link String},{@link java.util.BitSet},byte[],{@link java.time.LocalDateTime}</li>
     *                        <li>POJO class that have public default constructor</li>
     *                    </ul>
     *                    ,couldn't be {@link java.util.Map} or it's sub class/interface
     * @param option      statement option for more control,see {@link SyncStmtOption#timeoutMillis(int)} ,{@link SyncStmtOption#builder()} etc.
     * @param <R>         representing row Java Type.
     * @return <ul>
     *     <li>nullable : resultClass is simple value class</li>
     *     <li>non-null : resultClass is pojo class</li>
     * </ul>
     * @throws NoSuchElementException throw when no row
     * @throws NonMonoException       throw when more than one row.
     * @throws CriteriaException      throw when
     * @throws SessionException       throw when
     *                                <ul>
     *                                    <li>session have closed</li>
     *                                    <li>statement is dml statement,but {@link #isReadonlySession()} is true,see {@link ReadOnlySessionException}</li>
     *                                    <li>statement is dml statement,but {@link #isReadOnlyStatus()} is true,see {@link ReadOnlyTransactionException}</li>
     *                                    <li>update/delete child table (eg : firebird update statement),but {@link #inTransaction()} is false,see {@link ChildDmlNoTractionException}</li>
     *                                    <li>statement is query insert statement,but {@link #isQueryInsertAllowed()} is false , see {@link QueryInsertException}</li>
     *                                    <li>result row count more than one,see {@link NonMonoException}</li>
     *                                    <li>server response error message</li>
     *                                </ul>
     * @see #queryList(DqlStatement, Class, SyncStmtOption)
     */
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    <R> Optional<R> queryOneOptional(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Optional<R> queryOneOptional(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option);


    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);

    <R> Optional<R> queryOneOptionalObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Optional<R> queryOneOptionalObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);


    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);

    <R> Optional<R> queryOneOptionalRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Optional<R> queryOneOptionalRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> queryList(DqlStatement statement, Class<R> resultClass);

    <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);

    <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     */
    <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor);

    <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     */
    <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor);

    <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option);

    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     */
    <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function);

    <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function,
                                Supplier<List<R>> listConstructor);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function,
                                Supplier<List<R>> listConstructor, SyncStmtOption option);


    /*-------------------below queryStream -------------------*/

    <R> Stream<R> query(DqlStatement statement, Class<R> resultClass);

    /**
     * <p>Execute a simple/batch statement to query row stream.
     * <p>This method don't support {@link java.util.Map} and the pojo that have generic,but you can use {@link #queryObject(DqlStatement, Supplier, SyncStmtOption)} instead of this method.
     * <p>statement will be parsed as {@link io.army.stmt.Stmt} by {@link io.army.dialect.DialectParser} and {@link io.army.stmt.Stmt} will be executed by {@link io.army.sync.executor.SyncExecutor}.
     *
     * @param statement   simple/batch query statement
     * @param resultClass result class is one of
     *                    <ul>
     *                        <li>simple value class ony when single {@link Selection},for example : {@link Integer},{@link String},{@link java.util.BitSet},byte[],{@link java.time.LocalDateTime}</li>
     *                        <li>POJO class that have public default constructor and have no generic</li>
     *                    </ul>
     *                    ,couldn't be {@link java.util.Map} or it's sub class/interface
     * @param option      statement option for more control,see {@link SyncStmtOption#timeoutMillis(int)} ,{@link SyncStmtOption#commanderConsumer(Consumer)} ,{@link SyncStmtOption#builder()} etc.
     * @param <R>         representing row Java Type.
     * @return non-null row stream. The underlying resource (eg : {@code java.sql.ResultSet}) of the stream will be close in following situation :
     * <ul>
     *     <li>stream normally end</li>
     *     <li>the downstream of stream throw {@link Throwable}</li>
     *     <li>{@link io.army.sync.executor.SyncExecutor} invoke consumer of {@link ResultStates} occur error, see {@link SyncStmtOption#stateConsumer()}</li>
     *     <li>you invoke {@link StreamCommander#cancel()} , see {@link SyncStmtOption#commanderConsumer()}</li>
     *     <li>You invoke {@link Stream#close()}</li>
     * </ul>
     * @throws CriteriaException throw when
     * @throws SessionException  throw when
     *                           <ul>
     *                               <li>session have closed</li>
     *                               <li>statement is dml statement,but {@link #isReadonlySession()} is true,see {@link ReadOnlySessionException}</li>
     *                               <li>statement is dml statement,but {@link #isReadOnlyStatus()} is true,see {@link ReadOnlyTransactionException}</li>
     *                               <li>update/delete child table (eg : firebird update statement),but {@link #inTransaction()} is false,see {@link ChildDmlNoTractionException}</li>
     *                               <li>statement is query-insert statement,but {@link #isQueryInsertAllowed()} is false , see {@link QueryInsertException}</li>
     *                               <li>server response error message</li>
     *                           </ul>
     */
    <R> Stream<R> query(DqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     */
    <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     */
    <R> Stream<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     *
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    long update(SimpleDmlStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    long update(SimpleDmlStatement statement, SyncStmtOption option);

    ResultStates updateAsStates(SimpleDmlStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    ResultStates updateAsStates(SimpleDmlStatement statement, SyncStmtOption option);

    <T> int save(T domain);

    <T> int save(T domain, SyncStmtOption option);

    <T> int batchSave(List<T> domainList);

    <T> int batchSave(List<T> domainList, LiteralMode literalMode);

    <T> int batchSave(List<T> domainList, SyncStmtOption option);

    <T> int batchSave(List<T> domainList, LiteralMode literalMode, SyncStmtOption option);

    /**
     * @return a unmodified list
     */
    List<Long> batchUpdate(BatchDmlStatement statement);

    /**
     * @return a unmodified list
     */
    List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option);

    /**
     * @return a unmodified list
     */
    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor);

    /**
     * @return a unmodified list
     */
    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option);

    Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement);

    Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement, SyncStmtOption option);


    @Override
    void close() throws SessionException;


}
