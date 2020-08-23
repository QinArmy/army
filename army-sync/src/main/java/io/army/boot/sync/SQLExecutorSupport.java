package io.army.boot.sync;

import io.army.DataAccessException;
import io.army.DomainUpdateException;
import io.army.ErrorCode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.boot.GenericSQLExecutorSupport;
import io.army.codec.CodecContext;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.dialect.MappingContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.meta.mapping.ResultColumnMeta;
import io.army.wrapper.BatchSimpleSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.sql.*;
import java.util.*;


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

    final InnerGenericRmSessionFactory sessionFactory;


    final MappingContext mappingContext;

    SQLExecutorSupport(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.mappingContext = sessionFactory.dialect().mappingContext();
    }


    protected final int doExecuteUpdate(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            bindParamList(session.codecContext(), st, sqlWrapper.paramList());
            // 2. execute
            int updateRows;
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            //4. execute update
            updateRows = st.executeUpdate();
            if (updateRows < 1 && sqlWrapper.hasVersion()) {
                // 5. check optimistic lock
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final long doExecuteLargeUpdate(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            bindParamList(session.codecContext(), st, sqlWrapper.paramList());
            // 2. execute
            long updateRows;
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            // 4. execute large update
            updateRows = st.executeLargeUpdate();
            if (updateRows < 1 && sqlWrapper.hasVersion()) {
                // 5. check optimistic lock
                throw createOptimisticLockException(this.sessionFactory, sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @return a unmodifiable map, key : key of {@linkplain BatchSimpleSQLWrapper#paramGroupList()}
     * * ,value : batch update rows of named param.
     */
    protected final int[] doExecuteBatch(InnerGenericRmSession session, BatchSimpleSQLWrapper sqlWrapper) {
        if (session.supportSharding()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "Army don't support batch operation in sharding mode.");
        }

        final CodecContext codecContext = session.codecContext();
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            for (List<ParamWrapper> paramWrapperList : sqlWrapper.paramGroupList()) {
                // 1. set params
                bindParamList(codecContext, st, paramWrapperList);
                // 2. add to batch
                st.addBatch();
            }
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            // 4. execute batch sql.
            return st.executeBatch();
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @return a unmodifiable map, key : key of {@linkplain BatchSimpleSQLWrapper#paramGroupList()}
     * * ,value : batch update rows of named param.
     */
    protected final long[] doExecuteLargeBatch(InnerGenericRmSession session, BatchSimpleSQLWrapper sqlWrapper) {
        if (session.supportSharding()) {
            throw new CriteriaException("Army don't support batch operation in sharding mode.");
        }
        final CodecContext codecContext = session.codecContext();
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            for (List<ParamWrapper> paramWrapperList : sqlWrapper.paramGroupList()) {
                // 1. set params
                bindParamList(codecContext, st, paramWrapperList);
                // 2. add to batch
                st.addBatch();
            }
            // 3. set timeout
            int timeout = session.timeToLiveInSeconds();
            if (timeout >= 0) {
                st.setQueryTimeout(timeout);
            }
            // 4. execute batch sql.
            return st.executeLargeBatch();
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteSimpleReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            // 2. set params
            bindParamList(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                //4. extract result
                if (singleType(sqlWrapper.selectionList(), resultClass)) {
                    resultList = extractSingleResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                } else {
                    resultList = extractBeanTypeResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                }
                //5.check Optimistic Lock
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(this.sessionFactory, sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        } catch (ResultColumnClassNotFoundException e) {
            throw createExceptionForResultColumnClassNotFound(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteChildReturning(InnerGenericRmSession session, ChildSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        Map<Object, ObjectWrapper> beanWrapperMap;
        // firstly, execute child sql
        beanWrapperMap = doExecuteFirstReturning(session, sqlWrapper.childWrapper(), resultClass);
        // secondly, execute parent sql
        List<T> resultList;
        resultList = doExecuteSecondReturning(session, sqlWrapper.parentWrapper(), beanWrapperMap);
        if (beanWrapperMap.size() != resultList.size()) {
            throw createBatchNotMatchException(sqlWrapper.parentWrapper().sql()
                    , sqlWrapper.childWrapper().sql()
                    , beanWrapperMap.size(), resultList.size());
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractSingleResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Class<T> resultClass) throws SQLException
            , ResultColumnClassNotFoundException {

        assertSimpleResult(selectionList, resultClass);
        final Selection selection = selectionList.get(0);
        FieldMeta<?, ?> fieldMeta = null;
        FieldCodec fieldCodec = null;
        if (selection instanceof FieldSelection) {
            fieldMeta = ((FieldSelection) selection).fieldMeta();
            if (fieldMeta.codec()) {
                fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec == null) {
                    throw createNoFieldCodecException(fieldMeta);
                }
            }

        }
        List<T> resultList = new ArrayList<>();
        final MappingMeta mappingMeta = selection.mappingMeta();
        final ResultColumnMeta columnMeta = extractResultRowMeta(resultSet.getMetaData(), selectionList).get(0);

        while (resultSet.next()) {
            Object value = mappingMeta.nullSafeGet(resultSet, selection.alias(), columnMeta, this.mappingContext);

            if (value != null && fieldCodec != null) {
                value = fieldCodec.decode(fieldMeta, value, codecContext);
            }
            resultList.add((T) value);
        }
        return resultList;
    }


    protected final Map<Object, ObjectWrapper> doExecuteFirstReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<?> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {

            // 2. set params
            bindParamList(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                Map<Object, ObjectWrapper> wrapperMap;
                //4. extract first result
                wrapperMap = extractFirstSQLResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                        , resultClass);
                if (wrapperMap.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(this.sessionFactory, sqlWrapper.sql());
                }
                return wrapperMap;
            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        } catch (ResultColumnClassNotFoundException e) {
            throw createExceptionForResultColumnClassNotFound(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteSecondReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Map<Object, ObjectWrapper> wrapperMap) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            // 2. set params
            bindParamList(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                // 4. extract second result
                resultList = extractSecondSQLResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                        , wrapperMap);
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(this.sessionFactory, sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw convertSQLException(e, sqlWrapper.sql());
        } catch (ResultColumnClassNotFoundException e) {
            throw createExceptionForResultColumnClassNotFound(e, sqlWrapper.sql());
        }
    }

    protected final Map<Object, ObjectWrapper> extractFirstSQLResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Class<?> resultClass) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryFieldForReturning(selectionList);
        final List<ResultColumnMeta> columnMetaList = extractResultRowMeta(resultSet.getMetaData(), selectionList);

        Map<Object, ObjectWrapper> map = new HashMap<>();
        ObjectWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = ObjectAccessorFactory.forBeanPropertyAccess(resultClass);
            // extract one row
            extractRowForBean(codecContext, resultSet, selectionList, columnMetaList, beanWrapper);
            Object idValue;
            if (beanWrapper.isReadableProperty(primaryField.alias())) {
                idValue = beanWrapper.getPropertyValue(TableMeta.ID);
                if (idValue == null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "Domain returning insert/update/delete id value is null.");
                }
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Domain returning  insert/update/delete must have id property.");
            }
            // result add to result map
            if (map.putIfAbsent(idValue, beanWrapper) != null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Domain returning  insert/update/delete duplication row.");
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractSecondSQLResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Map<Object, ObjectWrapper> wrapperMap) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryFieldForReturning(selectionList);
        final List<ResultColumnMeta> columnMetaList = extractResultRowMeta(resultSet.getMetaData(), selectionList);

        List<Selection> subSelectionList;
        List<ResultColumnMeta> subColumnMetaList;
        if (selectionList.size() == 1) {
            subSelectionList = Collections.emptyList();
            subColumnMetaList = Collections.emptyList();
        } else {
            subSelectionList = selectionList.subList(1, selectionList.size());
            subColumnMetaList = columnMetaList.subList(1, columnMetaList.size());
        }

        List<T> resultList = new ArrayList<>(wrapperMap.size());
        while (resultSet.next()) {
            Object idValue = primaryField.mappingMeta().nullSafeGet(resultSet, primaryField.alias()
                    , columnMetaList.get(0), this.mappingContext);

            ObjectWrapper beanWrapper = wrapperMap.get(idValue);
            if (beanWrapper == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Domain returning insert/update/delete criteria error,not found ObjectWrapper by id[%s]"
                        , idValue);
            }
            if (!subSelectionList.isEmpty()) {
                extractRowForBean(codecContext, resultSet, subSelectionList, subColumnMetaList, beanWrapper);
            }
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }


    protected final void bindParamList(CodecContext codecContext, PreparedStatement st, List<ParamWrapper> paramList)
            throws SQLException {
        ParamWrapper paramWrapper;
        Object value;

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            paramWrapper = paramList.get(i);
            value = paramWrapper.value();
            if (value == null) {
                st.setNull(i + 1, paramWrapper.paramMeta().mappingMeta().jdbcType().getVendorTypeNumber());
            } else {
                setNonNullValue(codecContext, st, i + 1, paramWrapper.paramMeta(), value);
            }
        }

    }

    protected final void setNonNullValue(CodecContext codecContext, PreparedStatement st, final int index
            , ParamMeta paramMeta, final Object value)
            throws SQLException {
        Object paramValue = value;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            if (fieldMeta.codec()) {
                paramValue = doEncodeParam(codecContext, fieldMeta, value);
            }
        }
        // set param
        paramMeta.mappingMeta().nonNullSet(st, paramValue, index, this.mappingContext);
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractBeanTypeResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Class<T> resultClass) throws SQLException {

        final List<ResultColumnMeta> columnMetaList = extractResultRowMeta(resultSet.getMetaData(), selectionList);

        List<T> resultList = new ArrayList<>();
        ObjectWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = ObjectAccessorFactory.forBeanPropertyAccess(resultClass);
            extractRowForBean(codecContext, resultSet, selectionList, columnMetaList, beanWrapper);
            // result add to resultList
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }

    protected final void extractRowForBean(CodecContext codecContext, ResultSet resultSet, List<Selection> selectionList
            , List<ResultColumnMeta> columnMetaList, ObjectWrapper beanWrapper)
            throws SQLException {

        final int size = columnMetaList.size();

        for (int i = 0; i < size; i++) {
            Selection selection = selectionList.get(i);

            Object value = selection.mappingMeta().nullSafeGet(resultSet, selection.alias()
                    , columnMetaList.get(i), this.mappingContext);
            if (value == null) {
                continue;
            }
            if (selection instanceof FieldSelection) {
                FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
                if (fieldMeta.codec()) {
                    value = doDecodeResult(codecContext, fieldMeta, value);
                }
            }
            beanWrapper.setPropertyValue(selection.alias(), value);
        }

    }

    /*################################## blow package method ##################################*/


    final DomainUpdateException createBatchNotMatchException(String parentSql, String childSql
            , int parentRowsLength, int childRowsLength) {
        throw new DomainUpdateException(
                "%s Domain update,parent sql[%s] update batch[%s] " +
                        "and child sql[%s] update batch[%s] not match."
                , this.sessionFactory
                , parentSql
                , parentRowsLength
                , childSql
                , childRowsLength
        );
    }

    static void assertParamGroupListSizeMatch(BatchSimpleSQLWrapper parentWrapper
            , BatchSimpleSQLWrapper childWrapper) {

        if (parentWrapper.paramGroupList().size() != childWrapper.paramGroupList().size()) {
            throw new CriteriaException(ErrorCode.CACHE_ERROR
                    , "Child update/delete sql [%s] and [%s], paramGroupList %s and %s not  match."
                    , parentWrapper.sql(), childWrapper.sql()
                    , parentWrapper.paramGroupList().size(), childWrapper.paramGroupList().size());
        }
    }


    /*################################## blow private method ##################################*/


    /**
     * @return a unmodifiable list
     */
    private List<ResultColumnMeta> extractResultRowMeta(ResultSetMetaData resultSetMetaData
            , List<Selection> selectionList) throws SQLException {
        final int size = selectionList.size();
        List<ResultColumnMeta> columnMetaList = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            JDBCType jdbcType = JDBCType.valueOf(resultSetMetaData.getColumnType(i));
            String sqlType = resultSetMetaData.getColumnTypeName(i);
            String javaClassName = resultSetMetaData.getColumnClassName(i);

            columnMetaList.add(new ResultColumnMetaImpl(jdbcType, sqlType, javaClassName));
        }
        return Collections.unmodifiableList(columnMetaList);
    }

    /*################################## blow package static method ##################################*/





    static String[] asSelectionAliasArray(List<Selection> selectionList) {
        final int size = selectionList.size();
        String[] aliasArray = new String[size];

        for (int i = 0; i < size; i++) {
            aliasArray[i] = selectionList.get(i).alias();
        }
        return aliasArray;
    }

    static DataAccessException createExceptionForResultColumnClassNotFound(
            ResultColumnClassNotFoundException e, String sql) {
        return new DataAccessException(ErrorCode.ACCESS_ERROR, e
                , "Class[%s] not found when extract ResultSetMeta with SQL[%s].", e.getClassName(), sql);
    }

    /*################################## blow private static method ##################################*/


    private static void assertSimpleResult(List<Selection> selectionList, Class<?> resultClass) {
        if (selectionList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size must be one but %s,when resultClass is simple java class"
                    , selectionList.size());
        }
        Selection selection = selectionList.get(0);
        if (selection.mappingMeta().javaType() != resultClass) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection's MappingMeta[%s] and  resultClass[%s] not match."
                    , selection.mappingMeta(), resultClass);
        }
    }


    private static final class ResultColumnMetaImpl implements ResultColumnMeta {

        private final JDBCType jdbcType;

        private final String sqlType;

        private final String javaClassName;

        private ResultColumnMetaImpl(JDBCType jdbcType, String sqlType, String javaClassName) {
            this.jdbcType = jdbcType;
            this.sqlType = sqlType;
            this.javaClassName = javaClassName;
        }

        @Override
        public final JDBCType jdbcType() {
            return this.jdbcType;
        }

        @Override
        public final String sqlType() {
            return this.sqlType;
        }

        @Override
        public String javaClassName() {
            return this.javaClassName;
        }

    }


    static final class ResultColumnClassNotFoundException extends RuntimeException {

        private ResultColumnClassNotFoundException(String className, Throwable cause) {
            super(className, cause);
        }

        String getClassName() {
            return getMessage();
        }
    }

}
