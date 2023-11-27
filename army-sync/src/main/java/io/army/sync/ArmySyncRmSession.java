package io.army.sync;

import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>This class is a implementation of {@link SyncRmSession}
 *
 * @see ArmySyncRmSessionFactory
 * @since 1.0
 */
class ArmySyncRmSession extends ArmySyncSession implements SyncRmSession {

    static ArmySyncRmSession create(ArmySyncRmSessionFactory.RmSessionBuilder builder) {
        final ArmySyncRmSession session;
        if (builder.inOpenDriverSpi()) {
            session = new OpenDriverSpiSession(builder);
        } else {
            session = new ArmySyncRmSession(builder);
        }
        return session;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncRmSession.class);

    private static final ConcurrentMap<Xid, TransactionInfo> ROLLBACK_ONLY_MAP = _Collections.concurrentHashMap();

    private TransactionInfo transactionInfo;

    private boolean rollbackOnly;

    /**
     * private constructor
     */
    private ArmySyncRmSession(ArmySyncRmSessionFactory.RmSessionBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncRmStmtExecutor;
    }

    @Override
    public final SyncRmSessionFactory sessionFactory() {
        return (SyncRmSessionFactory) this.factory;
    }


    @Override
    public final boolean hasTransactionInfo() {
        return this.transactionInfo != null;
    }

    @Override
    public boolean isRollbackOnly() {
        if (this.rollbackOnly) {
            return true;
        }
        final TransactionInfo info = this.transactionInfo;
        final Integer flags;
        return info != null
                && info.valueOf(Option.XA_STATES) == XaStates.IDLE
                && (flags = info.valueOf(Option.XA_FLAGS)) != null
                && (flags & TM_FAIL) != 0;
    }

    @Override
    public final TransactionInfo start(Xid xid) {
        return this.start(xid, TM_NO_FLAGS, TransactionOption.option(null, false));
    }

    @Override
    public final TransactionInfo start(Xid xid, int flags) {
        return this.start(xid, flags, TransactionOption.option(null, false));
    }

    @Override
    public final TransactionInfo start(final @Nullable Xid xid, final int flags, TransactionOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (this.transactionInfo != null) {
            throw _Exceptions.existsTransaction(this);
        } else if (xid == null) {
            throw _Exceptions.xidIsNull();
        }

        final TransactionInfo info;
        info = ((SyncRmStmtExecutor) this.stmtExecutor).start(xid, flags, option);

        Objects.requireNonNull(info);   // fail ,executor bug
        assert info.inTransaction();  // fail ,executor bug
        assert xid.equals(info.valueOf(Option.XID));  // fail ,executor bug
        assert info.valueOf(Option.XA_STATES) == XaStates.ACTIVE;  // fail ,executor bug

        assert info.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
    }

    @Override
    public final TransactionInfo end(Xid xid) {
        return this.end(xid, TM_NO_FLAGS, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final TransactionInfo end(Xid xid, int flags) {
        return this.end(xid, flags, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final TransactionInfo end(final @Nullable Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // no bug,never here
            throw new NullPointerException();
        }

        final TransactionInfo lastInfo = this.transactionInfo, info;
        final XaStates states;
        final Xid infoXid;

        if (lastInfo == null) {
            throw _Exceptions.noTransaction(this);
        } else if (!(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            throw _Exceptions.xaNonCurrentTransaction(xid); // use xid
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.ACTIVE) {
            throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, states); // use infoXid
        }

        info = ((SyncRmStmtExecutor) this.stmtExecutor).end(infoXid, flags, optionFunc); // use infoXid

        Objects.requireNonNull(info); // fail ,executor bug
        assert info != lastInfo; // fail ,executor bug
        assert info.inTransaction(); // fail ,executor bug
        assert infoXid.equals(info.valueOf(Option.XID)); // use infoXid ; fail ,executor bug

        assert info.valueOf(Option.XA_STATES) == XaStates.IDLE;  // fail ,executor bug
        assert info.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

        this.transactionInfo = info;

        return info;
    }

    @Override
    public final int prepare(Xid xid) {
        return this.prepare(xid, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final int prepare(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final TransactionInfo lastInfo = this.transactionInfo;
        final XaStates states;
        final Xid infoXid;

        if (lastInfo == null) {
            throw _Exceptions.noTransaction(this);
        } else if (!(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            throw _Exceptions.xaNonCurrentTransaction(xid); // use xid
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            throw _Exceptions.xaStatesDontSupportPrepareCommand(infoXid, states); // use infoXid
        } else if ((lastInfo.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0) {
            throw _Exceptions.xaTransactionRollbackOnly(infoXid);
        }

        final int flags;
        flags = ((SyncRmStmtExecutor) this.stmtExecutor).prepare(infoXid, optionFunc); // use infoXid

        final boolean rollbackOnly = this.rollbackOnly;

        this.transactionInfo = null;
        this.rollbackOnly = false;
        if (rollbackOnly || (lastInfo.nonNullOf(Option.XA_FLAGS) & TM_FAIL) != 0) { // rollback only

            if (ROLLBACK_ONLY_MAP.putIfAbsent(infoXid, lastInfo) != null) {
                // no bug ,never here
                String m = String.format("%s duplication prepare", infoXid);
                throw new SessionException(m);
            }
        }
        return flags;
    }

    @Override
    public final void commit(Xid xid) {
        this.commit(xid, TM_NO_FLAGS, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final void commit(Xid xid, int flags) {
        this.commit(xid, flags, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final void commit(final @Nullable Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final TransactionInfo info;

        if ((flags & TM_ONE_PHASE) != 0) {
            info = this.transactionInfo;
            final XaStates states;
            final Xid infoXid;

            if (info == null) {
                throw _Exceptions.noTransaction(this);
            } else if (!(infoXid = info.nonNullOf(Option.XID)).equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // use xid
            } else if ((states = info.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
                throw _Exceptions.xaStatesDontSupportCommitCommand(infoXid, states); // use infoXid
            } else if (this.rollbackOnly || (info.nonNullOf(Option.XA_FLAGS) & TM_FAIL) != 0) {
                // rollback only
                throw _Exceptions.xaTransactionRollbackOnly(infoXid);
            }

            ((SyncRmStmtExecutor) this.stmtExecutor).commit(infoXid, flags, optionFunc); // use infoXid

            this.transactionInfo = null;
        } else if (xid == null) {
            // application developer no bug,never here
            throw new NullPointerException();
        } else if ((info = ROLLBACK_ONLY_MAP.get(xid)) != null) {
            // rollback only
            throw _Exceptions.xaTransactionRollbackOnly(info.nonNullOf(Option.XID));
        } else {

            ((SyncRmStmtExecutor) this.stmtExecutor).commit(xid, flags, optionFunc);

            ROLLBACK_ONLY_MAP.remove(xid);
        }


    }

    @Override
    public final void rollback(Xid xid) {
        this.rollback(xid, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final void rollback(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // application developer no bug,never here
            throw new NullPointerException();
        }

        ((SyncRmStmtExecutor) this.stmtExecutor).rollback(xid, optionFunc);
        ROLLBACK_ONLY_MAP.remove(xid);

    }

    @Override
    public final void forget(Xid xid) {
        this.forget(xid, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final void forget(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (!isSupportForget()) {
            throw _Exceptions.xaDontSupportForget(this);
        } else if (xid == null) {
            // application developer no bug,never here
            throw new NullPointerException();
        }

        ((SyncRmStmtExecutor) this.stmtExecutor).forget(xid, optionFunc);

    }

    @Override
    public final List<Xid> recoverList(int flags) {
        return this.recoverList(flags, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final List<Xid> recoverList(int flags, Function<Option<?>, ?> optionFunc) {
        return recover(flags, optionFunc, StreamOption.defaultOption())
                .collect(Collectors.toCollection(_Collections::arrayList));
    }

    @Override
    public final Stream<Xid> recover(int flags) {
        return this.recover(flags, Option.EMPTY_OPTION_FUNC, StreamOption.defaultOption());
    }

    @Override
    public final Stream<Xid> recover(int flags, Function<Option<?>, ?> optionFunc, StreamOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        return ((SyncRmStmtExecutor) this.stmtExecutor).recover(flags, optionFunc, option);
    }

    @Override
    public final boolean isSupportForget() {
        return ((SyncRmStmtExecutor) this.stmtExecutor).isSupportForget();
    }

    @Override
    public final int startSupportFlags() {
        return ((SyncRmStmtExecutor) this.stmtExecutor).startSupportFlags();
    }

    @Override
    public final int endSupportFlags() {
        return ((SyncRmStmtExecutor) this.stmtExecutor).endSupportFlags();
    }

    @Override
    public final int commitSupportFlags() {
        return ((SyncRmStmtExecutor) this.stmtExecutor).commitSupportFlags();
    }

    @Override
    public final int recoverSupportFlags() {
        return ((SyncRmStmtExecutor) this.stmtExecutor).recoverSupportFlags();
    }

    @Override
    public final boolean isSameRm(final XaTransactionSupportSpec s) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final boolean match;
        if (s == this) {
            match = true;
        } else if (s instanceof ArmySyncRmSession) {
            match = ((SyncRmStmtExecutor) this.stmtExecutor).isSameRm((SyncRmStmtExecutor) ((ArmySyncRmSession) s).stmtExecutor);
        } else {
            match = false;
        }
        return match;
    }

    /*-------------------below protected template methods -------------------*/

    @Override
    protected final Logger getLogger() {
        return LOG;
    }

    @Override
    protected final TransactionInfo obtainTransactionInfo() {
        return this.transactionInfo;
    }


    @Override
    protected void rollbackOnlyOnError(ChildUpdateException cause) {
        this.rollbackOnly = true;
    }

    private static final class OpenDriverSpiSession extends ArmySyncRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmySyncRmSessionFactory.RmSessionBuilder builder) {
            super(builder);
        }

        @Override
        public boolean isDriverAssignableTo(Class<?> spiClass) {
            return this.stmtExecutor.isDriverAssignableTo(spiClass);
        }

        @Override
        public <T> T getDriverSpi(Class<T> spiClass) {
            return this.stmtExecutor.getDriverSpi(spiClass);
        }


    } // OpenDriverSpiSession


}
