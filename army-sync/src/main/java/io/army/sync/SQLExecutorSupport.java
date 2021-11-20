package io.army.sync;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.boot.GenericSQLExecutorSupport;
import io.army.codec.StatementType;
import io.army.criteria.Selection;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.stmt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;


/**
 * This class is a base class of sql executor implementation. This class provide common method.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see InsertSQLExecutorIml
 * @see UpdateSQLExecutorImpl
 * @see SelectSQLExecutorImpl
 */
abstract class SQLExecutorSupport extends GenericSQLExecutorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(SQLExecutorSupport.class);

    final InnerGenericRmSessionFactory sessionFactory;

    final MappingContext mappingContext;

    SQLExecutorSupport(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.mappingContext = sessionFactory.dialect().mappingContext();
    }


    final Integer integerUpdate(PreparedStatement st, SimpleStmt sqlWrapper) {
        try {
            return st.executeUpdate();
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    final Long longUpdate(PreparedStatement st, SimpleStmt sqlWrapper) {
        try {
            return st.executeLargeUpdate();
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @return a unmodifiable list
     */
    final List<Integer> integerBatchUpdate(PreparedStatement st, BatchSimpleStmt sqlWrapper) {
        try {
            int[] batchResult;
            batchResult = st.executeBatch();
            List<Integer> resultList = new ArrayList<>(batchResult.length);
            final boolean hasVersion = sqlWrapper.hasVersion();
            final boolean insertStatement = sqlWrapper.statementType().insertStatement();
            for (int row : batchResult) {
                if (hasVersion && row < 1) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                if (insertStatement && row != 1) {
                    throw createValueInsertException(row, sqlWrapper);
                }
                resultList.add(row);
            }
            return resultList;
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    final List<Long> longBatchUpdate(PreparedStatement st, BatchSimpleStmt sqlWrapper) {
        try {
            long[] batchResult;
            batchResult = st.executeLargeBatch();
            List<Long> resultList = new ArrayList<>(batchResult.length);
            final boolean hasVersion = sqlWrapper.hasVersion();
            for (long row : batchResult) {
                if (hasVersion && row < 1L) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                resultList.add(row);
            }
            return resultList;
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @param executeFunction execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link #integerUpdate(PreparedStatement, SimpleStmt)}</li>
     *                          <li>{@link #longUpdate(PreparedStatement, SimpleStmt)}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Integer or Long}
     */
    final <N extends Number> N doExecuteUpdate(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , BiFunction<PreparedStatement, SimpleStmt, N> executeFunction) {
        //1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 2. set params
            bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup());
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            if (this.sessionFactory.showSQL()) {
                LOG.info("Army will execute {} sql:\n{}", sqlWrapper.statementType()
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            N updateRows;
            //4. execute update
            updateRows = executeFunction.apply(st, sqlWrapper);
            if (updateRows.longValue() < 1L && sqlWrapper.hasVersion()) {
                // 5. check optimistic lock
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @param executeFunction execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link #integerBatchUpdate(PreparedStatement, BatchSimpleStmt)}</li>
     *                          <li>{@link #longBatchUpdate(PreparedStatement, BatchSimpleStmt)}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return a unmodifiable list,{@code List<Integer> or List<Long>}
     */
    final <N extends Number> List<N> doExecuteBatch(InnerGenericRmSession session, BatchSimpleStmt sqlWrapper
            , BiFunction<PreparedStatement, BatchSimpleStmt, List<N>> executeFunction) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            StatementType statementType = sqlWrapper.statementType();
            for (List<ParamValue> paramList : sqlWrapper.groupList()) {
                // 2. bind param list
                bindParamList(st, statementType, paramList);
                st.addBatch();
            }
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            if (this.sessionFactory.showSQL()) {
                LOG.info("Army will execute {} sql:\n{}", sqlWrapper.statementType()
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            // 4. execute
            return Collections.unmodifiableList(executeFunction.apply(st, sqlWrapper));
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }


    final <T> List<T> doExecuteSimpleQuery(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<T> resultClass) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            // 2. set params
            bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup());
            if (this.sessionFactory.showSQL()) {
                LOG.info("Army will execute {} sql:\n{}", sqlWrapper.statementType()
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                //4. extract result
                if (singleType(sqlWrapper.selectionList(), resultClass)) {
                    resultList = extractSingleResultList(resultSet, sqlWrapper, resultClass);
                } else {
                    resultList = extractRowResultList(session, resultSet, sqlWrapper, resultClass);
                }
                //5.check Optimistic Lock
                if (sqlWrapper.hasVersion() && resultList.isEmpty()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }


    final <T> List<T> doExecuteReturning(InnerGenericRmSession session, Stmt stmt
            , Class<T> resultClass, boolean updateStatement, String methodName) {
        List<T> resultList;
        if (stmt instanceof SimpleStmt) {
            resultList = doExecuteSimpleQuery(session, (SimpleStmt) stmt, resultClass);
        } else if (stmt instanceof PairStmt) {
            final PairStmt childSQLWrapper = (PairStmt) stmt;
            final SimpleStmt firstWrapper = updateStatement
                    ? childSQLWrapper.childStmt()
                    : childSQLWrapper.parentStmt();

            final SimpleStmt secondWrapper = updateStatement
                    ? childSQLWrapper.parentStmt()
                    : childSQLWrapper.childStmt();


            final boolean onlyIdReturning = onlyIdReturning(childSQLWrapper.parentStmt()
                    , childSQLWrapper.childStmt());
            // 1. execute first sql
            Map<Object, ObjectWrapper> objectWrapperMap = doExecuteFirstSQLReturning(session, firstWrapper
                    , resultClass, onlyIdReturning);
            // 2. execute second sql
            resultList = doExecuteSecondSQLReturning(session, secondWrapper, resultClass, objectWrapperMap);
        } else {
            throw createUnSupportedSQLWrapperException(stmt, methodName);
        }
        return resultList;
    }


    /*################################## blow private method ##################################*/

    private Map<Object, ObjectWrapper> doExecuteFirstSQLReturning(InnerGenericRmSession session
            , SimpleStmt sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            //2. bind param list
            bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup());
            if (this.sessionFactory.showSQL()) {
                LOG.info("Army will execute {} sql:\n{}", sqlWrapper.statementType()
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            //3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                //4.extract first sql result
                return extractFirstSQLResult(session, resultSet, sqlWrapper, resultClass, onlyIdReturning);

            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    private <T> List<T> doExecuteSecondSQLReturning(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<T> resultClass, Map<Object, ObjectWrapper> objectWrapperMap) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            //2. bind param list
            bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup());
            if (this.sessionFactory.showSQL()) {
                LOG.info("Army will execute {} sql:\n{}", sqlWrapper.statementType()
                        , this.sessionFactory.dialect().showSQL(sqlWrapper));
            }
            //3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                //4.extract second sql result
                return extractSecondSQLResult(resultSet, objectWrapperMap, sqlWrapper, resultClass);

            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @return a unmodifiable map
     */
    private Map<Object, ObjectWrapper> extractFirstSQLResult(InnerGenericRmSession session, ResultSet resultSet
            , SimpleStmt sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) throws SQLException {

        final List<Selection> selectionList = sqlWrapper.selectionList();
        final Selection primaryFieldSelection = obtainPrimaryFieldForReturning(selectionList);

        Map<Object, ObjectWrapper> map = new HashMap<>();
        while (resultSet.next()) {
            ObjectWrapper objectWrapper = onlyIdReturning
                    ? ObjectAccessorFactory.forIdAccess(resultClass)
                    : createObjectWrapper(resultClass, session);

            for (Selection selection : selectionList) {
                Object columnResult = extractColumnResult(resultSet, selection, sqlWrapper.statementType()
                        , selection.mappingMeta().javaType());
                if (columnResult == null) {
                    continue;
                }
                // set columnResult to object
                objectWrapper.set(selection.alias(), columnResult);
            }
            Object idValue = objectWrapper.get(primaryFieldSelection.alias());
            if (idValue == null) {
                // first selection must be Primary Field
                throw createDomainFirstReturningNoIdException();
            }
            map.put(idValue, objectWrapper);
        }
        return Collections.unmodifiableMap(map);
    }

    private <T> List<T> extractSecondSQLResult(ResultSet resultSet, Map<Object, ObjectWrapper> objectWrapperMap
            , SimpleStmt sqlWrapper, Class<T> resultClass) throws SQLException {

        final List<Selection> selectionList = sqlWrapper.selectionList();
        final Selection primaryFieldSelection = obtainPrimaryFieldForReturning(selectionList);

        final StatementType statementType = sqlWrapper.statementType();

        List<T> resultList = new ArrayList<>(objectWrapperMap.size());
        final int size = selectionList.size();
        while (resultSet.next()) {
            ObjectWrapper objectWrapper = obtainFirstSQLObjectWrapper(resultSet, primaryFieldSelection
                    , statementType, objectWrapperMap);

            for (int i = 1; i < size; i++) {
                Selection selection = selectionList.get(i);
                Object columnResult = extractColumnResult(resultSet, selection, statementType
                        , selection.mappingMeta().javaType());
                if (columnResult == null) {
                    continue;
                }
                objectWrapper.set(selection.alias(), columnResult);
            }
            resultList.add(resultClass.cast(objectWrapper.getWrappedInstance()));
        }
        if (objectWrapperMap.size() != resultList.size()) {
            throw createChildReturningNotMatchException(objectWrapperMap.size(), resultList.size(), sqlWrapper);
        }
        return resultList;
    }

    private ObjectWrapper obtainFirstSQLObjectWrapper(ResultSet resultSet, Selection primaryFieldSelection
            , StatementType statementType, Map<Object, ObjectWrapper> objectWrapperMap) throws SQLException {

        final Object primaryFieldValue = extractColumnResult(resultSet, primaryFieldSelection, statementType
                , primaryFieldSelection.mappingMeta().javaType());

        if (primaryFieldValue == null) {
            throw createDomainSecondReturningNoIdException();
        }
        ObjectWrapper objectWrapper = objectWrapperMap.get(primaryFieldValue);
        if (objectWrapper == null) {
            throw new IllegalStateException(String.format(
                    "wrapperMap error,not found value for key[%s]", primaryFieldValue));
        }
        return objectWrapper;
    }


    private <T> List<T> extractSingleResultList(ResultSet resultSet, SimpleStmt sqlWrapper
            , Class<T> resultClass) throws SQLException {

        final Selection selection = sqlWrapper.selectionList().get(0);
        final StatementType statementType = sqlWrapper.statementType();

        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(
                    extractColumnResult(resultSet, selection, statementType, resultClass)
            );
        }
        return resultList;
    }

    private <T> List<T> extractRowResultList(InnerGenericRmSession session, ResultSet resultSet
            , SimpleStmt sqlWrapper, Class<T> resultClass) throws SQLException {

        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(
                    extractRowResult(session, resultSet, sqlWrapper, resultClass)
            );
        }
        return resultList;
    }


    private <T> T extractRowResult(InnerGenericRmSession session, ResultSet resultSet
            , SimpleStmt sqlWrapper, Class<T> resultClass) throws SQLException {

        final ObjectWrapper beanWrapper = createObjectWrapper(resultClass, session);

        final StatementType statementType = sqlWrapper.statementType();
        for (Selection selection : sqlWrapper.selectionList()) {
            // 1. obtain column result
            Object columnResult = extractColumnResult(resultSet, selection, statementType
                    , selection.mappingMeta().javaType());
            if (columnResult == null) {
                continue;
            }
            // 2. set bean property value.
            beanWrapper.set(selection.alias(), columnResult);
        }
        return getWrapperInstance(beanWrapper);
    }

    @Nullable
    private <T> T extractColumnResult(ResultSet resultSet, Selection selection, StatementType statementType
            , Class<T> columnClass) throws SQLException {
//
//        final MappingType mappingType = selection.mappingMeta();
//
//        Object columnResult = mappingType.nullSafeGet(resultSet, selection.alias(), this.mappingContext);
//        if (columnResult == null) {
//            return null;
//        }
//        if (!mappingType.javaType().isInstance(columnResult)) {
//            throw new MetaException("%s nullSafeGet return value isn't %s's instance."
//                    , mappingType.getClass().getName()
//                    , mappingType.javaType().getName());
//        }
//        if (selection instanceof FieldSelection) {
//            FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
//            if (fieldMeta.codec()) {
//                columnResult = doDecodeResult(statementType, fieldMeta, columnResult);
//            }
//        }
//        return columnClass.cast(columnResult);
        return null;
    }


    private void bindParamList(PreparedStatement st, StatementType statementType, List<ParamValue> paramList)
            throws SQLException {
//        ParamValue paramValue;
//        Object value;
//
//        final int size = paramList.size();
//        for (int i = 0; i < size; i++) {
//            paramValue = paramList.get(i);
//            ParamMeta paramMeta = paramValue.paramMeta();
//            value = paramValue.value();
//            if (value == null) {
//                st.setNull(i + 1, paramMeta.mappingMeta().jdbcType().getVendorTypeNumber());
//            } else {
//                if (paramMeta instanceof FieldMeta) {
//                    FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
//                    if (fieldMeta.codec()) {
//                        value = doEncodeParam(statementType, fieldMeta, value);
//                    }
//                }
//                // bind param
//                paramMeta.mappingMeta().nonNullSet(st, value, i + 1, this.mappingContext);
//            }
//        }

    }


}
