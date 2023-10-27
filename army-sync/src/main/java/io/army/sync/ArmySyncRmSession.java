package io.army.sync;

import io.army.session.*;
import io.army.sync.executor.SyncRmStmtExecutor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

final class ArmySyncRmSession extends ArmySyncSession implements SyncRmSession {

    static ArmySyncRmSession create(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        return new ArmySyncRmSession(builder);
    }

    private ArmySyncRmSession(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncRmStmtExecutor;
    }

    @Override
    public SyncRmSessionFactory sessionFactory() {
        return null;
    }

    @Override
    public TransactionInfo start(Xid xid) {
        return null;
    }

    @Override
    public TransactionInfo start(Xid xid, int flags) {
        return null;
    }

    @Override
    public TransactionInfo start(Xid xid, int flags, TransactionOption option) {
        return null;
    }

    @Override
    public TransactionInfo end(Xid xid) {
        return null;
    }

    @Override
    public TransactionInfo end(Xid xid, int flags) {
        return null;
    }

    @Override
    public TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public int prepare(Xid xid) {
        return 0;
    }

    @Override
    public int prepare(Xid xid, Function<Option<?>, ?> optionFunc) {
        return 0;
    }

    @Override
    public SyncRmSession commit(Xid xid) {
        return null;
    }

    @Override
    public SyncRmSession commit(Xid xid, int flags) {
        return null;
    }

    @Override
    public SyncRmSession commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public SyncRmSession rollback(Xid xid) {
        return null;
    }

    @Override
    public SyncRmSession rollback(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public SyncRmSession forget(Xid xid) {
        return null;
    }

    @Override
    public SyncRmSession forget(Xid xid, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public List<Xid> recover(int flags) {
        return null;
    }

    @Override
    public List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public Stream<Xid> recoverStream(int flags) {
        return null;
    }

    @Override
    public Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    public boolean isSupportForget() {
        return false;
    }

    @Override
    public int startSupportFlags() {
        return 0;
    }

    @Override
    public int endSupportFlags() {
        return 0;
    }

    @Override
    public int recoverSupportFlags() {
        return 0;
    }

    @Override
    public boolean isSameRm(XaTransactionSupportSpec s) {
        return false;
    }

    @Override
    public boolean isRollbackOnly() {
        return false;
    }

    @Override
    public SyncRmSession markRollbackOnly() throws SessionException {
        return this;
    }


}
