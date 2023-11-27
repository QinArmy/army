package io.army.reactive;

import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Function;


/**
 * This class is a implementation of {@link ReactiveRmSession}.
 *
 * @see ArmyReactiveSessionFactory
 */
class ArmyReactiveRmSession extends ArmyReactiveSession implements ReactiveRmSession {

    /**
     * @see ArmyReactiveSessionFactory.ArmyRmBuilder#createSession(String, boolean)
     */
    static ArmyReactiveRmSession create(ArmyReactiveSessionFactory.ArmyRmBuilder builder) {
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
    private ArmyReactiveRmSession(ArmyReactiveSessionFactory.ArmyRmBuilder builder) {
        super(builder);
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
        return releaseSavePoint(savepoint, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> releaseSavePoint(Object savepoint, Function<ArmyOption<?>, ?> optionFunc) {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionClosed(this));
        }
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .onErrorMap(_ArmySession::wrapIfNeed)
                .thenReturn(this);
    }

    @Override
    public final Mono<ReactiveRmSession> rollbackToSavePoint(Object savepoint) {
        return rollbackToSavePoint(savepoint, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> rollbackToSavePoint(Object savepoint, Function<ArmyOption<?>, ?> optionFunc) {
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
    public final Mono<TransactionInfo> start(final @Nullable Xid xid, final int flags, TransactionOption option) {
        final Mono<TransactionInfo> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (this.transactionInfo != null) {
            mono = Mono.error(_Exceptions.existsTransaction(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).start(xid, flags, option)
                    .doOnSuccess(info -> {
                        assert info.inTransaction();  // fail ,executor bug
                        assert xid.equals(info.valueOf(ArmyOption.XID));  // fail ,executor bug
                        assert info.valueOf(ArmyOption.XA_STATES) == XaStates.ACTIVE;  // fail ,executor bug

                        assert info.nonNullOf(ArmyOption.XA_FLAGS) == flags;  // fail ,executor bug

                        TRANSACTION_INFO.set(this, info);
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                    }).onErrorMap(error -> {
                        TRANSACTION_INFO.set(this, null);
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                        return _ArmySession.wrapIfNeed(error);
                    });
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid) {
        return end(xid, TM_NO_FLAGS, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid, int flags) {
        return end(xid, flags, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(final @Nullable Xid xid, final int flags, Function<ArmyOption<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(ArmyOption.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(ArmyOption.XA_STATES)) != XaStates.ACTIVE) {
            mono = Mono.error(_Exceptions.xaTransactionDontSupportEndCommand(infoXid, states)); // use infoXid
        } else {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).end(infoXid, flags, optionFunc) // use infoXid
                    .doOnSuccess(info -> {
                        assert info.inTransaction();  // fail ,executor bug
                        assert infoXid.equals(info.valueOf(ArmyOption.XID));  // fail ,executor bug
                        assert info.valueOf(ArmyOption.XA_STATES) == XaStates.IDLE;  // fail ,executor bug

                        assert info.nonNullOf(ArmyOption.XA_FLAGS) == flags;  // fail ,executor bug

                        TRANSACTION_INFO.set(this, info);
                    }).onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<Integer> prepare(Xid xid) {
        return prepare(xid, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<Integer> prepare(final @Nullable Xid xid, Function<ArmyOption<?>, ?> optionFunc) {
        final Mono<Integer> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(ArmyOption.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(ArmyOption.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportPrepareCommand(infoXid, states)); // use infoXid
        } else if ((lastInfo.nonNullOf(ArmyOption.XA_FLAGS) & RmSession.TM_FAIL) != 0 || ROLLBACK_ONLY.get(this) != 0) {
            mono = Mono.error(_Exceptions.xaTransactionRollbackOnly(infoXid));
        } else {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).prepare(infoXid, optionFunc) // use infoXid
                    .doOnSuccess(flag -> TRANSACTION_INFO.set(this, null))
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid) {
        return commit(xid, TM_NO_FLAGS, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid, int flags) {
        return commit(xid, flags, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> commit(final @Nullable Xid xid, final int flags, Function<ArmyOption<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((flags & TM_ONE_PHASE) == 0) {   // tow phase commit
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).commit(xid, flags, optionFunc) // use xid
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(ArmyOption.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(ArmyOption.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportCommitCommand(infoXid, states)); // use infoXid
        } else if ((lastInfo.nonNullOf(ArmyOption.XA_FLAGS) & RmSession.TM_FAIL) != 0 || ROLLBACK_ONLY.get(this) != 0) {
            mono = Mono.error(_Exceptions.xaTransactionRollbackOnly(infoXid));
        } else {   // one phase commit
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).commit(infoXid, flags, optionFunc) // use infoXid
                    .doOnSuccess(flag -> TRANSACTION_INFO.set(this, null)) // clear transactionInfo for one phase commit
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(Xid xid) {
        return rollback(xid, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(final @Nullable Xid xid, Function<ArmyOption<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;


        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(ArmyOption.XID)).equals(xid)) {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).rollback(xid, optionFunc) // use xid
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        } else if ((states = lastInfo.nonNullOf(ArmyOption.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportRollbackCommand(xid, states)); // use xid
        } else {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).rollback(infoXid, optionFunc) // use infoXid
                    .doOnSuccess(flag -> TRANSACTION_INFO.set(this, null)) // clear transactionInfo for one phase rollback
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> forget(Xid xid) {
        return forget(xid, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> forget(final @Nullable Xid xid, Function<ArmyOption<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if (isSupportForget()) {
            mono = ((ReactiveRmStmtExecutor) this.stmtExecutor).forget(xid, optionFunc)
                    .thenReturn(this);
        } else {
            mono = Mono.error(_Exceptions.xaDontSupportForget(this));
        }
        return mono;
    }

    @Override
    public final Flux<Optional<Xid>> recover(int flags) {
        return recover(flags, ArmyOption.EMPTY_OPTION_FUNC);
    }

    @Override
    public final Flux<Optional<Xid>> recover(final int flags, Function<ArmyOption<?>, ?> optionFunc) {
        if (isClosed()) {
            return Flux.error(_Exceptions.sessionClosed(this));
        }
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).recover(flags, optionFunc);
    }

    @Override
    public final boolean isSupportForget() {
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).isSupportForget();
    }

    @Override
    public final int startSupportFlags() {
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).startSupportFlags();
    }

    @Override
    public final int endSupportFlags() {
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).endSupportFlags();
    }

    @Override
    public final int commitSupportFlags() {
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).commitSupportFlags();
    }

    @Override
    public final int recoverSupportFlags() {
        return ((ReactiveRmStmtExecutor) this.stmtExecutor).recoverSupportFlags();
    }

    @Override
    public final boolean isSameRm(final XaTransactionSupportSpec s) throws SessionException {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final boolean match;
        if (s == this) {
            match = true;
        } else if (s instanceof ArmyReactiveRmSession) {
            match = ((ReactiveRmStmtExecutor) this.stmtExecutor).isSameRm((ReactiveRmStmtExecutor) ((ArmyReactiveRmSession) s).stmtExecutor);
        } else {
            match = false;
        }
        return match;
    }


    @Override
    protected final Logger getLogger() {
        return LOG;
    }

    @Nullable
    @Override
    protected final TransactionInfo obtainTransactionInfo() {
        return this.transactionInfo;
    }

    @Override
    protected final void rollbackOnlyOnError(ChildUpdateException cause) {
        ROLLBACK_ONLY.compareAndSet(this, 0, 1);
    }


    @Override
    final ReactiveStmtOption defaultOption() {
        throw new UnsupportedOperationException();
    }


    private static final class OpenDriverSpiSession extends ArmyReactiveRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmyReactiveSessionFactory.ArmyRmBuilder builder) {
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
