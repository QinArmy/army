package io.army.boot.reactive;

import io.army.DomainUpdateException;
import io.army.ReadOnlySessionException;
import io.army.SessionUsageException;
import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.tx.GenericTransaction;
import io.army.tx.Isolation;
import io.army.wrapper.ChildBatchSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.jdbd.StatelessDatabaseSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

abstract class AbstractGenericReactiveRmSession<S extends StatelessDatabaseSession, F extends InnerGenericRmSessionFactory>
        extends AbstractGenericReactiveSession implements InnerGenericRmSession {

    final F sessionFactory;

    final S databaseSession;

    final Dialect dialect;

    final ReactiveSelectSQLExecutor selectSQLExecutor;

    final ReactiveInsertSQLExecutor insertSQLExecutor;

    final ReactiveUpdateSQLExecutor updateSQLExecutor;

    AbstractGenericReactiveRmSession(F sessionFactory, S databaseSession, boolean readOnly) {
        super(readOnly);
        this.sessionFactory = sessionFactory;
        this.databaseSession = databaseSession;

        this.dialect = sessionFactory.dialect();
        this.selectSQLExecutor = this.sessionFactory.selectSQLExecutor();
        this.insertSQLExecutor = this.sessionFactory.insertSQLExecutor();
        this.updateSQLExecutor = this.sessionFactory.updateSQLExecutor();
    }


    @Override
    public final <R> Flux<R> select(Select select, Class<R> resultClass, final Visible visible) {
        assertSessionActive(false);
        return this.selectSQLExecutor
                .select(this, this.dialect.select(select, visible), resultClass);
    }

    @Override
    public final <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Integer> subQueryInsert(Insert insert, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Long> largeSubQueryInsert(Insert insert, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Integer> update(Update update, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Long> largeUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public final <R> Flux<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Integer> delete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    public final Mono<Long> largeDelete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    public final <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return null;
    }

    /*################################## blow package method ##################################*/


    final SQLWrapper parseSubQueryInsert(Insert insert, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.subQueryInsert(insert, visible);
        if (sqlWrapper instanceof ChildSQLWrapper) {
            assertChildDomain();

        }
        return sqlWrapper;
    }

    final SQLWrapper parseReturningInsert(Insert insert, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        SQLWrapper sqlWrapper = this.dialect.returningInsert(insert, visible);
        if (sqlWrapper instanceof ChildSQLWrapper) {
            assertChildDomain();

        }
        return sqlWrapper;
    }

    final List<SQLWrapper> parseValueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet
            , final Visible visible) {

        return sqlWrapperList;
    }

    final SQLWrapper parseUpdate(Update update, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.update(update, visible);
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    final SQLWrapper parseDelete(Delete delete, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.delete(delete, visible);
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    final void markRollbackOnlyForChildUpdate(SQLWrapper sqlWrapper) {
        GenericTransaction transaction = obtainTransaction();
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            if (transaction != null) {
                transaction.markRollbackOnly();
            }
        }
    }

    final void markRollbackOnlyForChildInsert(List<SQLWrapper> sqlWrapperList) {
        GenericTransaction transaction = obtainTransaction();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                if (transaction != null) {
                    transaction.markRollbackOnly();
                    break;
                }

            }
        }
    }

    final void assertChildDomain() {
        GenericTransaction tx = obtainTransaction();
        if (tx == null || tx.isolation().level < Isolation.READ_COMMITTED.level) {
            throw new DomainUpdateException("Child domain update must in READ_COMMITTED(+) transaction.");
        }
    }

    final void assertSessionActive(final boolean write) {
        GenericTransaction tx = obtainTransaction();
        if (this.closed() || (tx != null && tx.nonActive())) {
            String txName = this.sessionTransaction().name();
            throw new SessionUsageException("TmSession[%s] closed or Transaction[%s] not active.", txName, txName);
        }
        if (write && this.readonly()) {
            throw new ReadOnlySessionException("%s read only");
        }
    }

    final void assertForValueInsert(SQLWrapper sqlWrapper) {
        if (sqlWrapper instanceof ChildSQLWrapper) {
            assertChildDomain();

        }
    }

    /*################################## blow private method ##################################*/


}
