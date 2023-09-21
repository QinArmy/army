package io.army.jdbd;

import io.army.ArmyException;
import io.army.reactive.MultiResult;
import io.army.reactive.QueryResults;
import io.army.reactive.StatementOption;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.*;
import io.army.stmt.*;
import io.army.util._Exceptions;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.SavePoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class JdbdStmtExecutor<E extends StmtExecutor, S extends DatabaseSession> extends ExecutorSupport implements StmtExecutor {

    final JdbdStmtExecutorFactory factory;

    final S session;

    JdbdStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        this.factory = factory;
        this.session = session;
    }


    @Override
    public final long sessionIdentifier() throws DataAccessException {
        try {
            return this.session.sessionIdentifier();
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }

    @Override
    public final boolean inTransaction() throws DataAccessException {
        try {
            return this.session.inTransaction();
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }

    @Override
    public final Mono<TransactionStatus> transactionStatus() {
        return Mono.from(this.session.transactionStatus())
                .map(this::mapToArmyTransactionStatus)
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }


    @SuppressWarnings("unchecked")
    @Override
    public final Mono<E> setTransactionCharacteristics(TransactionOption option) {
        return Mono.from(this.session.setTransactionCharacteristics(mapToJdbdTransactionOption(option)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return Mono.from(this.session.setSavePoint(readArmySetSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Mono<E> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.releaseSavePoint((SavePoint) savepoint, readArmyReleaseSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Mono<? extends StmtExecutor> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.rollbackToSavePoint((SavePoint) savepoint, readArmyRollbackSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @Override
    public final Mono<ResultStates> insert(SimpleStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public final Mono<ResultStates> update(SimpleStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public Flux<ResultStates> batchUpdate(BatchStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> query(SimpleStmt stmt, Class<R> resultClass, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, StatementOption option) {
        return null;
    }

    @Override
    public <R> Mono<Integer> secondQuery(TwoStmtQueryStmt stmt, List<R> resultList, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQuery(BatchStmt stmt, Class<R> resultClass, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, StatementOption option) {
        return null;
    }

    @Override
    public <R> Mono<Integer> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, StatementOption option) {
        return null;
    }

    @Override
    public QueryResults batchQuery(BatchStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public MultiResult multiStmt(MultiStmt stmt, StatementOption option) {
        return null;
    }


    @Override
    public final boolean isClosed() {
        return this.session.isClosed();
    }

    @Override
    public final <T> Mono<T> close() {
        final Mono<T> mono;
        mono = Mono.from(this.session.close());
        return mono.onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    /*-------------------below package instance methods-------------------*/

    abstract Function<Option<?>, ?> readJdbdTransactionOptions(io.jdbd.session.TransactionOption jdbdOption);

    abstract Function<Option<?>, ?> readArmyTransactionOptions(TransactionOption jdbdOption);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmySetSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyReleaseSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyRollbackSavePointOptions(Function<Option<?>, ?> optionFunc);

    /*-------------------below private instance methods-------------------*/

    /**
     * @see #transactionStatus()
     */
    private TransactionStatus mapToArmyTransactionStatus(io.jdbd.session.TransactionStatus jdbdStatus) {
        final Function<Option<?>, ?> map;
        map = readJdbdTransactionOptions(jdbdStatus);
        return null;
    }

    private io.jdbd.session.TransactionOption mapToJdbdTransactionOption(TransactionOption armyOption) {
        return null;
    }


    /*-------------------below package static methods -------------------*/

    static ArmyException wrapError(final Throwable error) {
        final ArmyException e;
        if (error instanceof JdbdException) {
            e = new DataAccessException(error);
        } else if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else {
            e = _Exceptions.unknownError(error.getMessage(), error);
        }
        return e;
    }


}
