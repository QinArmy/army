package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.beans.BeanWrapper;
import io.army.dialect.*;
import io.army.generator.GeneratorException;
import io.army.generator.MultiGenerator;
import io.army.generator.PostMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class InsertSQLExecutorIml implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);

    static final InsertSQLExecutorIml INSTANCE = new InsertSQLExecutorIml();


    private static class GeneratorWrapper {

        private final FieldMeta<?, ?> fieldMeta;

        private final PostMultiGenerator postGenerator;

        GeneratorWrapper(FieldMeta<?, ?> fieldMeta, PostMultiGenerator postGenerator) {
            this.fieldMeta = fieldMeta;
            this.postGenerator = postGenerator;
        }
    }

    private InsertSQLExecutorIml() {
    }

    @Override
    public final void insert(InnerSession session, List<SQLWrapper> sqlWrapperList)
            throws InsertException {

        final boolean showSql = session.sessionFactory().readonly();
        GeneratorWrapper generatorWrapper = null;
        BeanWrapper beanWrapper = null;
        final int[] updateCount = new int[sqlWrapperList.size()];
        int sqlNum = 0;
        final boolean traceEnabled = LOG.isTraceEnabled();

        for (SQLWrapper wrapper : sqlWrapperList) {
            if (showSql) {
                //LOG.info("{}", wrapper.toString(session.sessionFactory().dialect()));
            }
            if (wrapper instanceof BeanSQLWrapper) {
                beanWrapper = ((BeanSQLWrapper) wrapper).beanWrapper();
                // 1. find PostMultiGenerator
                generatorWrapper = getAutoGeneratorWrapper(session, beanWrapper);
            }
            try (PreparedStatement st = session.createStatement(wrapper.sql(), generatorWrapper != null)) {
                // 2. set params
                setParams(st, wrapper.paramList());
                //3. execute dml
                updateCount[sqlNum] = st.executeUpdate();
                if (generatorWrapper != null) {
                    // 4. extract generated key (optional)
                    extractGeneratedKey(generatorWrapper, st, beanWrapper);
                }

            } catch (SQLException e) {
                throw new InsertException(ErrorCode.INSERT_ERROR, e, "dml execute error:\n%s"
                        , wrapper.toString(null));
            }

            if (traceEnabled) {
                LOG.trace("dml:{};singleUpdate count:{}", wrapper.sql(), updateCount[sqlNum]);
            }
            //5. beanWrapper,generatorWrapper as null;
            beanWrapper = null;
            generatorWrapper = null;
            sqlNum++;

        }

    }


    @Override
    public final void batchInsert(InnerSession session, List<BatchSQLWrapper> batchSQLWrapperList) {
        for (BatchSQLWrapper sqlWrapper : batchSQLWrapperList) {
            // 1. create PreparedStatement
            try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
                for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
                    // 2. set params.
                    setParams(st, paramList);
                    // 3. add param group to batch.
                    st.addBatch();
                }
                //3. execute batch.
                st.executeBatch();

            } catch (SQLException e) {
                throw new InsertException(ErrorCode.INSERT_ERROR, e, "dml execute error:\n%s"
                        , sqlWrapper.toString(null));
            }
        }
    }

    private static void setParams(PreparedStatement st, List<ParamWrapper> paramWrapperList)
            throws SQLException {

        ParamWrapper paramWrapper;
        MappingType mappingType;
        for (int i = 0, len = paramWrapperList.size(); i < len; i++) {
            paramWrapper = paramWrapperList.get(i);
            mappingType = paramWrapper.mappingType();
            Object value = paramWrapper.value();
            if (value == null) {
                st.setNull(i + 1, mappingType.jdbcType().getVendorTypeNumber());
            } else {
                mappingType.nonNullSet(st, value, i + 1);
            }
        }
    }

    private static void extractGeneratedKey(GeneratorWrapper generatorWrapper
            , PreparedStatement st, BeanWrapper beanWrapper)
            throws SQLException {


        try (ResultSet resultSet = st.getGeneratedKeys()) {
            FieldMeta<?, ?> fieldMeta = generatorWrapper.fieldMeta;
            if (!resultSet.next()) {
                throw databaseAutoGeneratorException(fieldMeta);
            }
            PostMultiGenerator postMultiGenerator = generatorWrapper.postGenerator;

            Object value = postMultiGenerator.apply(fieldMeta, resultSet);
            if (!fieldMeta.javaType().isInstance(value)) {
                throw new GeneratorException(ErrorCode.GENERATOR_ERROR
                        , "PostMultiGenerator[%s] return value[%s] error,FieldMeta[%s]"
                        , postMultiGenerator.getClass().getName()
                        , value
                        , fieldMeta
                );
            }
            beanWrapper.setPropertyValue(fieldMeta.propertyName(), value);
        }
    }

    private static ArmyAccessException databaseAutoGeneratorException(FieldMeta<?, ?> fieldMeta) {
        throw new ArmyAccessException(ErrorCode.ACCESS_ERROR
                , "database no generated key for entity[%s] prop[%s]"
                , fieldMeta.tableMeta().javaType().getName()
                , fieldMeta.propertyName()
        );
    }

    @Nullable
    private static GeneratorWrapper getAutoGeneratorWrapper(InnerSession session, BeanWrapper beanWrapper) {
        TableMeta<?> tableMeta = session.sessionFactory().tableMetaMap().get(beanWrapper.getWrappedClass());

        TableMeta<?> parentMeta = tableMeta.parentMeta();
        FieldMeta<?, ?> primaryField;
        if (parentMeta == null) {
            primaryField = tableMeta.primaryKey();
        } else {
            primaryField = parentMeta.primaryKey();
        }

        MultiGenerator multiGenerator = session.sessionFactory().fieldGeneratorMap().get(primaryField);
        GeneratorWrapper wrapper = null;
        if (multiGenerator instanceof PostMultiGenerator) {
            wrapper = new GeneratorWrapper(primaryField, (PostMultiGenerator) multiGenerator);
        }
        return wrapper;
    }

}
