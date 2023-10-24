package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.reactive.executor.LocalStmtExecutor;
import io.army.session.CurrentRecord;
import io.army.session.Option;
import io.army.session.ResultStates;
import io.army.session.SessionException;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is a implementation of {@link ReactiveLocalSession}.
 */
final class ArmyReactiveLocalSession extends ArmyReactiveSession implements ReactiveLocalSession {


    private final ArmyReactiveLocalSessionFactory factory;

    private final LocalStmtExecutor stmtExecutor;
    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    private final boolean readonly;

    private final AtomicReference<Transaction> sessionTransaction = new AtomicReference<>(null);

    ArmyReactiveLocalSession(ArmyReactiveLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        this.factory = builder.sessionFactory;
        this.stmtExecutor = builder.stmtExecutor;
        this.readonly = builder.readOnly;
    }


    @Override
    public ReactiveLocalSessionFactory sessionFactory() {
        return this.factory;
    }


    @Override
    public long sessionIdentifier() throws SessionException {
        return this.stmtExecutor.sessionIdentifier();
    }


    @Override
    public boolean isClosed() {
        // just test sessionClosed
        return this.sessionClosed.get();
    }

    @Override
    public boolean inTransaction() {
        return this.stmtExecutor.inTransaction();
    }

    @Override
    public boolean isReadOnlyStatus() {
        return false;
    }



    /*-------------------below statement methods -------------------*/


    @Override
    public <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public Mono<ResultStates> update(SimpleDmlStatement dml, ReactiveOption option) {
        return null;
    }

    @Override
    public Flux<ResultStates> batchUpdate(BatchDmlStatement statement, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer, ReactiveOption option) {
        return null;
    }

    /*-------------------below local transaction methods -------------------*/

    @Override
    public Mono<TransactionInfo> transactionInfo() {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> startTransaction() {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> startTransaction(TransactionOption option) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> commit() {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> commit(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public Mono<Object> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> rollback() {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> rollback(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint) {
        return null;
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return null;
    }


}
