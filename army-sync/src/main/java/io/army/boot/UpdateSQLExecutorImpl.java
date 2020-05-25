package io.army.boot;

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
    public final long updateLarge(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        long updateRows;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            updateRows = doExecuteLargeUpdate(session, (SimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            updateRows = (int) doChildUpdate(session, (ChildSQLWrapper) sqlWrapper, true);
        } else {
            throw createNotSupportedException(sqlWrapper, "updateLarge");
        }
        return updateRows;
    }

    @Override
    public final int[] batchUpdate(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        int[] updateRows;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            updateRows = doExecuteBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            updateRows = (int[]) doBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper, false);
        } else {
            throw createNotSupportedException(sqlWrapper, "batchUpdate");
        }
        return updateRows;
    }

    @Override
    public final long[] batchUpdateLarge(InnerSession session, SQLWrapper sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
        long[] updateRows;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            updateRows = doExecuteLargeBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            updateRows = (long[]) doBatchChildUpdate(session, (ChildBatchSQLWrapper) sqlWrapper, true);
        } else {
            throw createNotSupportedException(sqlWrapper, "batchUpdateLarge");
        }
        return updateRows;
    }

    @Override
    public final <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("army will execute update sql:\n{}", this.sessionFactory.dialect().showSQL(sqlWrapper));
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


}
