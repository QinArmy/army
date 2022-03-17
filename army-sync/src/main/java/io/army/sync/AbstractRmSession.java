package io.army.sync;

import io.army.ArmyException;
import io.army.ArmyUnknownException;
import io.army.SessionException;
import io.army.SessionUsageException;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect._Dialect;
import io.army.lang.Nullable;
import io.army.session.DialectSessionFactory;
import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.GenericTransaction;
import io.army.tx.Isolation;
import io.army.tx.TransactionTimeOutException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class AbstractRmSession extends _AbstractSyncSession
        implements GenericSyncRmSession {

    final StmtExecutor stmtExecutor;

    final _Dialect dialect;
    final Function<ArmyException, RuntimeException> exceptionFunction;

    AbstractRmSession(DialectSessionFactory sessionFactory, StmtExecutor stmtExecutor) {
        this.stmtExecutor = stmtExecutor;
        this.dialect = null;
        this.exceptionFunction = sessionFactory.exceptionFunction();
    }


    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
        try {
            assertSessionActive(select);
            final Stmt stmt;
            stmt = this.dialect.select(select, visible);
            return this.stmtExecutor.select(stmt, timeToLiveInSeconds(), resultClass);
        } catch (ArmyException e) {
            throw this.exceptionFunction.apply(e);
        } catch (RuntimeException e) {
            throw this.exceptionFunction.apply(new ArmyUnknownException(e));
        } finally {
            ((_Statement) select).clear();
        }
    }


    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass
            , Supplier<List<R>> listConstructor, Visible visible) {
        return null;
    }

    @Override
    public final long update(Update update, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseUpdate(update, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .largeUpdate(this, stmt, true);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) update).clear();
//        }
        return 0;
    }


    @Override
    public final long delete(Delete delete, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseDelete(delete, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .largeUpdate(this, stmt, false);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) delete).clear();
//        }
        return 0;
    }


    @Override
    public void close() throws SessionException {
//        if (this.genericClosed) {
//            return;
//        }
//        GenericTransaction tx = obtainTransaction();
//        if (tx != null && !tx.transactionEnded()) {
//            throw new TransactionNotCloseException("Session transaction not close,tx status[%s]"
//                    , tx.status());
//        }
//        try {
//            this.connection.close();
//            this.genericClosed = true;
//        } catch (SQLException e) {
//            throw new SessionCloseFailureException(e, "Connection close failure.");
//        }

    }




    /*################################## blow package method ##################################*/

    final void assertSessionActive(Statement statement) {
        if (this.closed()) {
            String m = String.format("%s have closed.", this);
            throw new SessionUsageException(m);
        }
        final _Statement.SessionMode mode;
        mode = ((_Statement) statement).sessionMode();
        if (this.isReadonlySession() && mode != _Statement.SessionMode.READ) {
            String m = String.format("%s is read only.", this);
            throw new SessionUsageException(m);
        }
        final GenericTransaction tx = obtainTransaction();
        if (tx != null && tx.nonActive()) {
            String m = String.format("%s %s non-active.", this, tx);
            throw new SessionUsageException(m);
        }
        if (mode == _Statement.SessionMode.WRITE_TRANSACTION
                && (tx == null || tx.isolation() == Isolation.READ_UNCOMMITTED)) {
            String m = String.format("%s non-safe transaction,can't execute dml for Child table.", this);
            throw new SessionUsageException(m);
        }

    }

    final int timeToLiveInSeconds() throws TransactionTimeOutException {
        final GenericTransaction tx = obtainTransaction();
        int liveInsSeconds;
        if (tx == null) {
            liveInsSeconds = -1;
        } else {
            liveInsSeconds = tx.nextTimeout();
        }
        return liveInsSeconds;
    }

    @Nullable
    abstract GenericTransaction obtainTransaction();


    /*################################## blow private method ##################################*/


}
