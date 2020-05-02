package io.army.boot;

import io.army.*;
import io.army.beans.BeanWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.codec.FieldCodec;
import io.army.dialect.Dialect;
import io.army.dialect.InsertException;
import io.army.generator.FieldGenerator;
import io.army.generator.GeneratorException;
import io.army.generator.PostFieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class InsertSQLExecutorIml implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);

    static final InsertSQLExecutorIml INSTANCE = new InsertSQLExecutorIml();


    private static class GeneratorWrapper {

        private final FieldMeta<?, ?> fieldMeta;

        private final PostFieldGenerator postGenerator;

        GeneratorWrapper(FieldMeta<?, ?> fieldMeta, PostFieldGenerator postGenerator) {
            this.fieldMeta = fieldMeta;
            this.postGenerator = postGenerator;
        }
    }

    private InsertSQLExecutorIml() {
    }

    @Override
    public final List<Integer> insert(InnerSession session, List<SQLWrapper> sqlWrapperList)
            throws InsertException {

        List<Integer> sqlWrapperUpdateRowList = new ArrayList<>(sqlWrapperList.size());

        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof ChildSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteChild(session, (ChildSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof DomainSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteDomain(session, (DomainSQLWrapper) sqlWrapper)
                );
            } else {
                sqlWrapperUpdateRowList.add(
                        doExecute(session, Collections.emptyMap(), null, sqlWrapper.sql()
                                , sqlWrapper.paramList(), null)
                );
            }
        }
        return Collections.unmodifiableList(sqlWrapperUpdateRowList);
    }

    @Override
    public final List<Integer> batchInsert(InnerSession session, List<DomainBatchSQLWrapper> batchSQLWrapperList) {
        List<Integer> sqlWrapperInsertRowList = new ArrayList<>(batchSQLWrapperList.size());

        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();

        for (DomainBatchSQLWrapper sqlWrapper : batchSQLWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteChildBatch(session, (ChildBatchSQLWrapper) sqlWrapper)
                );
            } else {
                sqlWrapperInsertRowList.add(
                        doExecuteSimpleBatch(session, sqlWrapper)
                );
            }
        }
        return Collections.unmodifiableList(sqlWrapperInsertRowList);
    }

    /*################################## blow private method ##################################*/

    private int doExecuteDomain(InnerSession session, DomainSQLWrapper sqlWrapper) {
        TableMeta<?> tableMeta = sqlWrapper.domainWrapper().tableMeta();
        Map<FieldMeta<?, ?>, FieldCodec> codecMap = session.sessionFactory().fieldCodecMap(tableMeta);
        GeneratorWrapper generatorWrapper = obtainAutoGeneratorWrapper(session, tableMeta);

        int rows;
        rows = doExecute(session, codecMap, generatorWrapper, sqlWrapper.sql(), sqlWrapper.paramList()
                , sqlWrapper.domainWrapper());
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR, "TableMeta[%s] insert rows[%s] error.", tableMeta, rows);
        }
        return rows;
    }

    private int doExecuteChild(InnerSession session, ChildSQLWrapper childSQLWrapper) {

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) childSQLWrapper.domainWrapper().tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        Map<FieldMeta<?, ?>, FieldCodec> codecMap;
        int childRows, parentRows;

        // firstly, execute parent insert sql
        codecMap = session.sessionFactory().fieldCodecMap(parentMeta);
        final GeneratorWrapper generatorWrapper = obtainAutoGeneratorWrapper(session, parentMeta);

        parentRows = doExecute(session, codecMap, generatorWrapper, childSQLWrapper.parentSql()
                , childSQLWrapper.parentParamList(), childSQLWrapper.domainWrapper());

        // secondly, execute child insert sql
        codecMap = session.sessionFactory().fieldCodecMap(childMeta);

        childRows = doExecute(session, codecMap, null, childSQLWrapper.sql()
                , childSQLWrapper.paramList(), childSQLWrapper.domainWrapper());

        if (parentRows != childRows || parentRows != 1) {
            throw new InsertRowsNotMatchException("ChildMeta[%s] insert rows[%s] and ParentMeta[%s] rows[%s] not match."
                    , childMeta, childRows, parentMeta, parentRows);
        }

        return childRows;
    }

    private int doExecuteSimpleBatch(InnerSession session, DomainBatchSQLWrapper sqlWrapper) {
        Map<FieldMeta<?, ?>, FieldCodec> codecMap = session.sessionFactory().fieldCodecMap(sqlWrapper.tableMeta());
        GeneratorWrapper generatorWrapper = obtainAutoGeneratorWrapper(session, sqlWrapper.tableMeta());

        int[] domainRows;
        domainRows = doExecuteBatch(session, codecMap, generatorWrapper
                , sqlWrapper.sql(), sqlWrapper.paramGroupList(), sqlWrapper.domainWrapperList()
        );
        int totalRow = 0, row;

        for (int i = 0; i < domainRows.length; i++) {
            row = domainRows[i];
            if (row != 1) {
                throw new InsertRowsNotMatchException(
                        "TableMeta[%s] insert index[%s] actual row count[%s] not 1 .", sqlWrapper.tableMeta(), i, row);
            }
            totalRow += row;
        }
        return totalRow;
    }

    private int doExecuteChildBatch(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {
        final ChildTableMeta<?> childMeta = sqlWrapper.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final Map<FieldMeta<?, ?>, FieldCodec> parentCodecMap = session.sessionFactory().fieldCodecMap(parentMeta);
        final GeneratorWrapper generatorWrapper = obtainAutoGeneratorWrapper(session, parentMeta);

        int[] parentRows, childRows;
        // firstly, parent insert sql
        parentRows = doExecuteBatch(session, parentCodecMap, generatorWrapper
                , sqlWrapper.parentSql(), sqlWrapper.parentParamGroupList(), sqlWrapper.domainWrapperList());

        // secondly,child insert sql
        final Map<FieldMeta<?, ?>, FieldCodec> childCodecMap = session.sessionFactory().fieldCodecMap(childMeta);
        childRows = doExecuteBatch(session, childCodecMap, null
                , sqlWrapper.sql(), sqlWrapper.paramGroupList(), sqlWrapper.domainWrapperList());

        if (parentRows.length != childRows.length) {
            throw new InsertRowsNotMatchException(
                    "ChildMeta[%s] insert batch count[%s] and ParentMeta[%s] batch count[%s] not match."
                    , childMeta, childRows.length, parentMeta, parentRows.length);
        }
        int totalRows = 0, parentRow;
        for (int i = 0; i < parentRows.length; i++) {
            parentRow = parentRows[i];
            if (parentRow != childRows[i] || parentRow != 1) {
                throw new InsertRowsNotMatchException(
                        "ChildMeta[%s] insert batch[%s] rows[%s] and ParentMeta[%s] batch[%s] rows[%s] not match."
                        , childMeta, i, childRows[i], parentMeta, i, parentRows[i]);
            }
            totalRows += parentRow;
        }
        return totalRows;
    }

    private int[] doExecuteBatch(InnerSession session, Map<FieldMeta<?, ?>, FieldCodec> codecMap
            , @Nullable GeneratorWrapper generatorWrapper, String sql, List<List<ParamWrapper>> paramGroupList
            , List<BeanWrapper> domainWrapperList) {

        int[] insertRows;
        try (PreparedStatement st = session.createStatement(sql, false)) {

            for (List<ParamWrapper> paramList : paramGroupList) {
                // 1. set params
                setParams(st, paramList, codecMap);
                // 2. add to batch
                st.addBatch();
            }
            // 3. execute batch
            insertRows = st.executeBatch();
            if (generatorWrapper != null) {
                // 4. extract generated key (optional)
                extractBatchGenerateKey(st, generatorWrapper, domainWrapperList);
            }
            return insertRows;
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sql);
        }
    }

    private int doExecute(InnerSession session, Map<FieldMeta<?, ?>, FieldCodec> codecMap
            , @Nullable GeneratorWrapper generatorWrapper
            , String sql, List<ParamWrapper> paramList, @Nullable BeanWrapper beanWrapper) {

        try (PreparedStatement st = session.createStatement(sql, generatorWrapper != null)) {
            int updateRows;
            // 1. set params
            setParams(st, paramList, codecMap);
            // 2. execute
            updateRows = st.executeUpdate();
            if (generatorWrapper != null) {
                if (beanWrapper == null) {
                    throw new IllegalArgumentException("beanWrapper not null.");
                }
                // 3. extract generated key (optional)
                doExtractGeneratedKey(st.getGeneratedKeys(), generatorWrapper.fieldMeta, generatorWrapper.postGenerator
                        , beanWrapper);
            }
            return updateRows;
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sql);
        }

    }

    private static void setParams(PreparedStatement st, List<ParamWrapper> paramList
            , Map<FieldMeta<?, ?>, FieldCodec> codecMap) {

        ParamWrapper wrapper = null;
        try {

            final int size = paramList.size();
            for (int i = 0; i < size; i++) {
                wrapper = paramList.get(i);
                Object value = wrapper.value();
                if (value == null) {
                    st.setNull(i + 1, obtainVendorTypeNumber(wrapper.paramMeta()));
                } else {
                    setNonNullValue(st, i + 1, wrapper.paramMeta(), value, codecMap);

                }
            }
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error.ParamMetaAndValue[%s]", wrapper);
        } catch (Throwable e) {
            throw new ArmyRuntimeException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error.ParamMetaAndValue[%s]", wrapper);
        }


    }

    private static void setNonNullValue(PreparedStatement st, int index, ParamMeta paramMeta, Object value
            , Map<FieldMeta<?, ?>, FieldCodec> codecMap) throws SQLException {

        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            if (value instanceof ReadonlyWrapper) {
                // parent id generator is PostMultiGenerator,eg : AutoGeneratedKeyGenerator
                value = tryGetParentIdValue((ReadonlyWrapper) value, fieldMeta.tableMeta());
            } else {
                value = doEncodeFieldValue(fieldMeta, value, codecMap);
            }
            mappingMeta = fieldMeta.mappingType();
        } else if (paramMeta instanceof MappingMeta) {
            mappingMeta = (MappingMeta) paramMeta;
        } else {
            throw new UnKnownTypeException(paramMeta);
        }
        // set param
        mappingMeta.nonNullSet(st, value, index);
    }

    private static Object doEncodeFieldValue(FieldMeta<?, ?> fieldMeta, final Object value
            , Map<FieldMeta<?, ?>, FieldCodec> codecMap) {
        FieldCodec fieldCodec = codecMap.get(fieldMeta);
        Object encodedValue;
        if (fieldCodec != null) {
            // obtain encoded value before setParameter() method.
            encodedValue = fieldCodec.encode(fieldMeta, value);
            if (!fieldMeta.javaType().isInstance(value)) {
                throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, value);
            }
        } else {
            encodedValue = value;
        }
        return encodedValue;
    }

    private static Object tryGetParentIdValue(ReadonlyWrapper beanWrapper, TableMeta<?> tableMeta) {
        if (!(tableMeta instanceof ChildTableMeta)) {
            throw new ArmyRuntimeException(ErrorCode.CRITERIA_ERROR, "tableMeta isn't ChildTableMeta");
        }
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
        ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        GeneratorMeta parentPrimaryKeyGenerator = parentMeta.primaryKey().generator();

        Object parentPrimaryKeyValue;
        if (parentPrimaryKeyGenerator != null
                && PostFieldGenerator.class.isAssignableFrom(parentPrimaryKeyGenerator.type())) {
            // get parent primary key value.
            parentPrimaryKeyValue = beanWrapper.getPropertyValue(parentMeta.primaryKey().propertyName());

            if (parentPrimaryKeyValue == null) {
                throw new ArmyRuntimeException(ErrorCode.CRITERIA_ERROR
                        , "tableMeta[%s] insert sql parse error.", tableMeta);
            }
        } else {
            throw new ArmyRuntimeException(ErrorCode.CRITERIA_ERROR
                    , "tableMeta[%s] insert sql parse error.", tableMeta);
        }
        return parentPrimaryKeyValue;
    }


    private static Integer obtainVendorTypeNumber(ParamMeta paramMeta) {
        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            mappingMeta = ((FieldMeta<?, ?>) paramMeta).mappingType();
        } else if (paramMeta instanceof MappingMeta) {
            mappingMeta = (MappingMeta) paramMeta;
        } else {
            throw new UnKnownTypeException(paramMeta);
        }
        return mappingMeta.jdbcType().getVendorTypeNumber();
    }


    private static void extractBatchGenerateKey(PreparedStatement st, GeneratorWrapper generatorWrapper
            , List<BeanWrapper> domainWrapperList)
            throws SQLException {

        final FieldMeta<?, ?> fieldMeta = generatorWrapper.fieldMeta;
        final PostFieldGenerator postMultiGenerator = generatorWrapper.postGenerator;

        try (ResultSet resultSet = st.getGeneratedKeys()) {
            for (BeanWrapper domain : domainWrapperList) {
                doExtractGeneratedKey(resultSet, fieldMeta, postMultiGenerator, domain);
            }

        }
    }

    private static void doExtractGeneratedKey(ResultSet resultSet, FieldMeta<?, ?> fieldMeta
            , PostFieldGenerator postMultiGenerator, BeanWrapper beanWrapper) throws SQLException {

        if (!resultSet.next()) {
            throw databaseAutoGeneratorException(fieldMeta);
        }
        // invoke postMultiGenerator
        Object value = postMultiGenerator.apply(fieldMeta, resultSet);
        if (!fieldMeta.javaType().isInstance(value)) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR
                    , "PostMultiGenerator[%s] return value[%s] error,FieldMeta[%s]"
                    , postMultiGenerator.getClass().getName()
                    , value
                    , fieldMeta
            );
        }
        // set domain primary property
        beanWrapper.setPropertyValue(fieldMeta.propertyName(), value);
    }


    private static ArmyAccessException databaseAutoGeneratorException(FieldMeta<?, ?> fieldMeta) {
        throw new ArmyAccessException(ErrorCode.ACCESS_ERROR
                , "database no generated key for entity[%s] prop[%s]"
                , fieldMeta.tableMeta().javaType().getName()
                , fieldMeta.propertyName()
        );
    }

    @Nullable
    private static GeneratorWrapper obtainAutoGeneratorWrapper(InnerSession session, TableMeta<?> tableMeta) {

        TableMeta<?> parentMeta = tableMeta.parentMeta();
        FieldMeta<?, ?> primaryField;
        if (parentMeta == null) {
            primaryField = tableMeta.primaryKey();
        } else {
            primaryField = parentMeta.primaryKey();
        }

        FieldGenerator fieldGenerator = session.sessionFactory().fieldGeneratorMap().get(primaryField);
        GeneratorWrapper wrapper = null;
        if (fieldGenerator instanceof PostFieldGenerator) {
            wrapper = new GeneratorWrapper(primaryField, (PostFieldGenerator) fieldGenerator);
        }
        return wrapper;
    }

}
