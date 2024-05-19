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

import io.army.executor.DriverSpiHolder;
import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.session.*;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.Objects;
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
     * @see ArmyReactiveSessionFactory.LocalBuilder#createSession(String, boolean, Function)
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
                this.transactionInfo = TransactionInfo.forRollbackOnly(info);
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
    public final Mono<TransactionInfo> startTransaction() {
        return startTransaction(TransactionOption.option(null, false), HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final Mono<TransactionInfo> startTransaction(final TransactionOption option) {
        return startTransaction(option, HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final Mono<TransactionInfo> startTransaction(final TransactionOption option, final HandleMode mode) {

        final Mono<TransactionInfo> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (option.isolation() == Isolation.PSEUDO) {
            mono = pseudoTransactionInfo(option, mode);
        } else {
            mono = ((ReactiveLocalExecutor) this.stmtExecutor).startTransaction(option, mode)
                    .map(info -> {
                        assert info.inTransaction();
                        assert option.isolation() == null || info.isolation().equals(option.isolation());
                        assert info.isReadOnly() == option.isReadOnly();
                        assert info.valueOf(Option.START_MILLIS) != null;

                        assert (option.isolation() == null) == info.nonNullOf(Option.DEFAULT_ISOLATION);
                        assert Objects.equals(info.valueOf(Option.TIMEOUT_MILLIS), option.valueOf(Option.TIMEOUT_MILLIS));

                        TRANSACTION_INFO.set(this, info);
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);

                        return info;
                    })
                    .onErrorMap(error -> {
                        TRANSACTION_INFO.set(this, null);
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                        return _ArmySession.wrapIfNeed(error);
                    });
        }

        return mono;
    }


    @Override
    public final Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_FUNC)
                .thenReturn(this);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> commit(final Function<Option<?>, ?> optionFunc) {
        final Mono<Optional<TransactionInfo>> mono;
        final TransactionInfo info;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (this.rollbackOnly != 0) {
            mono = Mono.error(_Exceptions.rollbackOnlyTransaction(this));
        } else if ((info = this.transactionInfo) != null && info.isolation() == Isolation.PSEUDO) {
            TRANSACTION_INFO.set(this, null);
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just(Optional.empty());
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
    public final Mono<Optional<TransactionInfo>> rollback(final Function<Option<?>, ?> optionFunc) {
        final Mono<Optional<TransactionInfo>> mono;
        final TransactionInfo info;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if ((info = this.transactionInfo) != null && info.isolation() == Isolation.PSEUDO) {
            TRANSACTION_INFO.set(this, null);
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just(Optional.empty());
        } else {
            mono = ((ReactiveLocalExecutor) this.stmtExecutor).rollback(optionFunc)
                    .doOnSuccess(this::handleTransactionEndSuccess)
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
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
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    private Mono<TransactionInfo> pseudoTransactionInfo(final TransactionOption option, final HandleMode mode) {

        final Mono<TransactionInfo> mono;
        final TransactionInfo existTransaction;
        if (!this.readonly) {
            mono = Mono.error(_Exceptions.writeSessionPseudoTransaction(this));
        } else if (!option.isReadOnly()) {
            mono = Mono.error(_Exceptions.pseudoWriteError(this, option));
        } else if ((existTransaction = this.transactionInfo) == null) {
            mono = storePseudoTransactionInfo(null, option);
        } else switch (mode) {
            case ERROR_IF_EXISTS:
                mono = Mono.error(_Exceptions.existsTransaction(this));
                break;
            case COMMIT_IF_EXISTS: {
                if (isRollbackOnly()) {
                    mono = Mono.error(_Exceptions.rollbackOnlyTransaction(this));
                } else if (existTransaction.isolation() == Isolation.PSEUDO) {
                    mono = storePseudoTransactionInfo(existTransaction, option);
                } else {
                    mono = commit(Option.EMPTY_FUNC)
                            .then(Mono.defer(() -> storePseudoTransactionInfo(null, option)));
                }
            }
            break;
            case ROLLBACK_IF_EXISTS: {
                if (existTransaction.isolation() == Isolation.PSEUDO) {
                    mono = storePseudoTransactionInfo(existTransaction, option);
                } else {
                    mono = rollback(Option.EMPTY_FUNC)
                            .then(Mono.defer(() -> storePseudoTransactionInfo(null, option)));
                }
            }
            break;
            default:
                mono = Mono.error(_Exceptions.unexpectedEnum(mode));

        } //  switch

        return mono;
    }

    /**
     * @see #pseudoTransactionInfo(TransactionOption, HandleMode)
     */
    private Mono<TransactionInfo> storePseudoTransactionInfo(final @Nullable TransactionInfo existTransaction,
                                                             final TransactionOption option) {
        final TransactionInfo info;
        info = TransactionInfo.pseudoLocal(option);

        final Mono<TransactionInfo> mono;
        if (TRANSACTION_INFO.compareAndSet(this, existTransaction, info)) {
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just(info);
        } else {
            mono = Mono.error(new ConcurrentModificationException());
        }
        return mono;
    }


    /**
     * @see #commit(Function)
     * @see #rollback(Function)
     */
    @SuppressWarnings("all")
    private void handleTransactionEndSuccess(final Optional<TransactionInfo> optional) {
        final TransactionInfo info;
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
