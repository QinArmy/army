package io.army.reactive;

import io.army.reactive.executor.LocalStmtExecutor;
import io.army.session.DriverSpi;
import io.army.session.Option;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Function;

/**
 * This class is a implementation of {@link ReactiveLocalSession}.
 *
 * @see ArmyReactiveLocalSessionFactory
 */
final class ArmyReactiveLocalSession extends ArmyReactiveSession implements ReactiveLocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyReactiveLocalSession.class);

    private static final AtomicIntegerFieldUpdater<ArmyReactiveLocalSession> ROLLBACK_ONLY =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, "rollbackOnly");

    private static final AtomicReferenceFieldUpdater<ArmyReactiveLocalSession, TransactionInfo> TRANSACTION =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, TransactionInfo.class, "transaction");


    private volatile TransactionInfo transaction;

    private volatile int rollbackOnly;

    ArmyReactiveLocalSession(ArmyReactiveLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
    }


    @Override
    public ReactiveLocalSessionFactory sessionFactory() {
        return (ReactiveLocalSessionFactory) this.factory;
    }


    @Override
    public boolean inTransaction() {
        final boolean in;
        if (((ArmyReactiveLocalSessionFactory) this.factory).driverSpi != DriverSpi.JDBD) {
            in = this.transaction != null;
        } else switch (this.factory.serverDatabase) {
            case MySQL:
            case PostgreSQL:
                in = this.stmtExecutor.inTransaction();
                break;
            default:
                in = this.transaction != null;
        }
        return in;
    }

    @Override
    public boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final boolean readOnlyStatus;
        final TransactionOption option;
        if (this.readonly) {
            readOnlyStatus = true;
        } else if ((option = this.sessionTransaction.get()) == null) {
            readOnlyStatus = false;
        } else {
            readOnlyStatus = option.isReadOnly();
        }
        return readOnlyStatus;
    }



    /*-------------------below statement methods -------------------*/


    /*-------------------below local transaction methods -------------------*/


    @Override
    public Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option) {
        return this.stmtExecutor.setTransactionCharacteristics(option)
                .thenReturn(this);
    }


    @Override
    public Mono<ReactiveLocalSession> startTransaction() {
        return this.startTransaction(TransactionOption.option(null, false));
    }

    @Override
    public Mono<ReactiveLocalSession> startTransaction(final TransactionOption option) {
        return ((LocalStmtExecutor) this.stmtExecutor).startTransaction(option)
                .doOnSuccess(v -> this.sessionTransaction.set(option))
                .thenReturn(this);
    }

    @Override
    public ReactiveLocalSession markRollbackOnly() {

        return this;
    }

    @Override
    public Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> commit(Function<Option<?>, ?> optionFunc) {
        return ((LocalStmtExecutor) this.stmtExecutor).commit(optionFunc)
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> rollback() {
        return this.rollback(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollback(Function<Option<?>, ?> optionFunc) {
        return ((LocalStmtExecutor) this.stmtExecutor).rollback(optionFunc)
                .thenReturn(this);
    }


    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return this.releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint) {
        return this.rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc)
                .thenReturn(this);
    }


    /*-------------------below package methods -------------------*/

    @Override
    ReactiveStmtOption defaultOption() {
        return null;
    }

    @Override
    protected String transactionName() {
        return "unnamed";
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    void markRollbackOnlyOnError(Throwable cause) {

    }

    /*-------------------below private methods -------------------*/


}
