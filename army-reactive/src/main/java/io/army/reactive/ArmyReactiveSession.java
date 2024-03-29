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
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.reactive.executor.ReactiveExecutor;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
import io.army.stmt.*;
import io.army.util.ArmyCriteria;
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
import java.util.Optional;
import java.util.Set;
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
abstract class ArmyReactiveSession extends _ArmySession<ArmyReactiveSessionFactory> implements ReactiveSession {

    private static final AtomicIntegerFieldUpdater<ArmyReactiveSession> SESSION_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveSession.class, "sessionClosed");

    @SuppressWarnings("unchecked")
    private static final AtomicReferenceFieldUpdater<ArmyReactiveSession, ConcurrentMap<Object, Object>> ATTRIBUTE_MAP =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveSession.class, (Class<ConcurrentMap<Object, Object>>) ((Class<?>) ConcurrentMap.class), "attributeMap");


    final ReactiveExecutor stmtExecutor;
    private volatile int sessionClosed;

    private volatile ConcurrentMap<Object, Object> attributeMap;

    ArmyReactiveSession(ArmyReactiveSessionFactory.ReactiveSessionBuilder<?, ?> builder) {
        super(builder);
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
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
            in = this.stmtExecutor.inTransaction();
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
        return queryOne(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT);
    }


    @Override
    public final <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return query(statement, resultClass, option)
                .take(2)
                .collect(Collectors.toCollection(_Collections::arrayList))
                .flatMap(ArmyReactiveSession::onlyRow);
    }

    @Override
    public final <R> Mono<Optional<R>> queryOneNullable(SimpleDqlStatement statement, Class<R> resultClass) {
        return queryOneNullable(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Mono<Optional<R>> queryOneNullable(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return queryNullable(statement, resultClass, option)
                .take(2)
                .collect(Collectors.toCollection(_Collections::arrayList))
                .flatMap(ArmyReactiveSession::onlyRow);
    }

    @Override
    public final <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return queryOneObject(statement, constructor, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return queryObject(statement, constructor, option)
                .take(2)
                .collect(Collectors.toCollection(_Collections::arrayList))
                .flatMap(ArmyReactiveSession::onlyRow);
    }

    @Override
    public final <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return queryOneRecord(statement, function, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        return queryRecord(statement, function, option)
                .take(2)
                .collect(Collectors.toCollection(_Collections::arrayList))
                .flatMap(ArmyReactiveSession::onlyRow);
    }

    @Override
    public final <R> Flux<R> query(DqlStatement statement, Class<R> resultClass) {
        return query(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> query(DqlStatement statement, final Class<R> resultClass, final ReactiveStmtOption option) {
        return this.executeQuery(statement, option, (s, o, func) -> this.stmtExecutor.query(s, resultClass, o, func)); // here ,use o not option
    }


    @Override
    public final <R> Flux<Optional<R>> queryNullable(DqlStatement statement, Class<R> resultClass) {
        return this.queryNullable(statement, resultClass, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<Optional<R>> queryNullable(DqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, (s, o, func) -> this.stmtExecutor.queryOptional(s, resultClass, o, func)); // here ,use o not option
    }

    @Override
    public final <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> queryObject(DqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, (s, o, func) -> this.stmtExecutor.queryObject(s, constructor, o, func)); // here ,use o not option
    }

    @Override
    public final <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Flux<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, (s, o, func) -> this.stmtExecutor.queryRecord(s, function, o, func)); // here ,use o not option
    }

    @Override
    public final <T> Mono<ResultStates> save(T domain) {
        return save(domain, ArmyReactiveStmtOptions.DEFAULT);
    }

    @Override
    public final <T> Mono<ResultStates> save(T domain, ReactiveStmtOption option) {
        return update(ArmyCriteria.insertStmt(this, domain), option);
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
        return update(ArmyCriteria.batchInsertStmt(this, literalMode, domainList), option);
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
                flux = this.stmtExecutor.batchUpdate((BatchStmt) stmt, option, Option.EMPTY_FUNC);
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

                flux = this.stmtExecutor.batchUpdate(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    flux = flux.doOnNext(optimisticLockValidator);
                }
                flux = flux.collectMap(ResultStates::resultNo, states -> states, _Collections::hashMap)
                        .flatMapMany(statesMap -> validateBatchStates(this.stmtExecutor.batchUpdate(pairStmt.secondStmt(), option, Option.EMPTY_FUNC), statesMap, domainTable))
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
            return this.stmtExecutor.sessionIdentifier();
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
            mono = this.stmtExecutor.transactionInfo()
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
            mono = this.stmtExecutor.sessionTransactionCharacteristics(Option.EMPTY_FUNC)
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
        return this.stmtExecutor.setSavePoint(optionFunc)
                .onErrorMap(this::handleExecutionError);
    }

    @Override
    public final <T> T valueOf(final Option<T> option) {
        try {
            return this.stmtExecutor.valueOf(option);
        } catch (Exception e) {
            throw (RuntimeException) handleExecutionError(e);
        }
    }

    @Override
    public final Set<Option<?>> optionSet() {
        try {
            return this.stmtExecutor.optionSet();
        } catch (Exception e) {
            throw (RuntimeException) handleExecutionError(e);
        }
    }

    @Override
    public final boolean isClosed() {
        final boolean closed;
        if (this instanceof DriverSpiHolder) {
            closed = this.sessionClosed != 0 || this.stmtExecutor.isClosed();
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
    private <R> Flux<R> executeQuery(final DqlStatement statement, final ReactiveStmtOption unsafeOption,
                                     final ExecutorFunction<R> exeFunc) {

        Flux<R> flux;
        try {
            assertSession(statement);

            final ReactiveStmtOption option;
            option = replaceIfNeed(unsafeOption);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);
            if (stmt instanceof SingleSqlStmt) {
                flux = exeFunc.execute((SingleSqlStmt) stmt, option, Option.EMPTY_FUNC)
                        .onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairStmt)) {
                // no bug,never here
                flux = Flux.error(_Exceptions.unexpectedStmt(stmt));
            } else if (!inTransaction()) {
                flux = Flux.error(updateChildNoTransaction());
            } else if (statement instanceof InsertStatement) {
                flux = executePairInsertQuery((InsertStatement) statement, (PairStmt) stmt, option, exeFunc)
                        .onErrorMap(this::handlePairStmtError);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                flux = Flux.error(_Exceptions.unexpectedStatement(statement));
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


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(ReactiveStmtOption)}.
     * @see #executeQuery(DqlStatement, ReactiveStmtOption, ExecutorFunction)
     */
    private <R> Flux<R> executePairInsertQuery(final InsertStatement statement, final PairStmt stmt,
                                               final ReactiveStmtOption option,
                                               final ExecutorFunction<R> exeFunc) {

        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;
        final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) childInsert.table();

        final Function<Throwable, Throwable> errorFunc;
        errorFunc = error -> _Exceptions.childInsertError(this, domainTable, error);


        final Flux<R> flux;
        if (firstStmtIsQuery) {
            flux = exeFunc.execute(stmt.firstStmt(), option, Option.EMPTY_FUNC)
                    .collect(Collectors.toCollection(_Collections::arrayList))
                    .flatMapMany(resultList -> {
                        final int rowCount = resultList.size();
                        if (rowCount == 0) {
                            // exists conflict clause
                            return Flux.empty();
                        }
                        return Flux.create(sink -> this.stmtExecutor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), option, resultList, Option.EMPTY_FUNC)
                                .onErrorMap(errorFunc)
                                .subscribe(new ValidateItemCountSubscriber<>(sink, rowCount))
                        );
                    });
        } else {
            flux = this.stmtExecutor.insert(stmt.firstStmt(), option, Option.EMPTY_FUNC)
                    .flatMapMany(states -> {
                        final long rowCount = states.affectedRows();
                        if (rowCount == 0L) {
                            // exists conflict clause
                            return Flux.empty();
                        }
                        return Flux.create(sink -> exeFunc.execute(stmt.secondStmt(), option, Option.singleFunc(Option.FIRST_DML_STATES, states))
                                .onErrorMap(errorFunc)
                                .subscribe(new ValidateItemCountSubscriber<>(sink, rowCount))
                        );
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
                mono = this.stmtExecutor.insert((SimpleStmt) stmt, option, Option.EMPTY_FUNC)
                        .onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairStmt)) {
                mono = Mono.error(_Exceptions.unexpectedStmt(stmt));
            } else if (inTransaction()) {
                final PairStmt pairStmt = (PairStmt) stmt;
                final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();

                mono = this.stmtExecutor.insert(pairStmt.firstStmt(), option, Option.EMPTY_FUNC)
                        .flatMap(parentStates -> this.stmtExecutor.insert(pairStmt.secondStmt(), option, Option.singleFunc(Option.FIRST_DML_STATES, parentStates))
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
                mono = this.stmtExecutor.update((SimpleStmt) stmt, option, Option.EMPTY_FUNC);
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

                mono = this.stmtExecutor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);
                if (optimisticLockValidator != null) {
                    mono = mono.doOnNext(optimisticLockValidator);
                }
                mono = mono.flatMap(childStates -> this.stmtExecutor.update(pairStmt.secondStmt(), option, Option.singleFunc(Option.FIRST_DML_STATES, childStates))
                        .doOnSuccess(parentStates -> {
                            if (parentStates.affectedRows() != childStates.affectedRows()) {
                                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.affectedRows());
                            }
                        })
                ).onErrorMap(this::handlePairStmtError);
            } else {
                final PairStmt pairStmt = (PairStmt) stmt;
                mono = this.stmtExecutor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC)
                        .then(this.stmtExecutor.update(pairStmt.secondStmt(), option, Option.EMPTY_FUNC));
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
        return this.stmtExecutor.close();
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


    private static <R> Mono<R> onlyRow(final List<R> resultList) {
        final Mono<R> mono;
        switch (resultList.size()) {
            case 1:
                mono = Mono.just(resultList.get(0));
                break;
            case 0:
                mono = Mono.empty();
                break;
            default:
                mono = Mono.error(_Exceptions.nonSingleRow(resultList));
        }
        return mono;
    }


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


    @FunctionalInterface
    private interface ExecutorFunction<R> {

        Flux<R> execute(SingleSqlStmt stmt, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    }


}
