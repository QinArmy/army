package io.army.reactive;

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
 * This class is a implementation of {@link ReactiveRmSession}.
 *
 * @see ArmyReactiveRmSessionFactory
 */
class ArmyReactiveRmSession extends ArmyReactiveSession implements ReactiveRmSession {

    /**
     * @see ArmyReactiveRmSessionFactory.RmSessionBuilder#createSession(String, boolean)
     */
    static ArmyReactiveRmSession create(ArmyReactiveRmSessionFactory.RmSessionBuilder builder) {
        final ArmyReactiveRmSession session;
        if (builder.inOpenDriverSpi()) {
            session = new OpenDriverSpiSession(builder);
        } else {
            session = new ArmyReactiveRmSession(builder);
        }
        return session;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmyReactiveRmSession.class);

    private static final AtomicReferenceFieldUpdater<ArmyReactiveRmSession, TransactionInfo> TRANSACTION_INFO =
            AtomicReferenceFieldUpdater.newUpdater(ArmyReactiveRmSession.class, TransactionInfo.class, "transactionInfo");

    private static final AtomicIntegerFieldUpdater<ArmyReactiveRmSession> ROLLBACK_ONLY =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveRmSession.class, "rollbackOnly");

    private volatile TransactionInfo transactionInfo;

    private volatile int rollbackOnly;


    /**
     * private constructor
     */
    private ArmyReactiveRmSession(ArmyReactiveRmSessionFactory.RmSessionBuilder builder) {
        super(builder);
    }

    @Override
    public final ReactiveRmSessionFactory sessionFactory() {
        return (ReactiveRmSessionFactory) this.factory;
    }

    @Override
    public final boolean isRollbackOnly() {
        return ROLLBACK_ONLY.get(this) != 0;
    }

    @Override
    public final Mono<ReactiveRmSession> setTransactionCharacteristics(TransactionOption option) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.setTransactionCharacteristics(option)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public final Mono<ReactiveRmSession> releaseSavePoint(Object savepoint) {
        return releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public final Mono<ReactiveRmSession> rollbackToSavePoint(Object savepoint) {
        return rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public final Mono<TransactionInfo> start(Xid xid) {
        return start(xid, TM_NO_FLAGS, TransactionOption.option(null, false));
    }

    @Override
    public final Mono<TransactionInfo> start(Xid xid, int flags) {
        return start(xid, flags, TransactionOption.option(null, false));
    }

    @Override
    public final Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid) {
        return end(xid, TM_NO_FLAGS, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid, int flags) {
        return end(xid, flags, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Mono<Integer> prepare(Xid xid) {
        return prepare(xid, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid, int flags) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(Xid xid) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> forget(Xid xid) {
        return null;
    }

    @Override
    public final Mono<ReactiveRmSession> forget(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Mono<Optional<Xid>> recover(int flags) {
        return null;
    }

    @Override
    public final Mono<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final boolean isSupportForget() {
        return false;
    }

    @Override
    public final int startSupportFlags() {
        return 0;
    }

    @Override
    public final int endSupportFlags() {
        return 0;
    }

    @Override
    public final int commitSupportFlags() {
        return 0;
    }

    @Override
    public final int recoverSupportFlags() {
        return 0;
    }

    @Override
    public final boolean isSameRm(XaTransactionSupportSpec s) throws SessionException {
        return false;
    }


    @Override
    protected final Logger getLogger() {
        return null;
    }

    @Nullable
    @Override
    protected final TransactionInfo obtainTransactionInfo() {
        return null;
    }

    @Override
    protected final void rollbackOnlyOnError(ChildUpdateException cause) {

    }


    @Override
    final ReactiveStmtOption defaultOption() {
        return null;
    }


    private static final class OpenDriverSpiSession extends ArmyReactiveRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmyReactiveRmSessionFactory.RmSessionBuilder builder) {
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
