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

import io.army.executor.DriverSpiHolder;
import io.army.executor.SyncRmExecutor;
import io.army.option.Option;
import io.army.result.ChildUpdateException;
import io.army.transaction.*;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>This class is a implementation of {@link SyncRmSession}
 *
 * @see ArmySyncSessionFactory
 * @since 0.6.0
 */
class ArmySyncRmSession extends ArmySyncSession implements SyncRmSession {

    static ArmySyncRmSession create(ArmySyncSessionFactory.RmBuilder builder) {
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

    private boolean rollbackOnly;

    /**
     * private constructor
     */
    private ArmySyncRmSession(ArmySyncSessionFactory.RmBuilder builder) {
        super(builder);
        assert this.executor instanceof SyncRmExecutor;
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
    public final void markRollbackOnly() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
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
    public final TransactionInfo start(Xid xid) {
        return this.start(xid, TM_NO_FLAGS, TransactionOption.option(null, false));
    }

    @Override
    public final TransactionInfo start(Xid xid, int flags) {
        return this.start(xid, flags, TransactionOption.option(null, false));
    }

    @Override
    public final TransactionInfo start(final @Nullable Xid xid, final int flags, final TransactionOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (this.transactionInfo != null) {
            throw _Exceptions.existsTransaction(this);
        } else if (xid == null) {
            throw _Exceptions.xidIsNull();
        }

        final TransactionInfo info;

        if (option.isolation() != Isolation.PSEUDO) {
            info = ((SyncRmExecutor) this.executor).start(xid, flags, option);
            assertTransactionInfo(info, option);
            assert xid.equals(info.valueOf(Option.XID));  // fail ,executor bug
            assert info.valueOf(Option.XA_STATES) == XaStates.ACTIVE;  // fail ,executor bug
            assert info.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

        } else if (!this.readonly) {
            throw _Exceptions.writeSessionPseudoTransaction(this);
        } else if (!option.isReadOnly()) {
            throw _Exceptions.pseudoWriteError(this, option);
        } else {
            info = TransactionInfo.pseudoStart(xid, flags, option);
        }

        if (this.transactionInfo != null) {
            throw new ConcurrentModificationException();
        }
        this.transactionInfo = info;
        this.rollbackOnly = false;
        return info;
    }

    @Override
    public final TransactionInfo end(Xid xid) {
        return this.end(xid, TM_NO_FLAGS, Option.EMPTY_FUNC);
    }

    @Override
    public final TransactionInfo end(Xid xid, int flags) {
        return this.end(xid, flags, Option.EMPTY_FUNC);
    }

    @Override
    public final TransactionInfo end(final @Nullable Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {

        final TransactionInfo lastInfo = this.transactionInfo, info;
        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // no bug,never here
            throw new NullPointerException();
        } else if (lastInfo == null) {
            throw _Exceptions.noTransaction(this);
        } else if (!(infoXid = lastInfo.nonNullOf(Option.XID)).equals(xid)) {
            throw _Exceptions.xaNonCurrentTransaction(xid); // use xid
        } else if ((states = lastInfo.nonNullOf(Option.XA_STATES)) != XaStates.ACTIVE) {
            throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, states); // use infoXid
        }

        if (lastInfo.isolation() == Isolation.PSEUDO) {
            info = TransactionInfo.pseudoEnd(lastInfo, flags);
        } else {
            info = ((SyncRmExecutor) this.executor).end(infoXid, flags, optionFunc); // use infoXid

            assertXaEndTransactionInfo(lastInfo, flags, info);
        }

        if (this.transactionInfo != null) {
            throw new ConcurrentModificationException();
        }
        this.transactionInfo = info;
        return info;
    }


    @Override
    public final int prepare(Xid xid) {
        return this.prepare(xid, Option.EMPTY_FUNC);
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
        } else if ((lastInfo.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0 || this.rollbackOnly) {
            throw _Exceptions.xaTransactionRollbackOnly(infoXid);
        }

        final int flags;
        if (lastInfo.isolation() == Isolation.PSEUDO) {
            flags = XA_RDONLY;
        } else {
            flags = ((SyncRmExecutor) this.executor).prepare(infoXid, optionFunc); // use infoXid

        }

        if (this.transactionInfo != lastInfo) {
            throw new ConcurrentModificationException();
        }

        this.transactionInfo = null;
        this.rollbackOnly = false;
        return flags;
    }

    @Override
    public final void commit(Xid xid) {
        this.commit(xid, TM_NO_FLAGS, Option.EMPTY_FUNC);
    }

    @Override
    public final void commit(Xid xid, int flags) {
        this.commit(xid, flags, Option.EMPTY_FUNC);
    }

    @Override
    public final void commit(final @Nullable Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {

        final TransactionInfo info = this.transactionInfo;

        final XaStates states;
        final Xid infoXid;

        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // application developer no bug,never here
            throw _Exceptions.xidIsNull();
        } else if ((flags & TM_ONE_PHASE) == 0) { // tow phase commit
            if (info != null && info.nonNullOf(Option.XID).equals(xid)) {
                throw _Exceptions.xaTowPhaseXidConflict(xid);
            }
            ((SyncRmExecutor) this.executor).commit(xid, flags, optionFunc); // use xid
        } else if (info == null) {
            throw _Exceptions.noTransaction(this);
        } else if (!(infoXid = info.nonNullOf(Option.XID)).equals(xid)) {
            throw _Exceptions.xaNonCurrentTransaction(xid); // use xid
        } else if ((states = info.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            throw _Exceptions.xaStatesDontSupportCommitCommand(infoXid, states); // use infoXid
        } else if (this.rollbackOnly || (info.nonNullOf(Option.XA_FLAGS) & TM_FAIL) != 0) {
            // rollback only
            throw _Exceptions.xaTransactionRollbackOnly(infoXid);
        } else if (info.isolation() == Isolation.PSEUDO) { // one phase commit pseudo transaction
            this.transactionInfo = null; // clear transactionInfo for one phase commit
            this.rollbackOnly = false; // clear transactionInfo for one phase commit
        } else {  // one phase commit

            ((SyncRmExecutor) this.executor).commit(infoXid, flags, optionFunc); // use infoXid
            if (this.transactionInfo != info) {
                throw new ConcurrentModificationException();
            }
            this.transactionInfo = null;  // clear transactionInfo for one phase commit
            this.rollbackOnly = false;  // clear transactionInfo for one phase commit
        }

    }

    @Override
    public final void rollback(Xid xid) {
        this.rollback(xid, Option.EMPTY_FUNC);
    }

    @Override
    public final void rollback(final @Nullable Xid xid, Function<Option<?>, ?> optionFunc) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        } else if (xid == null) {
            // application developer no bug,never here
            throw _Exceptions.xidIsNull();
        }

        final TransactionInfo info = this.transactionInfo;
        final XaStates states;
        final Xid infoXid;

        if (info == null || !((infoXid = info.nonNullOf(Option.XID)).equals(xid))) { // tow phase rollback
            ((SyncRmExecutor) this.executor).rollback(xid, optionFunc);
        } else if ((states = info.nonNullOf(Option.XA_STATES)) != XaStates.IDLE) {
            throw _Exceptions.xaStatesDontSupportRollbackCommand(infoXid, states);
        } else if (info.isolation() == Isolation.PSEUDO) { // one phase rollback pseudo transaction
            this.transactionInfo = null; // clear  for one phase rollback
            this.rollbackOnly = false; // clear  for one phase rollback
        } else {
            ((SyncRmExecutor) this.executor).rollback(infoXid, optionFunc);  // use infoXid
            if (this.transactionInfo != info) {
                throw new ConcurrentModificationException();
            }
            this.transactionInfo = null; // clear  for one phase rollback
            this.rollbackOnly = false; // clear  for one phase rollback
        }


    }

    @Override
    public final void forget(Xid xid) {
        this.forget(xid, Option.EMPTY_FUNC);
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

        ((SyncRmExecutor) this.executor).forget(xid, optionFunc);

    }

    @Override
    public final List<Xid> recoverList(int flags) {
        return this.recoverList(flags, Option.EMPTY_FUNC);
    }

    @Override
    public final List<Xid> recoverList(int flags, Function<Option<?>, ?> optionFunc) {
        try (Stream<Xid> stream = recover(flags, optionFunc, ArmyStreamOptions.DEFAULT)) {
            return stream.collect(Collectors.toCollection(_Collections::arrayList));
        }
    }

    @Override
    public final Stream<Xid> recover(int flags) {
        return this.recover(flags, Option.EMPTY_FUNC, ArmyStreamOptions.DEFAULT);
    }

    @Override
    public final Stream<Xid> recover(int flags, Function<Option<?>, ?> optionFunc, StreamOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        return ((SyncRmExecutor) this.executor).recover(flags, optionFunc, option);
    }

    @Override
    public final boolean isSupportForget() {
        return ((SyncRmExecutor) this.executor).isSupportForget();
    }

    @Override
    public final int startSupportFlags() {
        return ((SyncRmExecutor) this.executor).startSupportFlags();
    }

    @Override
    public final int endSupportFlags() {
        return ((SyncRmExecutor) this.executor).endSupportFlags();
    }

    @Override
    public final int commitSupportFlags() {
        return ((SyncRmExecutor) this.executor).commitSupportFlags();
    }

    @Override
    public final int recoverSupportFlags() {
        return ((SyncRmExecutor) this.executor).recoverSupportFlags();
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
            match = ((SyncRmExecutor) this.executor).isSameRm((SyncRmExecutor) ((ArmySyncRmSession) s).executor);
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
        markRollbackOnly();
    }


    private static final class OpenDriverSpiSession extends ArmySyncRmSession implements DriverSpiHolder {

        private OpenDriverSpiSession(ArmySyncSessionFactory.RmBuilder builder) {
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
