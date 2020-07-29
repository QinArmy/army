package io.army.boot.sync;

import io.army.DomainUpdateException;
import io.army.codec.StatementType;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSQLExecutorImpl.class);


    UpdateSQLExecutorImpl(InnerRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final int update(InnerSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
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
    public final long largeUpdate(InnerSession session, SQLWrapper sqlWrapper, boolean updateStatement) {
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
    public final <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass
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


    @SuppressWarnings("unchecked")
    @Override
    public final <V extends Number> Map<Integer, V> batchUpdate(InnerSession session, SQLWrapper sqlWrapper
            , Class<V> mapValueClass, boolean updateStatement) {

        session.codecContextStatementType(updateStatement ? StatementType.UPDATE : StatementType.DELETE);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute batch update/delete sql:\n{}"
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            Map<Integer, V> batchResultMap;
            if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
                if (mapValueClass == Integer.class) {
                    batchResultMap = (Map<Integer, V>) doExecuteBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
                } else if (mapValueClass == Long.class) {
                    batchResultMap = (Map<Integer, V>) doExecuteLargeBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
                } else {
                    throw new IllegalArgumentException("mapValueClass error.");
                }
            } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                batchResultMap = doBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper, mapValueClass);
            } else {
                throw createNotSupportedException(sqlWrapper, "batchUpdate");
            }
            return batchResultMap;
        } finally {
            session.codecContextStatementType(null);
        }
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


    @SuppressWarnings("unchecked")
    private <V extends Number> Map<Integer, V> doBatchChildUpdate(InnerSession session
            , ChildBatchSQLWrapper sqlWrapper, Class<V> mapValueClass) {
        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        Map<Integer, V> batchResultMap;
        if (mapValueClass == Integer.class) {
            Map<Integer, Integer> parentResultMap, childResultMap;
            // firstly, execute child sql
            childResultMap = doExecuteBatch(session, childWrapper);
            // secondly,execute parent sql
            parentResultMap = doExecuteBatch(session, parentWrapper);
            // assert child execute result and parent result match.
            assertParentChildBatchResult(parentResultMap, childResultMap, sqlWrapper);

            batchResultMap = (Map<Integer, V>) childResultMap;
        } else if (mapValueClass == Long.class) {
            Map<Integer, Long> parentResultMap, childResultMap;
            // firstly, execute child sql
            childResultMap = doExecuteLargeBatch(session, childWrapper);
            // secondly,execute parent sql
            parentResultMap = doExecuteLargeBatch(session, parentWrapper);
            // assert child execute result and parent result match.
            assertParentChildBatchResult(parentResultMap, childResultMap, sqlWrapper);

            batchResultMap = (Map<Integer, V>) childResultMap;
        } else {
            throw new IllegalArgumentException("mapValueClass error.");
        }
        return batchResultMap;
    }

    private void assertParentChildBatchResult(Map<Integer, ? extends Number> parentResultMap
            , Map<Integer, ? extends Number> childResultMap, ChildBatchSQLWrapper sqlWrapper) {

        if (parentResultMap.size() != childResultMap.size()) {
            // check domain update batch match.
            throw createBatchNotMatchException(this.sessionFactory.name(), sqlWrapper.parentWrapper().sql()
                    , sqlWrapper.childWrapper().sql()
                    , parentResultMap.size(), childResultMap.size());
        }

        for (Map.Entry<Integer, ? extends Number> p : parentResultMap.entrySet()) {
            Number parentUpdateRows = p.getValue();
            if (!parentUpdateRows.equals(childResultMap.get(p.getKey()))) {
                throw createBatchItemNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                        , p.getKey(), parentUpdateRows, childResultMap.get(p.getKey()));
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
