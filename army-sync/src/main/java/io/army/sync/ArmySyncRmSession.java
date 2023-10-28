package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>This class is a implementation of {@link SyncRmSession}
 *
 * @see ArmySyncLocalSessionFactory
 * @since 1.0
 */
class ArmySyncRmSession extends ArmySyncSession implements SyncRmSession {

    static ArmySyncRmSession create(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        final ArmySyncRmSession session;
        if (builder.inOpenDriverSpi()) {
            session = new OpenDriverSpiSession(builder);
        } else {
            session = new ArmySyncRmSession(builder);
        }
        return session;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncRmSession.class);

    private TransactionInfo transactionInfo;

    /**
     * private constructor
     */
    private ArmySyncRmSession(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncRmStmtExecutor;
    }

    @Override
    public final SyncRmSessionFactory sessionFactory() {
        return (SyncRmSessionFactory) this.factory;
    }


    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (((ArmySyncLocalSessionFactory) this.factory).jdbcDriver || !(this instanceof OpenDriverSpiSession)) {
            // JDBC don't support to get the state from database client protocol.
            final TransactionInfo info = this.transactionInfo;
            return info != null && info.inTransaction();
        }
        // due to session open driver spi to application developer, TransactionInfo probably error.
        return this.stmtExecutor.inTransaction();
    }

    @Override
    public final boolean hasTransactionInfo() {
        return this.transactionInfo != null;
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
    public final TransactionInfo start(Xid xid, int flags, TransactionOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (this.transactionInfo != null) {
            throw _Exceptions.existsTransaction(this);
        }

        final TransactionInfo info;
        info = ((SyncRmStmtExecutor) this.stmtExecutor).start(xid, flags, option);

        Objects.requireNonNull(info);
        assert info.valueOf(Option.XID) != null;

        this.transactionInfo = info;
        this.clearRollbackOnly();

        TransactionInfo.validate(info); // finally validate
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
    public final TransactionInfo end(final @Nullable Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // no bug,never here
            throw new NullPointerException();
        }

        final TransactionInfo lastInfo = this.transactionInfo, info;
        final XaStates states;
        if (lastInfo == null) {
            throw _Exceptions.noTransaction(this);
        } else if (!xid.equals(lastInfo.valueOf(Option.XID))) {
            throw _Exceptions.xaNonCurrentTransaction(xid);
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.ACTIVE) {
            throw _Exceptions.xaTransactionDontSupportEndCommand(xid, states);
        }

        info = ((SyncRmStmtExecutor) this.stmtExecutor).end(xid, flags, optionFunc);

        Objects.requireNonNull(info);
        assert info != lastInfo;
        assert info.valueOf(Option.XID) != null;

        this.transactionInfo = info;

        TransactionInfo.validate(info); // finally validate
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
        } else if (xid == null) {
            // no bug,never here
            throw new NullPointerException();
        }

        return 0;
    }

    @Override
    public final SyncRmSession commit(Xid xid) {
        return null;
    }

    @Override
    public final SyncRmSession commit(Xid xid, int flags) {
        return null;
    }

    @Override
    public final SyncRmSession commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final SyncRmSession rollback(Xid xid) {
        return null;
    }

    @Override
    public final SyncRmSession rollback(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final SyncRmSession forget(Xid xid) {
        return null;
    }

    @Override
    public final SyncRmSession forget(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final List<Xid> recover(int flags) {
        return null;
    }

    @Override
    public final List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public final Stream<Xid> recoverStream(int flags) {
        return null;
    }

    @Override
    public final Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc) {
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
    public final int recoverSupportFlags() {
        return 0;
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

    /*-------------------below package template methods -------------------*/

    @Override
    final TransactionInfo obtainTransactionInfo() {
        return this.transactionInfo;
    }


    private static final class OpenDriverSpiSession extends ArmySyncRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
            super(builder);
        }

        @Override
        public <T> T getDriverSpi(Class<T> spiClass) {
            return ((DriverSpiHolder) this.stmtExecutor).getDriverSpi(spiClass);
        }


    } // OpenDriverSpiSession


}
