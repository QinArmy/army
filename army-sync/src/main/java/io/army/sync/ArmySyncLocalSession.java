package io.army.sync;

import io.army.session.HandleMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.TransactionOption;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link SyncLocalSession}
 *
 * @see ArmySyncLocalSessionFactory
 */
final class ArmySyncLocalSession extends ArmySyncSession implements SyncLocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(io.army.sync.ArmySyncLocalSession.class);

    private TransactionInfo transactionInfo;


    ArmySyncLocalSession(final ArmySyncLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncLocalStmtExecutor;
    }


    @Override
    public SyncLocalSessionFactory sessionFactory() {
        return (SyncLocalSessionFactory) this.factory;
    }


    @Override
    public boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        if (((ArmySyncLocalSessionFactory) this.factory).jdbcDriver) {
            return hasTransactionInfo();
        }
        return this.stmtExecutor.inTransaction();
    }

    @Override
    public boolean hasTransactionInfo() {
        return this.transactionInfo != null;
    }


    @Override
    public TransactionInfo startTransaction() {
        return this.startTransaction(TransactionOption.option(null, false), HandleMode.ERROR_IF_EXISTS);
    }

    @Override
    public TransactionInfo startTransaction(TransactionOption option) {
        return this.startTransaction(option, HandleMode.ERROR_IF_EXISTS);
    }


    @Override
    public TransactionInfo startTransaction(TransactionOption option, HandleMode mode) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final TransactionInfo existTransaction = this.transactionInfo;
        if (existTransaction != null) {
            switch (mode) {
                case ERROR_IF_EXISTS:
                    throw _Exceptions.existsTransaction(this);
                case COMMIT_IF_EXISTS: {
                    if (isRollbackOnly()) {
                        throw _Exceptions.rollbackOnlyTransaction(this);
                    }
                }
                break;
                default:
            }
        }


        final TransactionInfo info;
        info = ((SyncLocalStmtExecutor) this.stmtExecutor).startTransaction(option, mode);

        Objects.requireNonNull(info); // stmtExecutor no bug ,never error
        assert info.inTransaction();

        this.transactionInfo = info;
        this.clearRollbackOnly();
        return info;
    }

    @Override
    public void commit() {
        this.commit(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (isRollbackOnly()) {
            throw _Exceptions.rollbackOnlyTransaction(this);
        }

        final TransactionInfo info;
        info = ((SyncLocalStmtExecutor) this.stmtExecutor).commit(optionFunc);

        if (optionFunc != Option.EMPTY_OPTION_FUNC
                && Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN))) {
            assert info != null && info.inTransaction();
        } else {
            assert info == null;
        }
        this.transactionInfo = info;
        return info;
    }

    @Override
    public void rollback() {
        this.rollback(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
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
        return info;
    }


    /*-------------------below protected template methods -------------------*/

    @Override
    protected Logger getLogger() {
        return LOG;
    }


    /*-------------------below package template methods -------------------*/

    @Override
    protected final TransactionInfo obtainTransactionInfo() {
        return this.transactionInfo;
    }


} // ArmySyncLocalSession
