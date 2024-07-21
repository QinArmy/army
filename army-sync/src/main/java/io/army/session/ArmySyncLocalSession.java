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

package io.army.session;

import io.army.dialect.Database;
import io.army.executor.DriverSpiHolder;
import io.army.executor.SyncLocalExecutor;
import io.army.lang.Nullable;
import io.army.option.Option;
import io.army.result.ChildUpdateException;
import io.army.transaction.HandleMode;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link SyncLocalSession}
 *
 * @see ArmySyncSessionFactory
 */
non-sealed class ArmySyncLocalSession extends ArmySyncSession implements SyncLocalSession {

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
        assert this.executor instanceof SyncLocalExecutor;
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
            this.transactionInfo = TransactionInfo.forRollbackOnly(info);
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
                        this.rollbackOnly = false;
                    }
                }
                break;
                case ROLLBACK_IF_EXISTS: {
                    if (existTransaction.isolation() == Isolation.PSEUDO) {
                        this.transactionInfo = null; // clear pseudo transaction
                        this.rollbackOnly = false;
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);

            } //  switch

        } //    if (existTransaction != null)

        final TransactionInfo info;
        if (startPseudo) {
            info = TransactionInfo.pseudoLocal(option);
        } else {
            info = ((SyncLocalExecutor) this.executor).startTransaction(option, mode, Option.EMPTY_FUNC);
            assertTransactionInfo(info, option);
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

        final TransactionInfo existingInfo = this.transactionInfo;

        final TransactionInfo info;
        if (existingInfo != null && existingInfo.isolation() == Isolation.PSEUDO) {
            info = null; // clear pseudo transaction
        } else {
            info = commitOrRollback(true, optionFunc);
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
        if (this.transactionInfo != null) {
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

        final TransactionInfo existingInfo = this.transactionInfo;

        final TransactionInfo newInfo;
        if (existingInfo != null && existingInfo.isolation() == Isolation.PSEUDO) {
            newInfo = null; // clear pseudo transaction
        } else {
            newInfo = commitOrRollback(false, optionFunc);
        }

        this.transactionInfo = newInfo;
        this.rollbackOnly = false;
        return newInfo;
    }

    @Override
    public final void rollbackIfExists() {
        rollbackIfExists(Option.EMPTY_FUNC);
    }

    @Nullable
    @Override
    public final TransactionInfo rollbackIfExists(final Function<Option<?>, ?> optionFunc) {
        if (this.transactionInfo != null) {
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

    /**
     * @see #commit(Function)
     * @see #rollback(Function)
     */
    @Nullable
    private TransactionInfo commitOrRollback(final boolean commit, final Function<Option<?>, ?> optionFunc) {
        final Boolean chain, release;
        if (optionFunc == Option.EMPTY_FUNC) {
            chain = release = null;
        } else {
            chain = Boolean.TRUE.equals(optionFunc.apply(Option.CHAIN));
            release = Boolean.TRUE.equals(optionFunc.apply(Option.RELEASE));
            if (Boolean.TRUE.equals(chain) && Boolean.TRUE.equals(release)) {
                throw _Exceptions.chainAndReleaseConflict();
            }
        }

        final Database database = this.factory.serverDatabase;
        switch (database) {
            case PostgreSQL: {
                if (release != null && release) {
                    throw _Exceptions.dontSupportRelease(database);
                }
            }
            break;
            case MySQL:
            default: // no-op
        }

        final TransactionInfo existingInfo = this.transactionInfo;

        final TransactionInfo newInfo;
        if (commit) {
            newInfo = ((SyncLocalExecutor) this.executor).commit(optionFunc, Option.EMPTY_FUNC);
        } else {
            newInfo = ((SyncLocalExecutor) this.executor).rollback(optionFunc, Option.EMPTY_FUNC);
        }

        switch (database) {
            case MySQL: {
                if (chain == null) {
                    assert newInfo == null; // fail,executor bug
                } else if (chain) {
                    assert newInfo != null && newInfo.inTransaction(); // fail,executor bug
                    assert newInfo != existingInfo;
                    assert newInfo.valueOf(Option.START_MILLIS) != null;
                } else if (release) {
                    assert newInfo == null; // fail,executor bug
                    releaseSession();
                }
            }
            break;
            case PostgreSQL: {
                if (chain == null) {
                    assert newInfo == null; // fail,executor bug
                } else {
                    assert newInfo != null && newInfo.inTransaction(); // fail,executor bug
                    assert newInfo != existingInfo;
                    assert newInfo.valueOf(Option.START_MILLIS) != null;
                }
            }
            break;
            default:
                assert newInfo == null; // fail,executor bug
        }

        return newInfo;
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
            return this.executor.isDriverAssignableTo(spiClass);
        }

        @Override
        public <T> T getDriverSpi(Class<T> spiClass) {
            return this.executor.getDriverSpi(spiClass);
        }


    } // OpenDriverSpiSession


}
