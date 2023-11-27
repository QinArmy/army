package io.army.sync;

import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link SyncLocalSession}
 *
 * @see ArmySyncSessionFactory
 */
class ArmySyncLocalSession extends ArmySyncSession implements SyncLocalSession {

    /**
     * @see ArmySyncSessionFactory.LocalBuilder#createSession(String, boolean)
     */
    static ArmySyncLocalSession create(ArmySyncSessionFactory.LocalBuilder builder) {
        final ArmySyncLocalSession session;
        if (builder.inOpenDriverSpi()) {
            session = new OpenDriverSpiSession(builder);
        } else {
            session = new ArmySyncLocalSession(builder);
        }
        return session;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncLocalSession.class);

    private TransactionInfo transactionInfo;

    private boolean rollbackOnly;

    /**
     * private constructor
     *
     * @see ArmySyncLocalSession#create(ArmySyncSessionFactory.LocalBuilder)
     */
    private ArmySyncLocalSession(final ArmySyncSessionFactory.LocalBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncLocalStmtExecutor;
    }


    @Override
    public final boolean isRollbackOnly() {
        return this.rollbackOnly;
    }

    @Override
    public final void markRollbackOnly() {
        this.rollbackOnly = true;
    }


    @Override
    public final TransactionInfo pseudoTransaction(final TransactionOption option, final HandleMode mode) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (!this.readonly) {
            throw _Exceptions.writeSessionPseudoTransaction(this);
        }
        final Isolation isolation;
        isolation = option.isolation();
        if (isolation == null) {
            throw new IllegalArgumentException();
        }


        final TransactionInfo info = this.transactionInfo;
        if (info != null) {
            switch (mode) {
                case ERROR_IF_EXISTS:
                    throw _Exceptions.existsTransaction(this);
                case COMMIT_IF_EXISTS:
                    commit();
                    break;
                case ROLLBACK_IF_EXISTS:
                    rollback();
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
        }

        final TransactionInfo pseudoInfo;
        pseudoInfo = TransactionInfo.info(false, isolation, option.isReadOnly(), wrapStartMillis(option));

        assert this.transactionInfo == null;
        this.transactionInfo = pseudoInfo;
        this.rollbackOnly = false;
        return pseudoInfo;

    }


    @Override
    public final TransactionInfo startTransaction() {
        return this.startTransaction(TransactionOption.option(null, false), HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final TransactionInfo startTransaction(TransactionOption option) {
        return this.startTransaction(option, HandleMode.ERROR_IF_EXISTS);
    }


    @Override
    public final TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final TransactionInfo existTransaction = this.transactionInfo;
        final boolean pseudoTransaction;

        if (existTransaction == null) {
            pseudoTransaction = false;
        } else switch (mode) {
            case ERROR_IF_EXISTS:
                throw _Exceptions.existsTransaction(this);
            case COMMIT_IF_EXISTS: {
                if (isRollbackOnly()) {
                    throw _Exceptions.rollbackOnlyTransaction(this);
                } else if (existTransaction.inTransaction()) {
                    pseudoTransaction = false;
                } else {
                    pseudoTransaction = true;
                    commit();
                }
            }
            break;
            case ROLLBACK_IF_EXISTS: {
                if (existTransaction.inTransaction()) {
                    pseudoTransaction = false;
                } else {
                    pseudoTransaction = true;
                    rollback();
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);

        } // else switch

        final HandleMode actualMode;
        if (pseudoTransaction) {
            assert this.transactionInfo == null;
            actualMode = HandleMode.ERROR_IF_EXISTS;
        } else {
            actualMode = mode;
        }

        final TransactionInfo info;
        info = ((SyncLocalStmtExecutor) this.stmtExecutor).startTransaction(option, actualMode);

        Objects.requireNonNull(info); // fail,executor bug

        assert info.inTransaction(); // fail,executor bug
        assert info.isReadOnly() == option.isReadOnly();
        assert info.isolation().equals(option.isolation());

        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null && timeoutMillis > 0) {
            assert info.valueOf(Option.START_MILLIS) != null;
        }

        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
    }

    @Override
    public final void commit() {
        this.commit(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (isRollbackOnly()) {
            throw _Exceptions.rollbackOnlyTransaction(this);
        }

        final TransactionInfo info;
        info = ((SyncLocalStmtExecutor) this.stmtExecutor).commit(optionFunc);

        if (optionFunc != Option.EMPTY_OPTION_FUNC
                && Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN))) {
            assert info != null && info.inTransaction(); // fail,executor bug
        } else {
            assert info == null; // fail,executor bug
        }
        this.transactionInfo = info;
        return info;
    }

    @Override
    public final void rollback() {
        this.rollback(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final TransactionInfo info;
        info = ((SyncLocalStmtExecutor) this.stmtExecutor).rollback(optionFunc);

        if (optionFunc != Option.EMPTY_OPTION_FUNC
                && Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN))) {
            assert info != null && info.inTransaction();
        } else {
            assert info == null;
        }
        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
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
    protected final void rollbackOnlyOnError(ChildUpdateException cause) {
        this.rollbackOnly = true;
    }

    /*-------------------below inner class -------------------*/

    private static final class OpenDriverSpiSession extends ArmySyncLocalSession implements DriverSpiHolder {

        /**
         * @see ArmySyncLocalSession#create(ArmySyncSessionFactory.LocalBuilder)
         */
        private OpenDriverSpiSession(ArmySyncSessionFactory.LocalBuilder builder) {
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
