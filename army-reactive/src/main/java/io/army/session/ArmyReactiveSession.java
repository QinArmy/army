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

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.env.SqlLogMode;
import io.army.executor.DataAccessException;
import io.army.executor.DriverSpiHolder;
import io.army.executor.ReactiveExecutor;
import io.army.meta.ChildTableMeta;
import io.army.option.Option;
import io.army.result.ChildUpdateException;
import io.army.result.CurrentRecord;
import io.army.result.ResultStates;
import io.army.stmt.*;
import io.army.transaction.TransactionInfo;
import io.army.util.SQLStmts;
import io.army.util.StreamFunctions;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmyReactiveLocalSession}</li>
 *     <li>{@link ArmyReactiveRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implements of {@link ReactiveSession}.
 *
 * @since 0.6.0
 */
non-sealed abstract class ArmyReactiveSession extends _ArmySession<ArmyReactiveSessionFactory> implements ReactiveSession {

    private static final AtomicIntegerFieldUpdater<ArmyReactiveSession> SESSION_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveSession.class, "sessionClosed");

    @SuppressWarnings("unchecked")
    private static final AtomicReferenceFieldUpdater<ArmyReactiveSession, ConcurrentMap<Object, Object>> ATTRIBUTE_MAP =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveSession.class, (Class<ConcurrentMap<Object, Object>>) ((Class<?>) ConcurrentMap.class), "attributeMap");


    final ReactiveExecutor executor;
    private volatile int sessionClosed;

    private volatile ConcurrentMap<Object, Object> attributeMap;

    ArmyReactiveSession(ArmyReactiveSessionFactory.ReactiveSessionBuilder<?, ?> builder) {
        super(builder);
        this.executor = builder.stmtExecutor;
        assert this.executor != null;
    }

    @Override
    public final ReactiveSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public final boolean isReactive() {
        return true;
    }

    @Override
    public final boolean isSync() {
        return false;
    }

    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        boolean in;
        try {
            in = this.executor.inTransaction();
        } catch (DataAccessException e) {
            final TransactionInfo info;
            info = obtainTransactionInfo();
            in = info != null && info.inTransaction();
        } catch (Exception e) {
            throw (RuntimeException) handleExecutionError(e);
        }
        return in;

    }

    @Override
    public final <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return query(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne);
    }


    @Override
    public final <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return query(statement, resultClass, option)
                .reduce(StreamFunctions::atMostOne);
    }

    @Override
    public final <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return queryObject(statement, constructor, ArmyReactiveStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne);
    }

    @Override
    public final <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return queryObject(statement, constructor, option)
                .reduce(StreamFunctions::atMostOne);
    }

    @Override
    public final <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return queryRecord(statement, function, ArmyReactiveStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne);
    }

    @Override
    public final <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        return queryRecord(statement, function, option)
                .reduce(StreamFunctions::atMostOne);
    }

    @Override
    public final <R> Flux<R> query(DqlStatement statement, Class<R> resultClass) {
        return query(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> query(DqlStatement statement, final Class<R> resultClass, final ReactiveStmtOption option) {
        return executeQuery(statement, option, classReaderFunc(resultClass));
    }

    @Override
    public final <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return executeQuery(statement, option, constructorReaderFunc(constructor));
    }

    @Override
    public final <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        if (statement instanceof _Statement._ChildStatement) {
            throw new SessionException("queryRecord api don't support two statement mode");
        }
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);
            if (!(stmt instanceof SingleSqlStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            }

            final ReactiveStmtOption newOption;
            if (this.factory.sqlExecutionCostTime) {
                newOption = replaceForQueryExecutionLogger(option, stmt);  // for transaction timeout and optimistic lock and execution log
            } else {
                newOption = replaceForQueryIfNeed(stmt.hasOptimistic(), option, null); // for transaction timeout and optimistic lock
            }
            return this.executor.queryRecord((SingleSqlStmt) stmt, function, newOption, Option.EMPTY_FUNC);
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }


    @Override
    public final <T> Mono<ResultStates> save(T domain) {
        return save(domain, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <T> Mono<ResultStates> save(T domain, ReactiveStmtOption option) {
        return update(SQLStmts.insertStmt(this, LiteralMode.DEFAULT, domain), option);
    }

    @Override
    public final Mono<ResultStates> update(SimpleDmlStatement statement) {
        return update(statement, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final Mono<ResultStates> update(SimpleDmlStatement statement, final ReactiveStmtOption unsafeOption) {
        final Mono<ResultStates> mono;
        if (statement instanceof _BatchStatement) {
            mono = Mono.error(_Exceptions.unexpectedStatement(statement));
        } else if (statement instanceof InsertStatement) {
            mono = executeInsert((InsertStatement) statement, replaceIfNeed(unsafeOption));
        } else {
            mono = executeUpdate(statement, replaceIfNeed(unsafeOption));
        }
        return mono;
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList) {
        return batchSave(domainList, LiteralMode.DEFAULT, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList, LiteralMode literalMode) {
        return batchSave(domainList, literalMode, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList, ReactiveStmtOption option) {
        return batchSave(domainList, LiteralMode.DEFAULT, option);
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList, LiteralMode literalMode, ReactiveStmtOption option) {
        if (domainList.size() == 0) {
            throw new IllegalArgumentException("domainList must non-empty.");
        }
        return update(SQLStmts.batchInsertStmt(this, literalMode, domainList), option);
    }

    @Override
    public final Flux<ResultStates> batchUpdate(BatchDmlStatement statement) {
        return batchUpdate(statement, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final Flux<ResultStates> batchUpdate(BatchDmlStatement statement, final ReactiveStmtOption option) {
        Flux<ResultStates> flux;
        try {
            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);
            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final Consumer<ResultStates> optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = OPTIMISTIC_LOCK_VALIDATOR;
            } else {
                optimisticLockValidator = null;
            }
            if (stmt instanceof BatchStmt) {
                flux = this.executor.batchUpdate((BatchStmt) stmt, option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    flux = flux.doOnNext(optimisticLockValidator);
                }
                flux = flux.onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (inTransaction()) {
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;
                final ChildTableMeta<?> domainTable;
                domainTable = (ChildTableMeta<?>) getBatchUpdateDomainTable(statement); // fail, bug.

                assert domainTable != null; // fail, bug.

                flux = this.executor.batchUpdate(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    flux = flux.doOnNext(optimisticLockValidator);
                }
                flux = flux.collectMap(ResultStates::resultNo, states -> states, _Collections::hashMap)
                        .flatMapMany(statesMap -> validateBatchStates(this.executor.batchUpdate(pairStmt.secondStmt(), option, Option.EMPTY_FUNC), statesMap, domainTable))
                        .onErrorMap(this::handlePairStmtError);
            } else {
                throw updateChildNoTransaction();
            }
        } catch (Throwable e) {
            flux = Flux.error(_ArmySession.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }

        return flux;
    }

    @Override
    public final long sessionIdentifier() throws SessionException {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        try {
            return this.executor.sessionIdentifier();
        } catch (Exception e) {
            throw (RuntimeException) handleExecutionError(e);
        }
    }


    @Nullable
    @Override
    public final TransactionInfo currentTransactionInfo() {
        return obtainTransactionInfo();
    }

    @Override
    public final Mono<TransactionInfo> transactionInfo() {
        final Mono<TransactionInfo> mono;
        final TransactionInfo info;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if ((info = obtainTransactionInfo()) == null) {
            mono = this.executor.transactionInfo()
                    .onErrorMap(this::handleExecutionError);
        } else {
            mono = Mono.just(info);
        }
        return mono;
    }

    @Override
    public final Mono<TransactionInfo> sessionTransactionCharacteristics() {
        final Mono<TransactionInfo> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else {
            mono = this.executor.sessionTransactionCharacteristics(Option.EMPTY_FUNC)
                    .onErrorMap(this::handleExecutionError);
        }
        return mono;
    }

    @Override
    public final Mono<?> setSavePoint() {
        return this.setSavePoint(Option.EMPTY_FUNC);
    }


    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return this.executor.setSavePoint(optionFunc)
                .onErrorMap(this::handleExecutionError);
    }

    @Override
    public final <T> T valueOf(final Option<T> option) {
        try {
            return this.executor.valueOf(option);
        } catch (Exception e) {
            throw (RuntimeException) handleExecutionError(e);
        }
    }


    @Override
    public final boolean isClosed() {
        final boolean closed;
        if (this instanceof DriverSpiHolder) {
            closed = this.sessionClosed != 0 || this.executor.isClosed();
        } else {
            closed = this.sessionClosed != 0;
        }
        return closed;
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.defer(this::closeSession);
    }


    /*-------------------below protected template methods -------------------*/

    @Nullable
    @Override
    protected final Map<Object, Object> obtainAttributeMap() {
        return this.attributeMap;
    }

    @Override
    protected final Map<Object, Object> obtainOrCreateAttributeMap() {
        ConcurrentMap<Object, Object> map = this.attributeMap;
        if (map != null) {
            return map;
        }
        map = _Collections.concurrentHashMap();
        if (!ATTRIBUTE_MAP.compareAndSet(this, null, map)) {
            map = ATTRIBUTE_MAP.get(this);
            assert map != null;
        }
        return map;
    }

    /*-------------------below package methods -------------------*/




    /*-------------------below private methods -------------------*/


    /**
     * @see #queryRecord(DqlStatement, Function, ReactiveStmtOption)
     */
    private ReactiveStmtOption replaceForQueryExecutionLogger(final ReactiveStmtOption optionOfUser, final Stmt stmt) {
        final SqlLogMode sqlLogMode;
        if ((sqlLogMode = obtainSqlLogMode()) == SqlLogMode.OFF) {
            return replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser, null);
        }

        final long executionStartNanoSecond;
        executionStartNanoSecond = System.nanoTime();

        final Consumer<ResultStates> logConsumer;
        logConsumer = states -> {
            if (!states.hasMoreResult() && !states.hasMoreFetch()) {
                printExecutionCostTimeLog(getLogger(), stmt, sqlLogMode, executionStartNanoSecond);
            }
        };
        return replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser, logConsumer);
    }

    /**
     * @see #queryRecord(DqlStatement, Function, ReactiveStmtOption)
     */
    private ReactiveStmtOption replaceForQueryIfNeed(final boolean hasOptimistic, final ReactiveStmtOption option,
                                                     @Nullable Consumer<ResultStates> consumer) {
        final TransactionInfo info;
        final ReactiveStmtOption newOption;
        if (hasOptimistic || consumer != null) {
            if (hasOptimistic) {
                if (consumer == null) {
                    consumer = OPTIMISTIC_LOCK_VALIDATOR;
                } else {
                    consumer = OPTIMISTIC_LOCK_VALIDATOR.andThen(consumer);
                }
            }
            newOption = ArmyReactiveStmtOptions.overrideOptionIfNeed(option, obtainTransactionInfo(), consumer);
        } else if (option instanceof ArmyReactiveStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmyReactiveStmtOptions.overrideOptionIfNeed(option, info, null);
        }
        return newOption;
    }


    private ReactiveStmtOption replaceIfNeed(final ReactiveStmtOption option) {
        final TransactionInfo info;

        final ReactiveStmtOption newOption;
        if (option instanceof ArmyReactiveStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmyReactiveStmtOptions.overrideTimeoutIfNeed(option, info);
        }
        return newOption;
    }


    /**
     * @see #query(DqlStatement, Class, ReactiveStmtOption)
     * @see #queryObject(DqlStatement, Supplier, ReactiveStmtOption)
     * @see #queryRecord(DqlStatement, Function, ReactiveStmtOption)
     */
    private <R> Flux<R> executeQuery(final DqlStatement statement, final ReactiveStmtOption optionOfUser,
                                     final ReaderFunction<R> readerFunc) {
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, optionOfUser);

            final ReactiveStmtOption option;
            if (this.factory.sqlExecutionCostTime) {
                option = replaceForQueryExecutionLogger(optionOfUser, stmt);  // for transaction timeout and optimistic lock and execution log
            } else {
                option = replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser, null); // for transaction timeout and optimistic lock
            }


            final Flux<R> flux;
            if (stmt instanceof SingleSqlStmt) {
                final Function<? super CurrentRecord, R> rowFunc;
                rowFunc = readerFunc.apply((SingleSqlStmt) stmt, true);
                flux = this.executor.queryRecord((SingleSqlStmt) stmt, rowFunc, option, Option.EMPTY_FUNC);
            } else if (!(stmt instanceof PairStmt)) {
                // no bug,never here
                flux = Flux.error(_Exceptions.unexpectedStmt(stmt));
            } else if (!inTransaction()) {
                flux = Flux.error(updateChildNoTransaction());
            } else if (statement instanceof InsertStatement) {
                flux = executePairInsertQuery((InsertStatement) statement, (PairStmt) stmt, option, readerFunc);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                flux = Flux.error(_Exceptions.unexpectedStatement(statement));
            }
            return flux;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }

    }



    /**
     * @param option the instance is returned by {@link #replaceIfNeed(ReactiveStmtOption)}.
     * @see #executeQuery(DqlStatement, ReactiveStmtOption, ReaderFunction)
     */
    private <R> Flux<R> executePairInsertQuery(final InsertStatement statement, final PairStmt stmt,
                                               final ReactiveStmtOption option,
                                               final ReaderFunction<R> readerFunc) {

        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;
        final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) childInsert.table();

        final Function<Throwable, Throwable> errorFunc;
        errorFunc = error -> _Exceptions.childInsertError(this, domainTable, error);

        final SimpleStmt firstStmt = stmt.firstStmt(), secondStmt = stmt.secondStmt();
        final Function<? super CurrentRecord, R> rowFunc;

        final Flux<R> flux;
        if (firstStmtIsQuery) {
            rowFunc = readerFunc.apply(firstStmt, false);
            flux = this.executor.queryRecord(firstStmt, rowFunc, option, Option.EMPTY_FUNC)
                    .collect(Collectors.toCollection(_Collections::arrayList))
                    .flatMapMany(resultList -> {
                        final int rowCount = resultList.size();
                        if (rowCount == 0) {
                            // exists conflict clause
                            return Flux.empty();
                        }

                        final ReactiveSecondRecordReader<R> recordReader;
                        recordReader = new ReactiveSecondRecordReader<>(this, domainTable, (TwoStmtQueryStmt) secondStmt, resultList);

                        return this.executor.queryRecord(secondStmt, recordReader::readRecord, option, Option.EMPTY_FUNC)
                                .doOnComplete(recordReader::validateRowCount)
                                .onErrorMap(errorFunc);
                    });
        } else {
            rowFunc = readerFunc.apply(secondStmt, true); // here use secondStmt not firstStmt

            flux = this.executor.insert(firstStmt, option, Option.EMPTY_FUNC)
                    .flatMapMany(parentStates -> {
                        final long rowCount = parentStates.affectedRows();
                        if (rowCount == 0L) {
                            // exists conflict clause
                            return Flux.empty();
                        }
                        // TODO 验证 行数,fetch size
                        final Consumer<ResultStates> statesConsumer;
                        statesConsumer = childStates -> {
                            if (childStates.rowCount() != parentStates.affectedRows()) {
                                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.rowCount());
                            }
                        };
                        final ReactiveStmtOption newOption;
                        newOption = ArmyReactiveStmtOptions.replaceStateConsumer(option, statesConsumer);

                        return this.executor.queryRecord(secondStmt, rowFunc, newOption, Option.singleFunc(Option.FIRST_DML_STATES, parentStates))
                                .onErrorMap(errorFunc);
                    });
        }
        return flux;
    }

    /**
     * @param option the instance is returned by {@link #replaceIfNeed(ReactiveStmtOption)}.
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeInsert(final InsertStatement statement, final ReactiveStmtOption option) {
        Mono<ResultStates> mono;
        try {
            assertSession(statement);
            final Stmt stmt;
            stmt = parseInsertStatement(statement);
            if (stmt instanceof SimpleStmt) {
                mono = this.executor.insert((SimpleStmt) stmt, option, Option.EMPTY_FUNC)
                        .onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairStmt)) {
                mono = Mono.error(_Exceptions.unexpectedStmt(stmt));
            } else if (inTransaction()) {
                final PairStmt pairStmt = (PairStmt) stmt;
                final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();

                mono = this.executor.insert(pairStmt.firstStmt(), option, Option.EMPTY_FUNC)
                        .flatMap(parentStates -> this.executor.insert(pairStmt.secondStmt(), option, Option.singleFunc(Option.FIRST_DML_STATES, parentStates))
                                .doOnSuccess(childStates -> {
                                    if (childStates.affectedRows() != parentStates.affectedRows()) {
                                        throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.affectedRows());
                                    }
                                })
                        ).onErrorMap(this::handlePairStmtError);
            } else {
                mono = Mono.error(updateChildNoTransaction());
            }
        } catch (Throwable e) {
            mono = Mono.error(_ArmySession.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
        return mono;
    }


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(ReactiveStmtOption)}.
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeUpdate(final SimpleDmlStatement statement, final ReactiveStmtOption option) {
        Mono<ResultStates> mono;
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDmlStatement(statement, option);

            final Consumer<ResultStates> optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = OPTIMISTIC_LOCK_VALIDATOR;
            } else {
                optimisticLockValidator = null;
            }
            if (stmt instanceof SimpleStmt) {
                mono = this.executor.update((SimpleStmt) stmt, option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    mono = mono.doOnNext(optimisticLockValidator);
                }
                mono = mono.onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairStmt)) {
                mono = Mono.error(_Exceptions.unexpectedStmt(stmt));
            } else if (!inTransaction()) {
                mono = Mono.error(updateChildNoTransaction());
            } else if (statement instanceof NarrowDmlStatement) {
                final PairStmt pairStmt = (PairStmt) stmt;
                final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();

                mono = this.executor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    mono = mono.doOnNext(optimisticLockValidator);
                }
                mono = mono.flatMap(childStates -> this.executor.update(pairStmt.secondStmt(), option, Option.singleFunc(Option.FIRST_DML_STATES, childStates))
                        .doOnSuccess(parentStates -> {
                            if (parentStates.affectedRows() != childStates.affectedRows()) {
                                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.affectedRows());
                            }
                        })
                ).onErrorMap(this::handlePairStmtError);
            } else {
                final PairStmt pairStmt = (PairStmt) stmt;
                mono = this.executor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC)
                        .then(this.executor.update(pairStmt.secondStmt(), option, Option.EMPTY_FUNC));
            }
        } catch (Throwable e) {
            mono = Mono.error(_ArmySession.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
        return mono;
    }


    private Flux<ResultStates> validateBatchStates(final Flux<ResultStates> source, Map<Integer, ResultStates> statesMap,
                                                   final ChildTableMeta<?> domainTable) {
        return Flux.create(sink -> source.subscribe(new ValidateBatchStatesSubscriber(sink, statesMap, domainTable)));
    }

    private <T> Mono<T> closeSession() {
        if (!SESSION_CLOSED.compareAndSet(this, 0, 1)) {
            return Mono.empty();
        }
        return this.executor.close();
    }


    private Throwable handleExecutionError(final Throwable cause) {
        if (cause instanceof SessionClosedException) {
            SESSION_CLOSED.compareAndSet(this, 0, 1);
        }
        return _ArmySession.wrapIfNeed(cause);
    }


    private Throwable handlePairStmtError(final Throwable cause) {
        if (cause instanceof ChildUpdateException) {
            final ChildUpdateException error = (ChildUpdateException) cause;
            rollbackOnlyOnError(error);
            return error;
        }
        return handleExecutionError(cause);
    }



    /*-------------------below static methods  -------------------*/


    @SuppressWarnings("all")
    private static abstract class ValidateSubscriber<T> implements Subscriber<T> {

        static final AtomicIntegerFieldUpdater<ValidateSubscriber> DONE = AtomicIntegerFieldUpdater.newUpdater(ValidateSubscriber.class, "done");

        private volatile int done;

        private Subscription s;

        @Override
        public final void onSubscribe(Subscription s) {
            this.s = s;
        }

        final void onRequest(long n) {
            final Subscription s = this.s;
            if (s != null) {
                s.request(n);
            }
        }

    }


    private static final class ValidateItemCountSubscriber<T> extends ValidateSubscriber<T> {

        private final FluxSink<T> sink;

        private final long expectedCount;

        private final AtomicLong count = new AtomicLong(0);


        private ValidateItemCountSubscriber(FluxSink<T> sink, long expectedCount) {
            this.sink = sink.onRequest(this::onRequest);
            this.expectedCount = expectedCount;
        }


        @Override
        public void onNext(T t) {
            this.count.addAndGet(1);
            this.sink.next(t);
        }

        @Override
        public void onError(Throwable t) {
            if (!DONE.compareAndSet(this, 0, 1)) {
                return;
            }
            this.sink.error(t);
        }

        @Override
        public void onComplete() {
            if (!DONE.compareAndSet(this, 0, 1)) {
                return;
            }
            if (this.count.get() == this.expectedCount) {
                this.sink.complete();
            } else {
                String m = String.format("parent insert row[%s] and child insert row[%s] not match.",
                        this.expectedCount, this.count.get());
                this.sink.error(new ChildUpdateException(m));
            }
        }


    } // ValidateItemCountSubscriber


    private static final class ValidateBatchStatesSubscriber extends ValidateSubscriber<ResultStates> {


        private static final AtomicReferenceFieldUpdater<ValidateBatchStatesSubscriber, Throwable> ERROR =
                AtomicReferenceFieldUpdater.newUpdater(ValidateBatchStatesSubscriber.class, Throwable.class, "error");

        private final FluxSink<ResultStates> sink;

        private final Map<Integer, ResultStates> statesMap;

        private final ChildTableMeta<?> domainTable;

        private volatile Throwable error;


        private ValidateBatchStatesSubscriber(FluxSink<ResultStates> sink, Map<Integer, ResultStates> statesMap,
                                              ChildTableMeta<?> domainTable) {
            this.sink = sink.onRequest(this::onRequest);
            this.statesMap = _Collections.unmodifiableMap(statesMap);
            this.domainTable = domainTable;
        }


        @Override
        public void onNext(final ResultStates states) {
            final ResultStates childStates;
            childStates = this.statesMap.get(states.resultNo());
            if (childStates == null) {
                String m = String.format("Not found %s for batch item[%s] , %s", ResultStates.class.getName(),
                        states.resultNo(), this.domainTable);
                ERROR.compareAndSet(this, null, new ChildUpdateException(m));
            } else if (childStates.affectedRows() == states.affectedRows()) {
                this.sink.next(states);
            } else {
                final ChildUpdateException e;
                e = _Exceptions.batchChildUpdateRowsError(this.domainTable, states.resultNo(), childStates.affectedRows(),
                        states.affectedRows());
                ERROR.compareAndSet(this, null, e);
            }
        }

        @Override
        public void onError(final Throwable t) {
            if (!DONE.compareAndSet(this, 0, 1)) {
                return;
            }
            final Throwable error;
            error = ERROR.get(this);
            if (error == null) {
                this.sink.error(t);
            } else {
                final String m = String.format("occur two error :\n1- %s\n\n2- %s ", error.getMessage(), t.getMessage());
                this.sink.error(new ChildUpdateException(m, t));
            }
        }

        @Override
        public void onComplete() {
            if (!DONE.compareAndSet(this, 0, 1)) {
                return;
            }

            final Throwable error;
            error = ERROR.get(this);
            if (error == null) {
                this.sink.complete();
            } else {
                this.sink.error(error);
            }
        }


    } // ValidateBatchStatesSubscriber




}
