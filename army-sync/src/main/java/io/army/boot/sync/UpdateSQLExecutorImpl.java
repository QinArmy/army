package io.army.boot.sync;

import io.army.DomainUpdateException;
import io.army.codec.StatementType;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSQLExecutorImpl.class);


    UpdateSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final int update(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
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
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final long largeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
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
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final <T> List<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass
            , boolean updateStatement) {
        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
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
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final int[] batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute batch update/delete sql:\n{}"
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            int[] batchResult;
            if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
                batchResult = doExecuteBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                batchResult = doBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper);
            } else {
                throw createNotSupportedException(sqlWrapper, "batchUpdate");
            }
            return batchResult;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final long[] batchLargeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute large batch update/delete sql:\n{}"
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            long[] batchResult;
            if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
                batchResult = doExecuteLargeBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                batchResult = doLargeBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper);
            } else {
                throw createNotSupportedException(sqlWrapper, "batchLargeUpdate");
            }
            return batchResult;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    /*################################## blow private method ##################################*/


    private long doChildUpdate(InnerGenericRmSession session, ChildSQLWrapper childSQLWrapper, final boolean large) {
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

    private int[] doBatchChildUpdate(InnerGenericRmSession session, ChildBatchSQLWrapper sqlWrapper) {
        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        assertParamGroupListSizeMatch(parentWrapper, childWrapper);

        int[] parentResult, childResult;
        // firstly, execute child sql
        childResult = doExecuteBatch(session, childWrapper);
        // secondly, execute child sql
        parentResult = doExecuteBatch(session, parentWrapper);

        assertBatchUpdateRows(childResult, parentResult, sqlWrapper);
        return childResult;
    }

    private long[] doLargeBatchChildUpdate(InnerGenericRmSession session, ChildBatchSQLWrapper sqlWrapper) {
        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        assertParamGroupListSizeMatch(parentWrapper, childWrapper);

        long[] parentResult, childResult;
        // firstly, execute child sql
        childResult = doExecuteLargeBatch(session, childWrapper);
        // secondly, execute child sql
        parentResult = doExecuteLargeBatch(session, parentWrapper);

        assertBatchLargeUpdateRows(childResult, parentResult, sqlWrapper);
        return childResult;
    }


    private void assertBatchUpdateRows(int[] childRows, int[] parentRows, ChildBatchSQLWrapper sqlWrapper) {

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

    private void assertBatchLargeUpdateRows(long[] childRows, long[] parentRows
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


    private static DomainUpdateException createBatchItemNotMatchException(String parentSql
            , String childSql, Integer index
            , Number parentRows, Number childRows) {
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
