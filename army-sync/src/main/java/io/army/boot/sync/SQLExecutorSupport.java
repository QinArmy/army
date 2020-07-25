package io.army.boot.sync;

import io.army.DomainUpdateException;
import io.army.ErrorCode;
import io.army.OptimisticLockException;
import io.army.beans.BeanWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.boot.ExecutorUtils;
import io.army.codec.CodecContext;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.dialect.MappingContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
abstract class SQLExecutorSupport {

    final InnerSyncSessionFactory sessionFactory;


    final MappingContext mappingContext;

    SQLExecutorSupport(InnerSyncSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.mappingContext = sessionFactory.dialect().mappingContext();
    }


    protected final int doExecuteUpdate(InnerSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            setParams(session.codecContext(), st, sqlWrapper.paramList());
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
            setParams(session.codecContext(), st, sqlWrapper.paramList());
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

    /**
     * @return a unmodifiable map, key : key of {@linkplain BatchSimpleSQLWrapper#paramGroupList()}
     * * ,value : batch update rows of named param.
     */
    protected final Map<Integer, Integer> doExecuteBatch(InnerSession session, BatchSimpleSQLWrapper sqlWrapper) {

        CodecContext codecContext = session.codecContext();
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {

            Map<Integer, Integer> indexMap = new HashMap<>();
            int index = 0;
            for (Map.Entry<Integer, List<ParamWrapper>> e : sqlWrapper.paramGroupList().entrySet()) {
                // 1. set params
                setParams(codecContext, st, e.getValue());
                // 2. add to batch
                st.addBatch();
                // 3. cache param's index and result's index map.
                indexMap.put(e.getKey(), index);
                index++;
            }
            int[] resultArray;
            // 4. execute batch sql.
            resultArray = st.executeBatch();
            Map<Integer, Integer> batchResultMap = new HashMap<>();
            // 5. convert result array to map.
            final boolean version = sqlWrapper.hasVersion();
            for (Integer paramIndex : sqlWrapper.paramGroupList().keySet()) {
                int updateRows = resultArray[indexMap.get(paramIndex)];
                batchResultMap.put(paramIndex, updateRows);
                if (version && updateRows < 1) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
            }
            return Collections.unmodifiableMap(batchResultMap);
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    /**
     * @return a unmodifiable map, key : key of {@linkplain BatchSimpleSQLWrapper#paramGroupList()}
     * * ,value : batch update rows of named param.
     */
    protected final Map<Integer, Long> doExecuteLargeBatch(InnerSession session, BatchSimpleSQLWrapper sqlWrapper) {

        CodecContext codecContext = session.codecContext();
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {

            Map<Integer, Integer> indexMap = new HashMap<>();
            int index = 0;
            for (Map.Entry<Integer, List<ParamWrapper>> e : sqlWrapper.paramGroupList().entrySet()) {
                // 1. set params
                setParams(codecContext, st, e.getValue());
                // 2. add to batch
                st.addBatch();
                // 3. cache param's index and result's index map.
                indexMap.put(e.getKey(), index);
                index++;
            }
            long[] resultArray;
            // 4. execute batch sql.
            resultArray = st.executeLargeBatch();
            Map<Integer, Long> batchResultMap = new HashMap<>();
            // 5. convert result array to map.
            final boolean version = sqlWrapper.hasVersion();
            for (Integer paramIndex : sqlWrapper.paramGroupList().keySet()) {
                long updateRows = resultArray[indexMap.get(paramIndex)];
                batchResultMap.put(paramIndex, updateRows);
                if (version && updateRows < 1L) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
            }
            return Collections.unmodifiableMap(batchResultMap);
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
            setParams(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                //4. extract result
                if (simpleJavaType(sqlWrapper.selectionList(), resultClass)) {
                    resultList = extractSimpleTypeResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                } else {
                    resultList = extractBeanTypeResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                }
                //5.check Optimistic Lock
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
            throw createBatchNotMatchException(this.sessionFactory.name(), sqlWrapper.parentWrapper().sql()
                    , sqlWrapper.childWrapper().sql()
                    , beanWrapperMap.size(), resultList.size());
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractSimpleTypeResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Class<T> resultClass) throws SQLException {

        assertSimpleResult(selectionList, resultClass);
        final Selection selection = selectionList.get(0);
        FieldMeta<?, ?> fieldMeta = null;
        FieldCodec fieldCodec = null;
        if (selection instanceof FieldSelection) {
            fieldMeta = ((FieldSelection) selection).fieldMeta();
            if (fieldMeta.codec()) {
                fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec == null) {
                    throw createFieldCodecException(fieldMeta);
                }
            }

        }
        List<T> resultList = new ArrayList<>();
        final MappingMeta mappingMeta = selection.mappingMeta();
        while (resultSet.next()) {
            Object value = mappingMeta.nullSafeGet(resultSet, selection.alias(), this.mappingContext);

            if (value != null && fieldCodec != null) {
                value = fieldCodec.decode(fieldMeta, value, codecContext);
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
            setParams(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                Map<Object, BeanWrapper> wrapperMap;
                //4. extract first result
                wrapperMap = extractFirstSQLResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                        , resultClass);
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
            setParams(session.codecContext(), st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                // 4. extract second result
                resultList = extractSecondSQLResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                        , wrapperMap);
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    protected final Map<Object, BeanWrapper> extractFirstSQLResult(CodecContext codecContext, ResultSet resultSet
            , List<Selection> selectionList, Class<?> resultClass) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList);

        Map<Object, BeanWrapper> map = new HashMap<>();
        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = ObjectAccessorFactory.forBeanPropertyAccess(resultClass);
            // extract one row
            extractRow(codecContext, resultSet, selectionList, beanWrapper);
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
            , List<Selection> selectionList, Map<Object, BeanWrapper> wrapperMap) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList);
        List<Selection> subSelectionList;
        if (selectionList.size() == 1) {
            subSelectionList = Collections.emptyList();
        } else {
            subSelectionList = selectionList.subList(1, selectionList.size());
        }
        List<T> resultList = new ArrayList<>(wrapperMap.size());
        while (resultSet.next()) {
            Object idValue = primaryField.mappingMeta().nullSafeGet(resultSet, primaryField.alias(), this.mappingContext);
            BeanWrapper beanWrapper = wrapperMap.get(idValue);
            if (beanWrapper == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Domain returning insert/update/delete criteria error,not found BeanWrapper by id[%s]"
                        , idValue);
            }
            extractRow(codecContext, resultSet, subSelectionList, beanWrapper);
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }


    protected final void setParams(CodecContext codecContext, PreparedStatement st, List<ParamWrapper> paramList)
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

        List<T> resultList = new ArrayList<>();
        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = ObjectAccessorFactory.forBeanPropertyAccess(resultClass);
            extractRow(codecContext, resultSet, selectionList, beanWrapper);
            // result add to resultList
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }

    protected final void extractRow(CodecContext codecContext, ResultSet resultSet, List<Selection> selectionList
            , BeanWrapper beanWrapper)
            throws SQLException {

        for (Selection selection : selectionList) {
            Object value = selection.mappingMeta().nullSafeGet(resultSet, selection.alias(), this.mappingContext);
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



    /*################################## blow private method ##################################*/

    private Object doEncodeParam(CodecContext codecContext, FieldMeta<?, ?> fieldMeta, final Object value) {
        FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
        Object paramValue;
        if (fieldCodec == null) {
            throw createFieldCodecException(fieldMeta);
        } else {
            // encode param
            paramValue = fieldCodec.encode(fieldMeta, value, codecContext);
            if (!fieldMeta.javaType().isInstance(paramValue)) {
                throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, paramValue);
            }
        }
        return paramValue;
    }

    private Object doDecodeResult(CodecContext codecContext, FieldMeta<?, ?> fieldMeta, final Object resultFromDB) {
        FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
        Object result;
        if (fieldCodec == null) {
            throw createFieldCodecException(fieldMeta);
        } else {
            // decode result
            result = fieldCodec.decode(fieldMeta, resultFromDB, codecContext);
            if (!fieldMeta.javaType().isInstance(result)) {
                throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, result);
            }
        }
        return result;
    }

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

    static boolean simpleJavaType(List<Selection> selectionList, Class<?> resultClass) {
        return selectionList.size() == 1
                && resultClass.isAssignableFrom(selectionList.get(0).mappingMeta().javaType());

    }

    static IllegalArgumentException createNotSupportedException(SQLWrapper sqlWrapper, String methodName) {
        return new IllegalArgumentException(String.format("%s supported by %s", sqlWrapper, methodName));
    }

    static DomainUpdateException createBatchNotMatchException(String factoryNam, String parentSql, String childSql
            , int parentRowsLength, int childRowsLength) {
        throw new DomainUpdateException(
                "SessionFactory[%s] Domain update,parent sql[%s] update batch[%s] " +
                        "and child sql[%s] update batch[%s] not match."
                , factoryNam
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


    OptimisticLockException createOptimisticLockException(String sql) {
        return new OptimisticLockException(
                "SessionFactory[%s] record maybe be updated or deleted by transaction,sql:%s"
                , this.sessionFactory.name(), sql);
    }

    /*################################## blow private static method ##################################*/

    private static MetaException createFieldCodecException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException("FieldMeta[%s] not found FieldCodec.", fieldMeta);
    }

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

}
