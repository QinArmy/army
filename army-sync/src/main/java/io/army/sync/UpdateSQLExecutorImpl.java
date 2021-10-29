package io.army.sync;

import io.army.wrapper.*;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiFunction;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    UpdateSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final int update(InnerGenericRmSession session, SQLWrapper sqlWrapper) {
        return internalUpdate(session, sqlWrapper, this::integerUpdate, "update");
    }

    @Override
    public final long largeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper) {
        return internalUpdate(session, sqlWrapper, this::longUpdate, "largeUpdate");
    }

    @Override
    public final List<Integer> batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper) {
        return internalBatchUpdate(session, sqlWrapper, this::integerBatchUpdate, "batchUpdate");
    }

    @Override
    public final List<Long> batchLargeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper) {
        return internalBatchUpdate(session, sqlWrapper, this::longBatchUpdate, "batchLargeUpdate");
    }

    @Override
    public final <T> List<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass) {
        return doExecuteReturning(session, sqlWrapper, resultClass, true, "returningUpdate");
    }

    /*################################## blow private method ##################################*/

    /**
     * @param executeFunction execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link #integerUpdate(PreparedStatement, SimpleSQLWrapper)}</li>
     *                          <li>{@link #longUpdate(PreparedStatement, SimpleSQLWrapper)}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Integer or Long}
     */
    private <N extends Number> N internalUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , BiFunction<PreparedStatement, SimpleSQLWrapper, N> executeFunction, String methodName) {
        N rows;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            SimpleSQLWrapper simpleSQLWrapper = (SimpleSQLWrapper) sqlWrapper;
            ///1. execute update sql
            rows = doExecuteUpdate(session, simpleSQLWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            final ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
            //1. execute child update sql
            rows = doExecuteUpdate(session, childWrapper, executeFunction);
            if (rows.longValue() > 1L) {
                final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
                N parentRows;
                //2. execute parent insert sql
                parentRows = doExecuteUpdate(session, parentWrapper, executeFunction);
                //3. assert parent updated rows and child match
                assertParentChildRowsNotMatch(parentRows, rows, childWrapper);
            }
        } else {
            throw createUnSupportedSQLWrapperException(sqlWrapper, methodName);
        }
        return rows;
    }

    private <N extends Number> List<N> internalBatchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , BiFunction<PreparedStatement, BatchSimpleSQLWrapper, List<N>> executeFunction, String methodName) {
        List<N> resultList;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            BatchSimpleSQLWrapper simpleSQLWrapper = (BatchSimpleSQLWrapper) sqlWrapper;
            // 1. execute batch update sql
            resultList = doExecuteBatch(session, simpleSQLWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            final ChildBatchSQLWrapper childSQLWrapper = (ChildBatchSQLWrapper) sqlWrapper;
            final BatchSimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
            //1. execute child batch update sql
            resultList = doExecuteBatch(session, childWrapper, executeFunction);
            final BatchSimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
            List<N> parentList;
            //2. execute parent batch update sql
            parentList = doExecuteBatch(session, parentWrapper, executeFunction);
            //3. assert batch result
            assertBatchUpdate(parentList, resultList, childWrapper);
        } else {
            throw createUnSupportedSQLWrapperException(sqlWrapper, methodName);
        }
        return resultList;
    }

    private void assertBatchUpdate(List<? extends Number> parentList, List<? extends Number> childList
            , BatchSimpleSQLWrapper childWrapper) {

        if (parentList.size() != childList.size()) {
            throw createBatchChildInsertNotMatchException(parentList.size(), childList.size(), childWrapper);
        }
        final int size = childList.size();
        for (int i = 0; i < size; i++) {
            if (!parentList.get(i).equals(childList.get(i))) {
                throw createParentUpdateNotMatchException(parentList.get(i)
                        , childList.get(i), childWrapper);
            }
        }
    }

    private <N extends Number> void assertParentChildRowsNotMatch(N parentRows, N childRows
            , GenericSimpleWrapper simpleWrapper) {
        if (!parentRows.equals(childRows)) {
            throw createParentUpdateNotMatchException(parentRows, childRows, simpleWrapper);
        }
    }
}
