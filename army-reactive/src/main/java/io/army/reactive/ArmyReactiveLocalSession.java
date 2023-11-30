package io.army.reactive;

import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
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
     * @see ArmyReactiveSessionFactory.LocalBuilder#createSession(String, boolean)
     */
    static ArmyReactiveLocalSession create(ArmyReactiveSessionFactory.LocalBuilder builder) {
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
     * @see ArmyReactiveLocalSession#create(ArmyReactiveSessionFactory.LocalBuilder)
     */
    private ArmyReactiveLocalSession(ArmyReactiveSessionFactory.LocalBuilder builder) {
        super(builder);
    }


    @Override
    public final boolean isRollbackOnly() {
        return this.rollbackOnly != 0;
    }

    @Override
    public final void markRollbackOnly() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (ROLLBACK_ONLY.compareAndSet(this, 0, 1)) {
            final TransactionInfo info = this.transactionInfo;
            if (info != null) {
                this.transactionInfo = wrapRollbackOnly(info);
            }
        }

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
    public final Mono<TransactionInfo> pseudoTransaction(final TransactionOption option, final HandleMode mode) {
        final Mono<TransactionInfo> mono;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (!this.readonly) {
            mono = Mono.error(_Exceptions.writeSessionPseudoTransaction(this));
        } else if (option.isolation() != Isolation.PSEUDO) {
            mono = Mono.error(_Exceptions.pseudoIsolationError(this, option));
        } else if (!option.isReadOnly()) {
            mono = Mono.error(_Exceptions.pseudoWriteError(this, option));
        } else if (this.transactionInfo == null) {
            mono = doPseudoTransaction(option);
        } else switch (mode) {
            case ERROR_IF_EXISTS:
                mono = Mono.error(_Exceptions.existsTransaction(this));
                break;
            case COMMIT_IF_EXISTS:
                mono = commit()
                        .then(Mono.defer(() -> doPseudoTransaction(option)));
                break;
            case ROLLBACK_IF_EXISTS:
                mono = rollback()
                        .then(Mono.defer(() -> doPseudoTransaction(option)));
                break;
            default:
                mono = Mono.error(_Exceptions.unexpectedEnum(mode));
        }
        return mono;
    }


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
        return ((ReactiveLocalExecutor) this.stmtExecutor).startTransaction(option, mode)
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
    public final Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_FUNC)
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
            mono = ((ReactiveLocalExecutor) this.stmtExecutor).commit(optionFunc)
                    .doOnSuccess(this::handleTransactionEndSuccess)
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveLocalSession> commitIfExists() {
        return commitIfExists(Option.EMPTY_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> commitIfExists(final Function<Option<?>, ?> optionFunc) {
        return Mono.defer(() -> {
            final TransactionInfo info = this.transactionInfo;
            if ((info != null && info.isolation() == Isolation.PSEUDO) || inTransaction()) {
                return commit(optionFunc);
            }
            return Mono.empty();
        });
    }

    @Override
    public final Mono<ReactiveLocalSession> rollback() {
        return rollback(Option.EMPTY_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return ((ReactiveLocalExecutor) this.stmtExecutor).rollback(optionFunc)
                .doOnSuccess(this::handleTransactionEndSuccess)
                .onErrorMap(_ArmySession::wrapIfNeed);
    }

    @Override
    public final Mono<ReactiveLocalSession> rollbackIfExists() {
        return rollbackIfExists(Option.EMPTY_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> rollbackIfExists(final Function<Option<?>, ?> optionFunc) {
        return Mono.defer(() -> {
            final TransactionInfo info = this.transactionInfo;
            if ((info != null && info.isolation() == Isolation.PSEUDO) || inTransaction()) {
                return rollback(optionFunc);
            }
            return Mono.empty();
        });
    }

    @Override
    public final Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return releaseSavePoint(savepoint, Option.EMPTY_FUNC);
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
        return rollbackToSavePoint(savepoint, Option.EMPTY_FUNC);
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
        markRollbackOnly();
    }

    /*-------------------below private methods -------------------*/


    /**
     * @see #pseudoTransaction(TransactionOption, HandleMode)
     */
    private Mono<TransactionInfo> doPseudoTransaction(final TransactionOption option) {
        final TransactionInfo pseudoInfo;
        pseudoInfo = TransactionInfo.info(false, Isolation.PSEUDO, true, wrapStartMillis(null, option));

        if (!TRANSACTION_INFO.compareAndSet(this, null, pseudoInfo)) {
            return Mono.error(new ConcurrentModificationException());
        }
        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
        return Mono.just(pseudoInfo);
    }

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
         * @see ArmyReactiveLocalSession#create(ArmyReactiveSessionFactory.LocalBuilder)
         */
        private OpenDriverSpiSession(ArmyReactiveSessionFactory.LocalBuilder builder) {
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
