package io.army.reactive;

import io.army.reactive.executor.ReactiveRmExecutor;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.Objects;
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
     * @see ArmyReactiveSessionFactory.RmBuilder#createSession(String, boolean, Function)
     */
    static ArmyReactiveRmSession create(ArmyReactiveSessionFactory.RmBuilder builder) {
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
    private ArmyReactiveRmSession(ArmyReactiveSessionFactory.RmBuilder builder) {
        super(builder);
    }


    @Override
    public final boolean isRollbackOnly() {
        if (this.rollbackOnly != 0) {
            return true;
        }
        final TransactionInfo info = this.transactionInfo;
        return info != null
                && info.valueOf(Option.XA_STATES) == XaStates.IDLE
                && (info.nonNullOf(Option.XA_FLAGS) & TM_FAIL) != 0;
    }

    @Override
    public final void markRollbackOnly() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final TransactionInfo info = this.transactionInfo;
        if (!ROLLBACK_ONLY.compareAndSet(this, 0, 1) || info == null) {
            return;
        }

        if (!TRANSACTION_INFO.compareAndSet(this, info, TransactionInfo.replaceOption(info, Option.ROLLBACK_ONLY, Boolean.TRUE))) {
            throw new ConcurrentModificationException();
        }

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
        return releaseSavePoint(savepoint, Option.EMPTY_FUNC);
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
        return rollbackToSavePoint(savepoint, Option.EMPTY_FUNC);
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
        return start(xid, TM_NO_FLAGS, TransactionOption.option());
    }

    @Override
    public final Mono<TransactionInfo> start(Xid xid, int flags) {
        return start(xid, flags, TransactionOption.option());
    }

    @Override
    public final Mono<TransactionInfo> start(final @Nullable Xid xid, final int flags, final TransactionOption option) {
        final Mono<TransactionInfo> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (this.transactionInfo != null) {
            mono = Mono.error(_Exceptions.existsTransaction(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if (option.isolation() == Isolation.PSEUDO) { // start pseudo transaction
            if (!this.readonly) {
                mono = Mono.error(_Exceptions.writeSessionPseudoTransaction(this));
            } else if (!option.isReadOnly()) {
                mono = Mono.error(_Exceptions.pseudoWriteError(this, option));
            } else {
                final TransactionInfo info;
                info = TransactionInfo.pseudoStart(xid, flags, option);
                TRANSACTION_INFO.set(this, info);
                ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                mono = Mono.just(info);
            }
        } else {  // start XA transaction
            mono = ((ReactiveRmExecutor) this.stmtExecutor).start(xid, flags, option)
                    .doOnSuccess(info -> {
                        assert info.inTransaction();  // fail ,executor bug
                        assert xid.equals(info.valueOf(Option.XID));  // fail ,executor bug
                        assert info.valueOf(Option.XA_STATES) == XaStates.ACTIVE;  // fail ,executor bug
                        assert info.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

                        assert info.valueOf(Option.START_MILLIS) != null;  // fail ,executor bug
                        assert Objects.equals(info.valueOf(Option.TIMEOUT_MILLIS), option.valueOf(Option.TIMEOUT_MILLIS));

                        TRANSACTION_INFO.set(this, info);
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                    }).onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid) {
        return end(xid, TM_NO_FLAGS, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(Xid xid, int flags) {
        return end(xid, flags, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> end(final @Nullable Xid xid, final int flags, final Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.ACTIVE) {
            mono = Mono.error(_Exceptions.xaTransactionDontSupportEndCommand(infoXid, states)); // use infoXid
        } else if (lastInfo.isolation() == Isolation.PSEUDO) { // start pseudo transaction
            TRANSACTION_INFO.set(this, TransactionInfo.pseudoEnd(lastInfo, flags));
            mono = Mono.just(this);
        } else {
            mono = ((ReactiveRmExecutor) this.stmtExecutor).end(infoXid, flags, optionFunc) // use infoXid
                    .doOnSuccess(info -> {
                        assert info.inTransaction();  // fail ,executor bug
                        assert infoXid.equals(info.valueOf(Option.XID));  // fail ,executor bug
                        assert info.valueOf(Option.XA_STATES) == XaStates.IDLE;  // fail ,executor bug
                        assert info.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

                        assert lastInfo.nonNullOf(Option.START_MILLIS).equals(info.valueOf(Option.START_MILLIS));  // fail ,executor bug
                        assert Objects.equals(info.valueOf(Option.TIMEOUT_MILLIS), lastInfo.valueOf(Option.TIMEOUT_MILLIS));

                        TRANSACTION_INFO.set(this, info);
                    }).onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<Integer> prepare(Xid xid) {
        return prepare(xid, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<Integer> prepare(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        final Mono<Integer> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportPrepareCommand(infoXid, states)); // use infoXid
        } else if ((lastInfo.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0 || this.rollbackOnly != 0) {
            mono = Mono.error(_Exceptions.xaTransactionRollbackOnly(infoXid));
        } else if (lastInfo.isolation() == Isolation.PSEUDO) { // commit pseudo transaction
            TRANSACTION_INFO.set(this, null);
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just((int) XA_RDONLY);
        } else {
            mono = ((ReactiveRmExecutor) this.stmtExecutor).prepare(infoXid, optionFunc) // use infoXid
                    .doOnSuccess(flag -> TRANSACTION_INFO.set(this, null))
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid) {
        return commit(xid, TM_NO_FLAGS, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> commit(Xid xid, int flags) {
        return commit(xid, flags, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> commit(final @Nullable Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((flags & TM_ONE_PHASE) == 0) {   // tow phase commit
            mono = ((ReactiveRmExecutor) this.stmtExecutor).commit(xid, flags, optionFunc) // use xid
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            mono = Mono.error(_Exceptions.xaNonCurrentTransaction(xid));
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportCommitCommand(infoXid, states)); // use infoXid
        } else if ((lastInfo.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0 || this.rollbackOnly != 0) {
            mono = Mono.error(_Exceptions.xaTransactionRollbackOnly(infoXid));
        } else if (lastInfo.isolation() == Isolation.PSEUDO) { // one phase commit pseudo transaction
            TRANSACTION_INFO.set(this, null);
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just(this);
        } else {   // one phase commit
            mono = ((ReactiveRmExecutor) this.stmtExecutor).commit(infoXid, flags, optionFunc) // use infoXid
                    .doOnSuccess(v -> {
                        TRANSACTION_INFO.set(this, null); // clear transactionInfo for one phase commit
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                    })
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(Xid xid) {
        return rollback(xid, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> rollback(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;

        final TransactionInfo lastInfo;
        final XaStates states;
        final Xid infoXid;


        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if ((lastInfo = this.transactionInfo) == null
                || !(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {  // two phase rollback
            mono = ((ReactiveRmExecutor) this.stmtExecutor).rollback(xid, optionFunc) // use xid
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            mono = Mono.error(_Exceptions.xaStatesDontSupportRollbackCommand(infoXid, states)); // use infoXid
        } else if (lastInfo.isolation() == Isolation.PSEUDO) { // one phase rollback pseudo transaction
            TRANSACTION_INFO.set(this, null); // clear  for one phase rollback
            ROLLBACK_ONLY.compareAndSet(this, 1, 0);
            mono = Mono.just(this);
        } else {  // one phase rollback
            mono = ((ReactiveRmExecutor) this.stmtExecutor).rollback(infoXid, optionFunc) // use infoXid
                    .doOnSuccess(v -> {
                        TRANSACTION_INFO.set(this, null); // clear  for one phase rollback
                        ROLLBACK_ONLY.compareAndSet(this, 1, 0);
                    })
                    .onErrorMap(_ArmySession::wrapIfNeed)
                    .thenReturn(this);
        }
        return mono;
    }

    @Override
    public final Mono<ReactiveRmSession> forget(Xid xid) {
        return forget(xid, Option.EMPTY_FUNC);
    }

    @Override
    public final Mono<ReactiveRmSession> forget(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        final Mono<ReactiveRmSession> mono;
        if (isClosed()) {
            mono = Mono.error(_Exceptions.sessionClosed(this));
        } else if (xid == null) {
            mono = Mono.error(_Exceptions.xidIsNull());
        } else if (isSupportForget()) {
            mono = ((ReactiveRmExecutor) this.stmtExecutor).forget(xid, optionFunc)
                    .thenReturn(this);
        } else {
            mono = Mono.error(_Exceptions.xaDontSupportForget(this));
        }
        return mono;
    }

    @Override
    public final Flux<Optional<Xid>> recover(int flags) {
        return recover(flags, Option.EMPTY_FUNC);
    }

    @Override
    public final Flux<Optional<Xid>> recover(final int flags, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            return Flux.error(_Exceptions.sessionClosed(this));
        }
        return ((ReactiveRmExecutor) this.stmtExecutor).recover(flags, optionFunc);
    }

    @Override
    public final boolean isSupportForget() {
        return ((ReactiveRmExecutor) this.stmtExecutor).isSupportForget();
    }

    @Override
    public final int startSupportFlags() {
        return ((ReactiveRmExecutor) this.stmtExecutor).startSupportFlags();
    }

    @Override
    public final int endSupportFlags() {
        return ((ReactiveRmExecutor) this.stmtExecutor).endSupportFlags();
    }

    @Override
    public final int commitSupportFlags() {
        return ((ReactiveRmExecutor) this.stmtExecutor).commitSupportFlags();
    }

    @Override
    public final int recoverSupportFlags() {
        return ((ReactiveRmExecutor) this.stmtExecutor).recoverSupportFlags();
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
            match = ((ReactiveRmExecutor) this.stmtExecutor).isSameRm((ReactiveRmExecutor) ((ArmyReactiveRmSession) s).stmtExecutor);
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
        markRollbackOnly();
    }

    private static final class OpenDriverSpiSession extends ArmyReactiveRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmyReactiveSessionFactory.RmBuilder builder) {
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
