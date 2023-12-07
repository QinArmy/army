package io.army.sync;

import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link SyncLocalSession}
 *
 * @see ArmySyncSessionFactory
 */
class ArmySyncLocalSession extends ArmySyncSession implements SyncLocalSession {

    /**
     * @see ArmySyncSessionFactory.LocalBuilder#createSession(String, boolean, Function)
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
        if (this.rollbackOnly) {
            return true;
        }
        final TransactionInfo info = this.transactionInfo;
        return info != null && info.isRollbackOnly();
    }

    @Override
    public final void markRollbackOnly() {
        if (this.rollbackOnly) {
            return;
        }
        this.rollbackOnly = true;
        final TransactionInfo info = this.transactionInfo;
        if (info != null) {
            this.transactionInfo = wrapRollbackOnly(info);
        }
    }

    @Override
    public final TransactionInfo startTransaction() {
        return startTransaction(TransactionOption.option(), HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public final TransactionInfo startTransaction(TransactionOption option) {
        return startTransaction(option, HandleMode.ERROR_IF_EXISTS);
    }


    @Override
    public final TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final boolean startPseudo;
        startPseudo = option.isolation() == Isolation.PSEUDO;
        if (startPseudo) {
            if (!this.readonly) {
                throw _Exceptions.writeSessionPseudoTransaction(this);
            } else if (!option.isReadOnly()) {
                throw _Exceptions.pseudoWriteError(this, option);
            }
        }

        final TransactionInfo existTransaction = this.transactionInfo;
        if (existTransaction != null) {
            switch (mode) {
                case ERROR_IF_EXISTS:
                    throw _Exceptions.existsTransaction(this);
                case COMMIT_IF_EXISTS: {
                    if (isRollbackOnly()) {
                        throw _Exceptions.rollbackOnlyTransaction(this);
                    } else if (existTransaction.isolation() == Isolation.PSEUDO) {
                        this.transactionInfo = null; // clear pseudo transaction
                    }
                }
                break;
                case ROLLBACK_IF_EXISTS: {
                    if (existTransaction.isolation() == Isolation.PSEUDO) {
                        this.transactionInfo = null; // clear pseudo transaction
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);

            } //  switch

        } //    if (existTransaction != null)

        final TransactionInfo info;
        if (startPseudo) {
            info = TransactionInfo.info(false, Isolation.PSEUDO, true, wrapStartMillis(null, option));
        } else {
            info = ((SyncLocalStmtExecutor) this.stmtExecutor).startTransaction(option, mode);

            Objects.requireNonNull(info); // fail,executor bug

            assert info.inTransaction(); // fail,executor bug
            assert info.isReadOnly() == option.isReadOnly();
            assert info.isolation().equals(option.isolation());

            final Integer timeoutMillis;
            timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
            assert timeoutMillis == null || timeoutMillis <= 0 || info.valueOf(Option.START_MILLIS) != null;
        }

        if (this.transactionInfo != null) {
            throw new ConcurrentModificationException();
        }
        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
    }

    @Override
    public final void commit() {
        this.commit(Option.EMPTY_FUNC);
    }

    @Override
    public final TransactionInfo commit(final Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (isRollbackOnly()) {
            throw _Exceptions.rollbackOnlyTransaction(this);
        }

        final TransactionInfo existsTransaction = this.transactionInfo;

        final TransactionInfo info;
        if (existsTransaction != null && existsTransaction.isolation() == Isolation.PSEUDO) {
            info = null; // clear pseudo transaction
        } else {
            info = ((SyncLocalStmtExecutor) this.stmtExecutor).commit(optionFunc);
            assertTransactionInfoAfterEnd(optionFunc, info);
        }
        this.transactionInfo = info;
        return info;
    }


    @Override
    public final void commitIfExists() {
        commitIfExists(Option.EMPTY_FUNC);
    }

    @Nullable
    @Override
    public final TransactionInfo commitIfExists(final Function<Option<?>, ?> optionFunc) {
        final TransactionInfo info = this.transactionInfo;
        if (!(info != null && info.isolation() == Isolation.PSEUDO) || inTransaction()) {
            return commit(optionFunc);
        }
        return null;
    }

    @Override
    public final void rollback() {
        rollback(Option.EMPTY_FUNC);
    }

    @Override
    public final TransactionInfo rollback(final Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        final TransactionInfo existsTransaction = this.transactionInfo;

        final TransactionInfo info;
        if (existsTransaction != null && existsTransaction.isolation() == Isolation.PSEUDO) {
            info = null; // clear pseudo transaction
        } else {
            info = ((SyncLocalStmtExecutor) this.stmtExecutor).rollback(optionFunc);
            assertTransactionInfoAfterEnd(optionFunc, info);
        }

        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
    }

    @Override
    public final void rollbackIfExists() {
        rollbackIfExists(Option.EMPTY_FUNC);
    }

    @Nullable
    @Override
    public final TransactionInfo rollbackIfExists(final Function<Option<?>, ?> optionFunc) {
        final TransactionInfo info = this.transactionInfo;
        if (!(info != null && info.isolation() == Isolation.PSEUDO) || inTransaction()) {
            return rollback(optionFunc);
        }
        return null;
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
        markRollbackOnly();
    }

    /*-------------------below private methods-------------------*/

    private void assertTransactionInfoAfterEnd(final Function<Option<?>, ?> optionFunc, final @Nullable TransactionInfo info) {

        switch (this.factory.serverDatabase) {
            case MySQL: {
                if (optionFunc == Option.EMPTY_FUNC) {
                    assert info == null; // fail,executor bug
                } else if (Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN))) {
                    assert info != null && info.inTransaction(); // fail,executor bug
                } else if (Boolean.TRUE.equals(optionFunc.apply(Option.RELEASE))) {
                    assert info == null; // fail,executor bug
                    releaseSession();
                }
            }
            break;
            case PostgreSQL: {
                if (optionFunc == Option.EMPTY_FUNC) {
                    assert info == null; // fail,executor bug
                } else {
                    assert !Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN))
                            || (info != null && info.inTransaction()); // fail,executor bug
                }
            }
            break;
            default:
                assert info == null; // fail,executor bug
        }

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
