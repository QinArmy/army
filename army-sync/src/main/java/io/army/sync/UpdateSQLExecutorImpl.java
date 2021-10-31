package io.army.sync;

import io.army.stmt.*;

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
    public final int update(InnerGenericRmSession session, Stmt stmt) {
        return internalUpdate(session, stmt, this::integerUpdate, "update");
    }

    @Override
    public final long largeUpdate(InnerGenericRmSession session, Stmt stmt) {
        return internalUpdate(session, stmt, this::longUpdate, "largeUpdate");
    }

    @Override
    public final List<Integer> batchUpdate(InnerGenericRmSession session, Stmt stmt) {
        return internalBatchUpdate(session, stmt, this::integerBatchUpdate, "batchUpdate");
    }

    @Override
    public final List<Long> batchLargeUpdate(InnerGenericRmSession session, Stmt stmt) {
        return internalBatchUpdate(session, stmt, this::longBatchUpdate, "batchLargeUpdate");
    }

    @Override
    public final <T> List<T> returningUpdate(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass) {
        return doExecuteReturning(session, stmt, resultClass, true, "returningUpdate");
    }

    /*################################## blow private method ##################################*/

    /**
     * @param executeFunction execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link #integerUpdate(PreparedStatement, SimpleStmt)}</li>
     *                          <li>{@link #longUpdate(PreparedStatement, SimpleStmt)}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Integer or Long}
     */
    private <N extends Number> N internalUpdate(InnerGenericRmSession session, Stmt stmt
            , BiFunction<PreparedStatement, SimpleStmt, N> executeFunction, String methodName) {
        N rows;
        if (stmt instanceof SimpleStmt) {
            SimpleStmt simpleSQLWrapper = (SimpleStmt) stmt;
            ///1. execute update sql
            rows = doExecuteUpdate(session, simpleSQLWrapper, executeFunction);
        } else if (stmt instanceof ChildStmt) {
            final ChildStmt childSQLWrapper = (ChildStmt) stmt;
            final SimpleStmt childWrapper = childSQLWrapper.childWrapper();
            //1. execute child update sql
            rows = doExecuteUpdate(session, childWrapper, executeFunction);
            if (rows.longValue() > 1L) {
                final SimpleStmt parentWrapper = childSQLWrapper.parentWrapper();
                N parentRows;
                //2. execute parent insert sql
                parentRows = doExecuteUpdate(session, parentWrapper, executeFunction);
                //3. assert parent updated rows and child match
                assertParentChildRowsNotMatch(parentRows, rows, childWrapper);
            }
        } else {
            throw createUnSupportedSQLWrapperException(stmt, methodName);
        }
        return rows;
    }

    private <N extends Number> List<N> internalBatchUpdate(InnerGenericRmSession session, Stmt stmt
            , BiFunction<PreparedStatement, BatchSimpleStmt, List<N>> executeFunction, String methodName) {
        List<N> resultList;
        if (stmt instanceof BatchSimpleStmt) {
            BatchSimpleStmt simpleSQLWrapper = (BatchSimpleStmt) stmt;
            // 1. execute batch update sql
            resultList = doExecuteBatch(session, simpleSQLWrapper, executeFunction);
        } else if (stmt instanceof ChildBatchStmt) {
            final ChildBatchStmt childSQLWrapper = (ChildBatchStmt) stmt;
            final BatchSimpleStmt childWrapper = childSQLWrapper.childWrapper();
            //1. execute child batch update sql
            resultList = doExecuteBatch(session, childWrapper, executeFunction);
            final BatchSimpleStmt parentWrapper = childSQLWrapper.parentWrapper();
            List<N> parentList;
            //2. execute parent batch update sql
            parentList = doExecuteBatch(session, parentWrapper, executeFunction);
            //3. assert batch result
            assertBatchUpdate(parentList, resultList, childWrapper);
        } else {
            throw createUnSupportedSQLWrapperException(stmt, methodName);
        }
        return resultList;
    }

    private void assertBatchUpdate(List<? extends Number> parentList, List<? extends Number> childList
            , BatchSimpleStmt childWrapper) {

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
            , GenericSimpleStmt simpleWrapper) {
        if (!parentRows.equals(childRows)) {
            throw createParentUpdateNotMatchException(parentRows, childRows, simpleWrapper);
        }
    }
}
