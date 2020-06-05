package io.army.boot;

import io.army.DomainUpdateException;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSQLExecutorImpl.class);

    static UpdateSQLExecutorImpl build(InnerSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }

    UpdateSQLExecutorImpl(InnerSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final int update(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        int updateRows;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            updateRows = doExecuteUpdate(session, (SimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            updateRows = (int) doChildUpdate(session, (ChildSQLWrapper) sqlWrapper, false);
        } else {
            throw createNotSupportedException(sqlWrapper, "update");
        }
        return updateRows;
    }

    @Override
    public final long largeUpdate(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        long updateRows;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            updateRows = doExecuteLargeUpdate(session, (SimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            updateRows = doChildUpdate(session, (ChildSQLWrapper) sqlWrapper, true);
        } else {
            throw createNotSupportedException(sqlWrapper, "largeUpdate");
        }
        return updateRows;
    }

    @Override
    public final int[] batchUpdate(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute  sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        int[] updateRows;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            updateRows = doExecuteBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            updateRows = doBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper);
        } else {
            throw createNotSupportedException(sqlWrapper, "batchUpdate");
        }
        return updateRows;
    }

    @Override
    public final long[] batchLargeUpdate(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute  sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        long[] updateRows;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            updateRows = doExecuteLargeBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            updateRows = doBatchChildLargeUpdate(session, (ChildBatchSQLWrapper) sqlWrapper);
        } else {
            throw createNotSupportedException(sqlWrapper, "batchUpdateLarge");
        }
        return updateRows;
    }

    @Override
    public final <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute  sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        List<T> resultList;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            resultList = doExecuteSimpleReturning(session, (SimpleSQLWrapper) sqlWrapper, resultClass);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            resultList = doExecuteChildReturning(session, (ChildSQLWrapper) sqlWrapper, resultClass);
        } else {
            throw createNotSupportedException(sqlWrapper, "returningUpdate");
        }
        return resultList;
    }

    /*################################## blow private method ##################################*/

    private long doChildUpdate(InnerSession session, ChildSQLWrapper childSQLWrapper, final boolean large) {
        final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
        final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

        long parentRows, childRows;
        // firstly, execute child update sql.
        if (large) {
            childRows = doExecuteLargeUpdate(session, childWrapper);
        } else {
            childRows = doExecuteUpdate(session, childWrapper);
        }
        // secondly, execute parent update sql.
        if (large) {
            parentRows = doExecuteLargeUpdate(session, parentWrapper);
        } else {
            parentRows = doExecuteUpdate(session, parentWrapper);
        }
        if (parentRows != childRows) {
            throw new DomainUpdateException("Domain update ,child[%s] rows[%s] parent[%s] rows[%s] not match."
                    , childSQLWrapper.childWrapper().sql(), childRows
                    , childSQLWrapper.parentWrapper().sql(), parentRows);
        }
        return childRows;
    }


    private int[] doBatchChildUpdate(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {
        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        int[] parentRows, childRows;
        parentRows = doExecuteBatch(session, parentWrapper);
        childRows = doExecuteBatch(session, childWrapper);

        assertBatchUpdateRows(childRows, parentRows, sqlWrapper);
        return childRows;
    }

    private long[] doBatchChildLargeUpdate(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {
        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        long[] parentRows, childRows;
        parentRows = doExecuteLargeBatch(session, parentWrapper);
        childRows = doExecuteLargeBatch(session, childWrapper);

        assertBatchLargeUpdateRows(childRows, parentRows, sqlWrapper);
        return childRows;
    }

    private static void assertBatchLargeUpdateRows(long[] childRows, long[] parentRows
            , ChildBatchSQLWrapper sqlWrapper) {

        if (parentRows.length != childRows.length) {
            // check domain update batch match.
            throw createBatchNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                    , parentRows.length, childRows.length);
        }

        final int len = childRows.length;
        long parentUpdateRows;
        for (int i = 0; i < len; i++) {
            parentUpdateRows = parentRows[i];
            if (parentUpdateRows != childRows[i]) {
                throw createBatchItemNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                        , i, parentRows[i], childRows[i]);
            }
            if (parentUpdateRows < 1 && sqlWrapper.parentWrapper().hasVersion()) {
                // use child sql,developer can find child table
                throw createOptimisticLockException(sqlWrapper.childWrapper().sql());
            }
        }

    }

    private static void assertBatchUpdateRows(int[] childRows, int[] parentRows, ChildBatchSQLWrapper sqlWrapper) {

        if (parentRows.length != childRows.length) {
            // check domain update batch match.
            throw createBatchNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                    , parentRows.length, childRows.length);
        }

        final int len = childRows.length;
        int parentUpdateRows;
        for (int i = 0; i < len; i++) {
            parentUpdateRows = parentRows[i];
            if (parentUpdateRows != childRows[i]) {
                throw createBatchItemNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                        , i, parentRows[i], childRows[i]);
            }
            if (parentUpdateRows < 1 && sqlWrapper.parentWrapper().hasVersion()) {
                // use child sql,developer can find child table
                throw createOptimisticLockException(sqlWrapper.childWrapper().sql());
            }
        }
    }

    private static DomainUpdateException createBatchItemNotMatchException(String parentSql, String childSql, int index
            , long parentRows, long childRows) {
        throw new DomainUpdateException(
                "Domain update,index[%s] parent sql[%s] update rows[%s] and" +
                        " child sql[%s] update rows[%s] not match."
                , index
                , parentSql
                , parentRows
                , childSql
                , childRows
        );
    }


}
