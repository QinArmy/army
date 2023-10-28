package io.army.reactive;

import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.session.*;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;
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

    private static final AtomicReferenceFieldUpdater<ArmyReactiveLocalSession, TransactionInfo> TRANSACTION_INFO =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, TransactionInfo.class, "transactionInfo");

    private static final AtomicIntegerFieldUpdater<ArmyReactiveLocalSession> ROLLBACK_ONLY =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, "rollbackOnly");

    private volatile TransactionInfo transactionInfo;

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
        boolean in;
        try {
            in = this.stmtExecutor.inTransaction();
        } catch (DataAccessException e) {
            in = hasTransactionInfo();
        }
        return in;

    }

    @Override
    public boolean hasTransactionInfo() {
        return this.transactionInfo != null;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final boolean readOnlyStatus;
        final TransactionInfo info;
        if (this.readonly) {
            readOnlyStatus = true;
        } else if ((info = this.transactionInfo) == null) {
            readOnlyStatus = false;
        } else {
            readOnlyStatus = info.isReadOnly();
        }
        return readOnlyStatus;
    }



    /*-------------------below statement methods -------------------*/


    /*-------------------below local transaction methods -------------------*/

    @Override
    public Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.setTransactionCharacteristics(option)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }


    @Override
    public Mono<TransactionInfo> startTransaction() {
        return this.startTransaction(TransactionOption.option(null, false));
    }

    @Override
    public Mono<TransactionInfo> startTransaction(final TransactionOption option) {
        final Mono<TransactionInfo> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (inTransaction()) {
            mono = Mono.error(_Exceptions.existsTransaction(this));
        } else {
            mono = ((ReactiveLocalStmtExecutor) this.stmtExecutor).startTransaction(option)
                    .doOnSuccess(info -> TRANSACTION_INFO.set(this, info))
                    .onErrorMap(error -> {
                        TRANSACTION_INFO.set(this, null);
                        return error;
                    });
        }
        return mono;
    }


    @Override
    public ReactiveLocalSession markRollbackOnly() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (!hasTransactionInfo()) {
            throw _Exceptions.noTransaction(this);
        }
        ROLLBACK_ONLY.compareAndSet(this, 0, 1);
        return this;
    }

    @Override
    public Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> commit(Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveLocalSession> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (ROLLBACK_ONLY.get(this) != 0) {
            mono = Mono.error(_Exceptions.rollbackOnlyTransaction(this));
        } else {
            mono = ((ReactiveLocalStmtExecutor) this.stmtExecutor).commit(optionFunc)
                    .doOnSuccess(this::handleTransactionEndSuccess)
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public Mono<ReactiveLocalSession> rollback() {
        return this.rollback(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollback(Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveLocalSession> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else {
            mono = ((ReactiveLocalStmtExecutor) this.stmtExecutor).rollback(optionFunc)
                    .doOnSuccess(this::handleTransactionEndSuccess)
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }


    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return this.releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint) {
        return this.rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }


    /*-------------------below package methods -------------------*/

    @Override
    ReactiveStmtOption defaultOption() {
        return null;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    void markRollbackOnlyOnError(Throwable cause) {
        ROLLBACK_ONLY.compareAndSet(this, 0, 1);
    }

    /*-------------------below private methods -------------------*/

    /**
     * @see #commit(Function)
     * @see #rollback(Function)
     */
    @SuppressWarnings("all")
    private void handleTransactionEndSuccess(Optional<TransactionInfo> optional) {
        if (optional.isPresent()) {
            TRANSACTION_INFO.set(this, optional.get());
        } else {
            TRANSACTION_INFO.set(this, null);
        }
        ROLLBACK_ONLY.set(this, 0);
    }


}
