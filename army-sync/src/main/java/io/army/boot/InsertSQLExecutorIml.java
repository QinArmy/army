package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.InsertRowsNotMatchException;
import io.army.UnKnownTypeException;
import io.army.beans.BeanWrapper;
import io.army.beans.DomainReadonlyWrapper;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.codec.FieldCodec;
import io.army.criteria.ArmyCriteriaException;
import io.army.criteria.CriteriaException;
import io.army.dialect.Dialect;
import io.army.dialect.DialectUtils;
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

    private static class GeneratorWrapper {

        private final FieldMeta<?, ?> fieldMeta;

        private final PostFieldGenerator postGenerator;

        GeneratorWrapper(FieldMeta<?, ?> fieldMeta, PostFieldGenerator postGenerator) {
            this.fieldMeta = fieldMeta;
            this.postGenerator = postGenerator;
        }
    }

    private final InnerSessionFactory sessionFactory;

    InsertSQLExecutorIml(InnerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final List<Integer> multiInsert(InnerSession session, List<SQLWrapper> sqlWrapperList)
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
            } else if (sqlWrapper instanceof SimpleSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteSimple(session, (SimpleSQLWrapper) sqlWrapper)
                );
            } else {
                throw new IllegalArgumentException(String.format("%s supported by multiInsert", sqlWrapper));
            }
        }
        return Collections.unmodifiableList(sqlWrapperUpdateRowList);
    }

    @Override
    public final List<Integer> batchInsert(InnerSession session, List<BatchSQLWrapper> sqlWrapperList) {
        List<Integer> sqlWrapperInsertRowList = new ArrayList<>(sqlWrapperList.size());

        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();

        for (BatchSQLWrapper sqlWrapper : sqlWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteChildBatch(session, (ChildBatchSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof DomainBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteDomainBatch(session, (DomainBatchSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof SimpleBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteSimpleBatch(session, (SimpleBatchSQLWrapper) sqlWrapper)
                );
            } else {
                throw new IllegalArgumentException(String.format("%s supported by batchInsert", sqlWrapper));
            }
        }
        return Collections.unmodifiableList(sqlWrapperInsertRowList);
    }

    /*################################## blow private method ##################################*/

    private int doExecuteSimple(InnerSession session, SimpleSQLWrapper sqlWrapper) {
        DomainWrapper domainWrapper = null;
        if (sqlWrapper instanceof DomainSQLWrapper) {
            domainWrapper = ((DomainSQLWrapper) sqlWrapper).domainWrapper();
        }
        int rows;
        rows = doExecute(session, sqlWrapper, domainWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "sql[%s] multiInsert rows[%s] error.", sqlWrapper.sql(), rows);
        }
        return rows;
    }

    private int doExecuteDomain(InnerSession session, DomainSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecute(session, sqlWrapper, sqlWrapper.domainWrapper());
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR, "sql[%s] multiInsert rows[%s] error."
                    , sqlWrapper.sql(), rows);
        }
        return rows;
    }

    private int doExecuteChild(InnerSession session, ChildSQLWrapper childSQLWrapper) {

        final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
        final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

        DomainWrapper domainWrapper = null;
        if (childWrapper instanceof DomainSQLWrapper) {
            domainWrapper = ((DomainSQLWrapper) childWrapper).domainWrapper();
        }
        int childRows, parentRows;
        // firstly,execute parent multiInsert sql
        parentRows = doExecute(session, parentWrapper, domainWrapper);

        // secondly, execute child multiInsert sql
        childRows = doExecute(session, childWrapper, null);

        if (parentRows != childRows || parentRows != 1) {
            throw new InsertRowsNotMatchException(
                    "child sql [%s] multiInsert rows[%s] and parent sql[%s] rows[%s] not match."
                    , childWrapper.sql(), childRows, parentWrapper.sql(), parentRows);
        }
        return childRows;
    }

    private int doExecuteSimpleBatch(InnerSession session, SimpleBatchSQLWrapper sqlWrapper) {
        return assertAndSumTotal(sqlWrapper
                , doExecuteBatch(session, sqlWrapper, Collections.emptyList(), null)
        );
    }

    private int doExecuteDomainBatch(InnerSession session, DomainBatchSQLWrapper sqlWrapper) {
        return assertAndSumTotal(sqlWrapper
                , doExecuteBatch(session, sqlWrapper, sqlWrapper.beanWrapperList(), sqlWrapper.tableMeta())
        );
    }

    private int assertAndSumTotal(SimpleBatchSQLWrapper sqlWrapper, int[] domainRows) {
        if (domainRows.length != sqlWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException(
                    "TableMeta[%s] multiInsert batch count error,sql[%s]"
                    , sqlWrapper.tableMeta(), sqlWrapper.sql());
        }
        int row;
        for (int i = 0; i < domainRows.length; i++) {
            row = domainRows[i];
            if (row != 1) {
                throw new InsertRowsNotMatchException(
                        "TableMeta[%s] multiInsert index[%s] actual row count[%s] not 1 ."
                        , sqlWrapper.tableMeta(), i, row);
            }
        }
        return domainRows.length;
    }

    private int doExecuteChildBatch(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {
        List<BeanWrapper> beanWrapperList = Collections.emptyList();
        TableMeta<?> tableMeta = null;

        final SimpleBatchSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final SimpleBatchSQLWrapper childWrapper = sqlWrapper.childWrapper();

        if (childWrapper instanceof DomainBatchSQLWrapper) {
            DomainBatchSQLWrapper domainSQLWrapper = (DomainBatchSQLWrapper) childWrapper;
            beanWrapperList = domainSQLWrapper.beanWrapperList();
            tableMeta = ((ChildTableMeta<?>) domainSQLWrapper.tableMeta()).parentMeta();
        }

        int[] parentRows, childRows;
        // firstly, parent multiInsert sql
        parentRows = doExecuteBatch(session, parentWrapper, beanWrapperList, tableMeta);

        // secondly,child multiInsert sql
        childRows = doExecuteBatch(session, childWrapper, Collections.emptyList(), null);

        return assertAndSumTotal(parentWrapper, childWrapper, parentRows, childRows);
    }

    private int assertAndSumTotal(SimpleBatchSQLWrapper parentWrapper, SimpleBatchSQLWrapper childWrapper
            , int[] parentRows, int[] childRows) {
        if (parentRows.length != childRows.length || childRows.length != childWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException(
                    "child sql[%s] multiInsert batch count[%s] and parent sql [%s] batch count[%s] not match."
                    , childWrapper.sql(), childRows.length, parentWrapper.sql(), parentRows.length);
        }
        int parentRow;
        for (int i = 0; i < parentRows.length; i++) {
            parentRow = parentRows[i];
            if (parentRow != childRows[i] || parentRow != 1) {
                throw new InsertRowsNotMatchException(
                        "child sql[%s] multiInsert batch[%s] rows[%s] and parent sql [%s] batch[%s] rows[%s] not match."
                        , childWrapper.sql(), i, childRows[i], parentWrapper.sql(), i, parentRows[i]);
            }
        }
        return childRows.length;
    }

    private int[] doExecuteBatch(InnerSession session, SimpleBatchSQLWrapper sqlWrapper
            , List<BeanWrapper> beanWrapperList, @Nullable TableMeta<?> tableMeta) {

        GeneratorWrapper generatorWrapper = null;
        if (tableMeta != null && !beanWrapperList.isEmpty()) {
            generatorWrapper = obtainAutoGeneratorWrapper(session, tableMeta);
        }
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), generatorWrapper != null)) {

            for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
                // 1. set params
                setParams(st, paramList);
                // 2. add to batch
                st.addBatch();
            }
            int[] insertRows;
            // 3. execute batch
            insertRows = st.executeBatch();
            if (generatorWrapper != null) {
                // 4. extract generated key (optional)
                extractBatchGenerateKey(st, generatorWrapper, beanWrapperList);
            }
            return insertRows;
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sqlWrapper.sql());
        }
    }

    private int doExecute(InnerSession session, SimpleSQLWrapper sqlWrapper, @Nullable DomainWrapper domainWrapper) {
        GeneratorWrapper generatorWrapper = null;
        if (domainWrapper != null) {
            generatorWrapper = obtainAutoGeneratorWrapper(session, domainWrapper);
        }
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), generatorWrapper != null)) {
            // 1. set params
            setParams(st, sqlWrapper.paramList());
            int updateRows;
            // 2. execute
            updateRows = st.executeUpdate();

            if (generatorWrapper != null) {
                // 3. extract generated key (optional)
                try (ResultSet resultSet = st.getGeneratedKeys()) {
                    doExtractGeneratedKey(resultSet, generatorWrapper.fieldMeta, generatorWrapper.postGenerator
                            , domainWrapper);
                }

            }
            return updateRows;
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sqlWrapper.sql());
        }

    }

    private void setParams(PreparedStatement st, List<ParamWrapper> paramList) throws SQLException {

        ParamWrapper wrapper;
        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            wrapper = paramList.get(i);
            Object value = wrapper.value();
            if (value == null) {
                st.setNull(i + 1, obtainVendorTypeNumber(wrapper.paramMeta()));
            } else {
                setNonNullValue(st, i + 1, wrapper.paramMeta(), value);

            }
        }

    }

    private void setNonNullValue(PreparedStatement st, final int index, ParamMeta paramMeta, Object value)
            throws SQLException {

        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            if (TableMeta.ID.equals(fieldMeta.propertyName())
                    && DialectUtils.hasParentIdPostFieldGenerator(fieldMeta.tableMeta())) {
                // parent id generator is PostMultiGenerator,eg : AutoGeneratedKeyGenerator
                value = obtainParentIdValue(fieldMeta, value);
            } else {
                value = doEncodeFieldValue(fieldMeta, value);
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

    private Object doEncodeFieldValue(FieldMeta<?, ?> fieldMeta, final Object value) {
        Map<FieldMeta<?, ?>, FieldCodec> codecMap = this.sessionFactory.fieldCodecMap(fieldMeta.tableMeta());

        FieldCodec fieldCodec = codecMap.get(fieldMeta);
        Object encodedValue;
        if (fieldCodec != null) {
            if (!(value instanceof DomainReadonlyWrapper)) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] criteria error,value isn't DomainReadonlyWrapper.", fieldMeta);
            }
            // obtain encoded value before setParameter() method.
            DomainReadonlyWrapper readonlyWrapper = (DomainReadonlyWrapper) value;
            Object plantObject = readonlyWrapper.getPropertyValue(fieldMeta.propertyName());
            if (plantObject == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] property value is null,can't invoke FieldCodec.", fieldMeta);
            }
            // invoke fieldCodec
            encodedValue = fieldCodec.encode(fieldMeta, plantObject, readonlyWrapper);
            if (!fieldMeta.javaType().isInstance(value)) {
                throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, value);
            }
        } else {
            encodedValue = value;
        }
        return encodedValue;
    }

    private static Object obtainParentIdValue(FieldMeta<?, ?> fieldMeta, Object value) {
        if (!(value instanceof ReadonlyWrapper)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "FieldMeta[%s] criteria error,value object isn't ReadonlyWrapper", fieldMeta);
        }
        ReadonlyWrapper readonlyWrapper = (ReadonlyWrapper) value;
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) fieldMeta.tableMeta()).parentMeta();
        Object idValue = readonlyWrapper.getPropertyValue(parentMeta.primaryKey().propertyName());
        if (idValue == null) {
            throw new ArmyCriteriaException(ErrorCode.CRITERIA_ERROR
                    , "ChildTable[%s] inset parse error,parent id value is null."
                    , fieldMeta.tableMeta());
        }
        return idValue;
    }

    private static int obtainVendorTypeNumber(ParamMeta paramMeta) {
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
            , List<BeanWrapper> beanWrapperList)
            throws SQLException {

        final FieldMeta<?, ?> fieldMeta = generatorWrapper.fieldMeta;
        final PostFieldGenerator postMultiGenerator = generatorWrapper.postGenerator;

        try (ResultSet resultSet = st.getGeneratedKeys()) {
            for (BeanWrapper beanWrapper : beanWrapperList) {
                doExtractGeneratedKey(resultSet, fieldMeta, postMultiGenerator, beanWrapper);
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
    private static GeneratorWrapper obtainAutoGeneratorWrapper(InnerSession session, DomainWrapper domainWrapper) {
        TableMeta<?> tableMeta = domainWrapper.tableMeta();
        if (tableMeta instanceof ChildTableMeta) {
            tableMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
        }
        return obtainAutoGeneratorWrapper(session, tableMeta);
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
