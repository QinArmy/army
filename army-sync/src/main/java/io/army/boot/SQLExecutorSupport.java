package io.army.boot;

import io.army.DomainUpdateException;
import io.army.ErrorCode;
import io.army.OptimisticLockException;
import io.army.beans.AccessorFactory;
import io.army.beans.BeanWrapper;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.modelgen.MetaConstant;
import io.army.wrapper.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

abstract class SQLExecutorSupport {

    final InnerSessionFactory sessionFactory;

    SQLExecutorSupport(InnerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    protected final int doExecuteUpdate(InnerSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            setParams(st, sqlWrapper.paramList());
            // 2. execute
            int updateRows;
            //3. execute update
            updateRows = st.executeUpdate();
            if (updateRows < 1 && sqlWrapper.hasVersion()) {
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final long doExecuteLargeUpdate(InnerSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            setParams(st, sqlWrapper.paramList());
            // 2. execute
            long updateRows;
            // 3. execute large update
            updateRows = st.executeLargeUpdate();
            if (updateRows < 1 && sqlWrapper.hasVersion()) {
                // 4. check optimistic lock
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final int[] doExecuteBatch(InnerSession session, BatchSimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {

            for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
                // 1. set params
                setParams(st, paramList);
                // 2. add to batch
                st.addBatch();
            }
            return st.executeBatch();
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final long[] doExecuteLargeBatch(InnerSession session, BatchSimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {

            for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
                // 1. set params
                setParams(st, paramList);
                // 2. add to batch
                st.addBatch();
            }
            return st.executeLargeBatch();
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteSimpleReturning(InnerSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                //4. extract result
                if (MetaConstant.SIMPLE_JAVA_TYPE_SET.contains(resultClass)) {
                    resultList = extractSimpleResult(resultSet, sqlWrapper.selectionList(), resultClass);
                } else {
                    resultList = extractResult(resultSet, sqlWrapper.selectionList(), resultClass);
                }
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteChildReturning(InnerSession session, ChildSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        Map<Object, BeanWrapper> beanWrapperMap;
        // firstly, execute child sql
        beanWrapperMap = doExecuteFirstReturning(session, sqlWrapper.childWrapper(), resultClass);
        // secondly, execute parent sql
        List<T> resultList;
        resultList = doExecuteSecondReturning(session, sqlWrapper.parentWrapper(), beanWrapperMap);
        if (beanWrapperMap.size() != resultList.size()) {
            throw createBatchNotMatchException(sqlWrapper.parentWrapper().sql(), sqlWrapper.childWrapper().sql()
                    , beanWrapperMap.size(), resultList.size());
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractSimpleResult(ResultSet resultSet, List<Selection> selectionList
            , Class<T> resultClass)
            throws SQLException {

        assertSimpleResult(selectionList, resultClass);
        final Selection selection = selectionList.get(0);
        FieldMeta<?, ?> fieldMeta = null;
        FieldCodec fieldCodec = null;
        if (selection instanceof FieldSelection) {
            fieldMeta = ((FieldSelection) selection).fieldMeta();
            fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
        }
        List<T> resultList = new ArrayList<>();
        final MappingMeta mappingMeta = selection.mappingMeta();
        while (resultSet.next()) {
            Object value = mappingMeta.nullSafeGet(resultSet, selection.alias());

            if (value != null && fieldCodec != null) {
                value = fieldCodec.decode(fieldMeta, value);
            }
            resultList.add((T) value);
        }
        return resultList;
    }

    protected final Map<Object, BeanWrapper> doExecuteFirstReturning(InnerSession session, SimpleSQLWrapper sqlWrapper
            , Class<?> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {

            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                Map<Object, BeanWrapper> wrapperMap;
                //4. extract first result
                wrapperMap = extractFirstSQLResult(resultSet, sqlWrapper.selectionList(), resultClass);
                if (wrapperMap.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return wrapperMap;
            }
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final <T> List<T> doExecuteSecondReturning(InnerSession session, SimpleSQLWrapper sqlWrapper
            , Map<Object, BeanWrapper> wrapperMap) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                // 4. extract second result
                resultList = extractSecondSQLResult(resultSet, sqlWrapper.selectionList(), wrapperMap);
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final Map<Object, BeanWrapper> extractFirstSQLResult(ResultSet resultSet, List<Selection> selectionList
            , Class<?> resultClass) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList);

        Map<Object, BeanWrapper> map = new HashMap<>();
        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = AccessorFactory.forBeanPropertyAccess(resultClass);
            // extract one row
            extractRow(resultSet, selectionList, beanWrapper);
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
    protected final <T> List<T> extractSecondSQLResult(ResultSet resultSet, List<Selection> selectionList
            , Map<Object, BeanWrapper> wrapperMap) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList);
        List<Selection> subSelectionList;
        if (selectionList.size() == 1) {
            subSelectionList = Collections.emptyList();
        } else {
            subSelectionList = selectionList.subList(1, selectionList.size());
        }
        List<T> resultList = new ArrayList<>(wrapperMap.size());
        while (resultSet.next()) {
            Object idValue = primaryField.mappingMeta().nullSafeGet(resultSet, primaryField.alias());
            BeanWrapper beanWrapper = wrapperMap.get(idValue);
            if (beanWrapper == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Domain returning insert/update/delete criteria error,not found BeanWrapper by id[%s]"
                        , idValue);
            }
            extractRow(resultSet, subSelectionList, beanWrapper);
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }


    protected final void setParams(PreparedStatement st, List<ParamWrapper> paramList) throws SQLException {
        ParamWrapper paramWrapper;
        Object value;

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            paramWrapper = paramList.get(i);
            value = paramWrapper.value();
            if (value == null) {
                st.setNull(i + 1, paramWrapper.paramMeta().mappingMeta().jdbcType().getVendorTypeNumber());
            } else {
                setNonNullValue(st, i + 1, paramWrapper.paramMeta(), value);
            }
        }

    }

    protected final void setNonNullValue(PreparedStatement st, final int index, ParamMeta paramMeta
            , final Object value)
            throws SQLException {
        Object paramValue = value;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
            if (fieldCodec != null) {
                // encode param
                paramValue = fieldCodec.encode(fieldMeta, value);
                if (!fieldMeta.javaType().isInstance(paramValue)) {
                    throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, paramValue);
                }
            }
        }
        // set param
        paramMeta.mappingMeta().nonNullSet(st, paramValue, index);
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractResult(ResultSet resultSet, List<Selection> selectionList
            , Class<T> resultClass) throws SQLException {

        List<T> resultList = new ArrayList<>();
        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = AccessorFactory.forBeanPropertyAccess(resultClass);
            extractRow(resultSet, selectionList, beanWrapper);
            // result add to resultList
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }

    protected final void extractRow(ResultSet resultSet, List<Selection> selectionList, BeanWrapper beanWrapper)
            throws SQLException {

        for (Selection selection : selectionList) {
            Object value = selection.mappingMeta().nullSafeGet(resultSet, selection.alias());
            if (value == null) {
                continue;
            }
            if (selection instanceof FieldSelection) {
                FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
                FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec != null) {
                    // do decode
                    value = fieldCodec.decode(fieldMeta, value);
                    if (!fieldMeta.javaType().isInstance(value)) {
                        throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, value);
                    }
                }
            }
            beanWrapper.setPropertyValue(selection.alias(), value);
        }
    }



    /*################################## blow private method ##################################*/

    private PrimaryFieldMeta<?, ?> obtainPrimaryField(List<Selection> selectionList) {
        PrimaryFieldMeta<?, ?> primaryField;
        Selection selection = selectionList.get(0);
        if (selection instanceof PrimaryFieldMeta) {
            primaryField = (PrimaryFieldMeta<?, ?>) selection;
        } else {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "Domain update/insert,first selection must be PrimaryFieldMeta");
        }
        return primaryField;
    }

    /*################################## blow package static method ##################################*/

    static IllegalArgumentException createNotSupportedException(SQLWrapper sqlWrapper, String methodName) {
        return new IllegalArgumentException(String.format("%s supported by %s", sqlWrapper, methodName));
    }

    static DomainUpdateException createBatchNotMatchException(String parentSql, String childSql
            , int parentRowsLength, int childRowsLength) {
        throw new DomainUpdateException(
                "Domain update,parent sql[%s] update batch[%s] and child sql[%s] update batch[%s] not match."
                , parentSql
                , parentRowsLength
                , childSql
                , childRowsLength
        );
    }


    static String[] asSelectionAliasArray(List<Selection> selectionList) {
        final int size = selectionList.size();
        String[] aliasArray = new String[size];

        for (int i = 0; i < size; i++) {
            aliasArray[i] = selectionList.get(i).alias();
        }
        return aliasArray;
    }


    static OptimisticLockException createOptimisticLockException(String sql) {
        return new OptimisticLockException("record maybe be updated or deleted by transaction,sql:%s", sql);
    }

    /*################################## blow private static method ##################################*/

    private static MetaException createFieldCodecException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException("FieldMeta[%s] not found FieldCodec.", fieldMeta);
    }

    private static void assertSimpleResult(List<Selection> selectionList, Class<?> resultClass) {
        if (selectionList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size must be one,when resultClass is simple java class");
        }
        Selection selection = selectionList.get(0);
        if (selection.mappingMeta().javaType() != resultClass) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection's MappingMeta[%s] and  resultClass[%s] not match."
                    , selection.mappingMeta(), resultClass);
        }
    }

}
