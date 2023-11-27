package io.army.reactive;

import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Function;

/**
 * This class is a implementation of {@link ReactiveLocalSession}.
 *
 * @see ArmyReactiveSessionFactory
 */
class ArmyReactiveLocalSession extends ArmyReactiveSession implements ReactiveLocalSession {

    /**
     * @see ArmyReactiveSessionFactory.ArmyLocalBuilder#createSession(String, boolean)
     */
    static ArmyReactiveLocalSession create(ArmyReactiveSessionFactory.ArmyLocalBuilder builder) {
        final ArmyReactiveLocalSession session;
        if (builder.inOpenDriverSpi()) {
            session = new OpenDriverSpiSession(builder);
        } else {
            session = new ArmyReactiveLocalSession(builder);
        }
        return session;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmyReactiveLocalSession.class);

    private static final AtomicReferenceFieldUpdater<ArmyReactiveLocalSession, TransactionInfo> TRANSACTION_INFO =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, TransactionInfo.class, "transactionInfo");

    private static final AtomicIntegerFieldUpdater<ArmyReactiveLocalSession> ROLLBACK_ONLY =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveLocalSession.class, "rollbackOnly");

    private volatile TransactionInfo transactionInfo;

    private volatile int rollbackOnly;

    /**
     * private constructor
     *
     * @see ArmyReactiveLocalSession#create(ArmyReactiveSessionFactory.ArmyLocalBuilder)
     */
    private ArmyReactiveLocalSession(ArmyReactiveSessionFactory.ArmyLocalBuilder builder) {
        super(builder);
    }


    @Override
    public final boolean isRollbackOnly() {
        return ROLLBACK_ONLY.get(this) != 0;
    }

    @Override
    public final Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.setTransactionCharacteristics(option)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }


    /*-------------------below local transaction methods -------------------*/


    @Override
    public final Mono<TransactionInfo> startTransaction() {
        return startTransaction(TransactionOption.option(null, false), HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final Mono<TransactionInfo> startTransaction(final TransactionOption option) {
        return startTransaction(option, HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final Mono<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return ((ReactiveLocalStmtExecutor) this.stmtExecutor).startTransaction(option, mode)
                .doOnSuccess(info -> {
                    assert info.inTransaction();
                    assert option.isolation() == null || info.isolation().equals(option.isolation());
                    assert info.isReadOnly() == option.isReadOnly();
                    if (option.valueOf(Option.TIMEOUT_MILLIS) != null) {
                        assert info.valueOf(Option.TIMEOUT_MILLIS) != null;
                        assert info.valueOf(Option.START_MILLIS) != null;
                    }

                    TRANSACTION_INFO.set(this, info);
                    ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                })
                .onErrorMap(error -> {
                    TRANSACTION_INFO.set(this, null);
                    ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                    return _ArmySession.wrapIfNeed(error);
                });
    }


    @Override
    public final void markRollbackOnly() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        ROLLBACK_ONLY.compareAndSet(this, 0, 1);
    }

    @Override
    public final Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_OPTION_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc) {
        final Mono<Optional<TransactionInfo>> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (ROLLBACK_ONLY.get(this) != 0) {
            mono = Mono.error(_Exceptions.rollbackOnlyTransaction(this));
        } else {
            mono = ((ReactiveLocalStmtExecutor) this.stmtExecutor).commit(optionFunc)
                    .doOnSuccess(this::handleTransactionEndSuccess)
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveLocalSession> rollback() {
        return rollback(Option.EMPTY_OPTION_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return ((ReactiveLocalStmtExecutor) this.stmtExecutor).rollback(optionFunc)
                .doOnSuccess(this::handleTransactionEndSuccess)
                .onErrorMap(_ArmySession::wrapIfNeed);
    }


    @Override
    public final Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public final Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint) {
        return rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    /*-------------------below protected methods -------------------*/

    @Nullable
    @Override
    protected final TransactionInfo obtainTransactionInfo() {
        return this.transactionInfo;
    }


    /*-------------------below package methods -------------------*/


    @Override
    protected final Logger getLogger() {
        return LOG;
    }


    @Override
    protected final void rollbackOnlyOnError(ChildUpdateException cause) {
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
        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
    }


    private static final class OpenDriverSpiSession extends ArmyReactiveLocalSession implements DriverSpiHolder {

        /**
         * @see ArmyReactiveLocalSession#create(ArmyReactiveSessionFactory.ArmyLocalBuilder)
         */
        private OpenDriverSpiSession(ArmyReactiveSessionFactory.ArmyLocalBuilder builder) {
            super(builder);
        }

        @Override
        public boolean isDriverAssignableTo(Class<?> spiClass) {
            if (isClosed()) {
                throw _Exceptions.sessionClosed(this);
            }
            return this.stmtExecutor.isDriverAssignableTo(spiClass);
        }

        @Override
        public <T> T getDriverSpi(Class<T> spiClass) {
            if (isClosed()) {
                throw _Exceptions.sessionClosed(this);
            }
            return this.stmtExecutor.getDriverSpi(spiClass);
        }


    } // OpenDriverSpiSession


}
