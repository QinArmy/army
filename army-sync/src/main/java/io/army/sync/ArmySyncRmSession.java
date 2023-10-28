package io.army.sync;

import io.army.session.*;
import io.army.sync.executor.SyncRmStmtExecutor;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

final class ArmySyncRmSession extends ArmySyncSession implements SyncRmSession {

    static ArmySyncRmSession create(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        return new ArmySyncRmSession(builder);
    }

    private TransactionInfo transactionInfo;

    private boolean rollbackOnly;


    private ArmySyncRmSession(ArmySyncRmSessionFactory.SyncRmSessionBuilder builder) {
        super(builder);
        assert this.stmtExecutor instanceof SyncRmStmtExecutor;
    }

    @Override
    public SyncRmSessionFactory sessionFactory() {
        return (SyncRmSessionFactory) this.factory;
    }


    @Override
    public boolean hasTransaction() {
        return false;
    }

    @Override
    public boolean isReadOnlyStatus() {
        return false;
    }

    @Override
    public TransactionInfo transactionInfo() {
        return null;
    }

    @Override
    public boolean isRollbackOnly() {
        return false;
    }

    @Override
    public void markRollbackOnly() throws SessionException {

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
    protected Logger getLogger() {
        return null;
    }
}
