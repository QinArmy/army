package io.army.reactive;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.criteria.impl.inner._Statement;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.CurrentRecord;
import io.army.session.Option;
import io.army.session.ResultStates;
import io.army.session._ArmySession;
import io.army.stmt.*;
import io.army.util.ArmyCriteria;
import io.army.util._Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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

    final ArmyReactiveSessionFactory factory;

    final StmtExecutor stmtExecutor;


    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    protected ArmyReactiveSession(ArmyReactiveSessionFactory.ReactiveSessionBuilder<?, ?> builder) {
        super(builder);
        this.factory = (ArmyReactiveSessionFactory) builder.armyFactory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
    }


    @Override
    public final Mono<?> setSavePoint() {
        return this.setSavePoint(Option.EMPTY_OPTION_FUNC);
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
    public Flux<ResultStates> batchUpdate(BatchDmlStatement statement, ReactiveStmtOption option) {
        return null;
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.defer(this::closeSession);
    }

    /*-------------------below package methods -------------------*/

    abstract ReactiveStmtOption defaultOption();

    abstract <T> Mono<T> closeSession();


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
        } catch (Exception e) {
            flux = Flux.error(_Exceptions.unknownError(e));
        } catch (Throwable e) {
            flux = Flux.error(e);
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
        } catch (Exception e) {
            flux = Flux.error(_Exceptions.unknownError(e));
        } catch (Throwable e) {
            flux = Flux.error(e);
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
                    .flatMapMany(states -> validateCount(exeFunc.apply(stmt.secondStmt()), states));
        }
        return flux;
    }


    private <R> Flux<R> validateCount(Flux<R> source, ResultStates states) {
        return null;
    }

    /**
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeInsert(InsertStatement statement, ReactiveStmtOption option) {
        return Mono.empty();
    }

    /**
     * @see #update(SimpleDmlStatement, ReactiveStmtOption)
     */
    private Mono<ResultStates> executeUpdate(SimpleDmlStatement statement, ReactiveStmtOption option) {
        return Mono.empty();
    }


    private boolean sessionStatusError(Statement statement, ReactiveStmtOption option) {
        if (this.sessionClosed.get()) {
            return true;
        }


    }


}
