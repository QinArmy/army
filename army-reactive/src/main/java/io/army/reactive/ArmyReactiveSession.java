package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.reactive.executor.ReactiveStmtExecutor;
import io.army.session.*;
import io.army.stmt.*;
import io.army.tx.TransactionInfo;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmyReactiveLocalSession}</li>
 *     <li>{@link ArmyReactiveRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implementation of {@link ReactiveSession}.
 *
 * @since 1.0
 */
abstract class ArmyReactiveSession extends _ArmySession implements ReactiveSession {

    final ReactiveStmtExecutor stmtExecutor;
    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    protected ArmyReactiveSession(ArmyReactiveSessionFactory.ReactiveSessionBuilder<?, ?> builder) {
        super(builder);
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
    }

    @Override
    public final <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, defaultOption());
    }

    @Override
    public final <R> Flux<R> query(SimpleDqlStatement statement, final Class<R> resultClass, final ReactiveStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.query(s, resultClass, option));
    }


    @Override
    public final <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.queryOptional(statement, resultClass, defaultOption());
    }

    @Override
    public <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.queryOptional(s, resultClass, option));
    }

    @Override
    public final <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.queryObject(s, constructor, option));
    }

    @Override
    public final <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.queryRecord(s, function, option));
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass) {
        return this.batchQuery(statement, resultClass, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option) {
        return this.executeBatchQuery(statement, option, s -> this.stmtExecutor.batchQuery(s, resultClass, option));
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor) {
        return this.batchQueryObject(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option) {
        return this.executeBatchQuery(statement, option, s -> this.stmtExecutor.batchQueryObject(s, constructor, option));
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.batchQueryRecord(statement, function, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        return this.executeBatchQuery(statement, option, s -> this.stmtExecutor.batchQueryRecord(s, function, option));
    }

    @Override
    public final Mono<ResultStates> save(Object domain) {
        return this.update(ArmyCriteria.insertStmt(this, domain), defaultOption());
    }

    @Override
    public final Mono<ResultStates> update(SimpleDmlStatement statement) {
        return this.update(statement, defaultOption());
    }

    @Override
    public final Mono<ResultStates> update(SimpleDmlStatement statement, ReactiveStmtOption option) {
        final Mono<ResultStates> mono;
        if (statement instanceof _BatchStatement) {
            mono = Mono.error(_Exceptions.unexpectedStatement(statement));
        } else if (statement instanceof InsertStatement) {
            mono = this.executeInsert((InsertStatement) statement, option);
        } else {
            mono = this.executeUpdate(statement, option);
        }
        return mono;
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), defaultOption());
    }

    @Override
    public final Flux<ResultStates> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, defaultOption());
    }

    @Override
    public Flux<ResultStates> batchUpdate(BatchDmlStatement statement, final ReactiveStmtOption option) {
        Flux<ResultStates> flux;
        try {
            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);
            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            if (stmt instanceof BatchStmt) {
                flux = this.stmtExecutor.batchUpdate((BatchStmt) stmt, option)
                        .onErrorMap(this::handleExecutionError);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (inTransaction()) {
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;
                final ChildTableMeta<?> domainTable;
                domainTable = (ChildTableMeta<?>) getBatchUpdateDomainTable(statement); // fail, bug.

                assert domainTable != null; // fail, bug.

                flux = this.stmtExecutor.batchUpdate(pairStmt.firstStmt(), option)
                        .collectMap(ResultStates::getResultNo, states -> states, _Collections::hashMap)
                        .flatMapMany(statesMap -> validateBatchStates(this.stmtExecutor.batchUpdate(pairStmt.secondStmt(), option), statesMap, domainTable))
                        .onErrorMap(this::handlePairStmtError);
            } else {
                throw updateChildNoTransaction();
            }
        } catch (Throwable e) {
            flux = Flux.error(_Exceptions.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }

        return flux;
    }

    @Override
    public final long sessionIdentifier() throws SessionException {
        if (this.sessionClosed.get()) {
            throw _Exceptions.sessionClosed(this);
        }
        try {
            return this.stmtExecutor.sessionIdentifier();
        } catch (Exception e) {
            throw new SessionException(e.getMessage(), e);
        }
    }

    @Override
    public final Mono<TransactionInfo> transactionInfo() {
        return this.stmtExecutor.transactionInfo()
                .onErrorMap(_Exceptions::wrapIfNeed);
    }

    @Override
    public final Mono<?> setSavePoint() {
        return this.setSavePoint(Option.EMPTY_OPTION_FUNC);
    }


    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.setSavePoint(optionFunc)
                .onErrorMap(_Exceptions::wrapIfNeed);
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    public final boolean isClosed() {
        return this.sessionClosed.get();
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.defer(this::closeSession);
    }



    /*-------------------below package methods -------------------*/

    abstract ReactiveStmtOption defaultOption();

    abstract void markRollbackOnlyOnError(Throwable cause);

    /*-------------------below private methods -------------------*/

    /**
     * @see #query(SimpleDqlStatement, Class, ReactiveStmtOption)
     * @see #queryObject(SimpleDqlStatement, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SimpleDqlStatement, Function, ReactiveStmtOption)
     */
    private <R> Flux<R> executeQuery(final SimpleDqlStatement statement, final ReactiveStmtOption option,
                                     final Function<SimpleStmt, Flux<R>> exeFunc) {

        Flux<R> flux;
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);
            if (stmt instanceof SimpleStmt) {
                flux = exeFunc.apply((SimpleStmt) stmt);
            } else if (!(stmt instanceof PairStmt)) {
                // no bug,never here
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (statement instanceof InsertStatement) {
                flux = returningInsertPairStmt((InsertStatement) statement, (PairStmt) stmt, option, exeFunc);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                throw _Exceptions.unexpectedStatement(statement);
            }
        } catch (Throwable e) {
            flux = Flux.error(_Exceptions.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
        return flux;
    }

    /**
     * @see #batchQuery(BatchDqlStatement, Class, ReactiveStmtOption)
     * @see #batchQueryObject(BatchDqlStatement, Supplier)
     * @see #batchQueryRecord(BatchDqlStatement, Function)
     */
    private <R> Flux<R> executeBatchQuery(final BatchDqlStatement statement, final ReactiveStmtOption option,
                                          final Function<BatchStmt, Flux<R>> exeFunc) {
        Flux<R> flux;
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);
            if (stmt instanceof BatchStmt) {
                flux = exeFunc.apply((BatchStmt) stmt);
            } else if (!(stmt instanceof PairBatchStmt)) {
                // no bug,never here
                throw _Exceptions.unexpectedStmt(stmt);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                throw _Exceptions.unexpectedStatement(statement);
            }
        } catch (Throwable e) {
            flux = Flux.error(_Exceptions.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
        return flux;
    }


    /**
     * @see #query(SimpleDqlStatement, Class, ReactiveStmtOption)
     */
    private <R> Flux<R> returningInsertPairStmt(final InsertStatement statement, final PairStmt stmt,
                                                final ReactiveStmtOption option, final Function<SimpleStmt, Flux<R>> exeFunc) {

        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;

        final Flux<R> flux;
        if (firstStmtIsQuery) {
            flux = exeFunc.apply(stmt.firstStmt())
                    .collectList()
                    .flatMapMany(resultList -> this.stmtExecutor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), resultList, option));
        } else {
            flux = this.stmtExecutor.insert(stmt.firstStmt(), option)
                    .flatMapMany(states -> Flux.create(sink -> exeFunc.apply(stmt.secondStmt())
                                    .subscribe(new ValidateItemCountSubscriber<>(sink, states))
                            )
                    );
        }
        return flux;
    }

    /**
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeInsert(InsertStatement statement, ReactiveStmtOption option) {
        Mono<ResultStates> mono;
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseInsertStatement(statement);
            if (stmt instanceof SimpleStmt) {
                mono = this.stmtExecutor.insert((SimpleStmt) stmt, option)
                        .onErrorMap(this::handleExecutionError);
            } else if (stmt instanceof PairStmt) {
                final PairStmt pairStmt = (PairStmt) stmt;
                final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();

                mono = this.stmtExecutor.insert(pairStmt.firstStmt(), option)
                        .flatMap(parentStates -> this.stmtExecutor.insert(pairStmt.secondStmt(), option)
                                .doOnSuccess(childStates -> {
                                    if (childStates.affectedRows() != parentStates.affectedRows()) {
                                        throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.affectedRows());
                                    }
                                })
                        ).onErrorMap(this::handlePairStmtError);
            } else {
                mono = Mono.error(_Exceptions.unexpectedStmt(stmt));
            }
        } catch (Throwable e) {
            mono = Mono.error(_Exceptions.wrapIfNeed(e));
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
        return mono;
    }


    /**
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeUpdate(SimpleDmlStatement statement, ReactiveStmtOption option) {
        Mono<ResultStates> mono;
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDmlStatement(statement, option);
            if (stmt instanceof SimpleStmt) {
                mono = this.stmtExecutor.update((SimpleStmt) stmt, option)
                        .onErrorMap(this::handleExecutionError);
            } else if (stmt instanceof PairStmt) {
                final PairStmt pairStmt = (PairStmt) stmt;
                final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();

                mono = this.stmtExecutor.update(pairStmt.firstStmt(), option)
                        .flatMap(parentStates -> this.stmtExecutor.update(pairStmt.secondStmt(), option)
                                .doOnSuccess(childStates -> {
                                    if (childStates.affectedRows() != parentStates.affectedRows()) {
                                        throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentStates.affectedRows(), childStates.affectedRows());
                                    }
                                })
                        ).onErrorMap(this::handlePairStmtError);
            } else {
                mono = Mono.error(_Exceptions.unexpectedStmt(stmt));
            }
        } catch (Throwable e) {
            mono = Mono.error(_Exceptions.wrapIfNeed(e));
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
        if (!this.sessionClosed.compareAndSet(false, true)) {
            return Mono.empty();
        }
        return this.stmtExecutor.close();
    }

    private Throwable handleExecutionError(final Throwable cause) {
        return _Exceptions.wrapIfNeed(cause);
    }

    private Throwable handlePairStmtError(final Throwable cause) {
        if (cause instanceof ChildUpdateException && hasTransaction()) {
            markRollbackOnlyOnError(cause);
        }
        return handleExecutionError(cause);
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

        private final ResultStates resultStates;

        private final AtomicLong count = new AtomicLong(0);


        private ValidateItemCountSubscriber(FluxSink<T> sink, ResultStates resultStates) {
            this.sink = sink.onRequest(this::onRequest);
            this.resultStates = resultStates;
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
            if (this.count.get() == this.resultStates.affectedRows()) {
                this.sink.complete();
            } else {
                String m = String.format("parent insert row[%s] and child insert row[%s] not match.",
                        this.resultStates.affectedRows(), this.count.get());
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
            childStates = this.statesMap.get(states.getResultNo());
            if (childStates == null) {
                String m = String.format("Not found %s for batch item[%s] , %s", ResultStates.class.getName(),
                        states.getResultNo(), this.domainTable);
                ERROR.compareAndSet(this, null, new ChildUpdateException(m));
            } else if (childStates.affectedRows() == states.affectedRows()) {
                this.sink.next(states);
            } else {
                final ChildUpdateException e;
                e = _Exceptions.batchChildUpdateRowsError(this.domainTable, states.getResultNo(), childStates.affectedRows(),
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


    }//ValidateBatchStatesSubscriber


}
