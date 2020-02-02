package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.Session;
import io.army.beans.BeanWrapper;
import io.army.dialect.InsertException;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQLWrapper;
import io.army.generator.GeneratorException;
import io.army.generator.MultiGenerator;
import io.army.generator.PostMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class InsertSQLExecutorIml implements InsertSQLExecutor {

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
    public final void executeInsert(InnerSession session, List<SQLWrapper> sqlWrapperList, BeanWrapper beanWrapper)
            throws InsertException {
        Assert.isTrue(sqlWrapperList.size() < 3, "sqlWrapperList error.");

        TableMeta<?> tableMeta = session.sessionFactory().tableMetaMap().get(beanWrapper.getWrappedClass());
        Assert.notNull(tableMeta, "beanWrapper error,not found corresponding TableMeta.");

        // 1. find PostMultiGenerator
        final GeneratorWrapper generatorMeta = getAutoGeneratorWrapper(session, tableMeta);
        final int[] updateCount = new int[sqlWrapperList.size()];
        int sqlNum = 0;
        final boolean traceEnabled = LOG.isTraceEnabled();
        for (SQLWrapper wrapper : sqlWrapperList) {
            final boolean generatedKey = sqlNum == 0 && generatorMeta != null;

            try (PreparedStatement st = session.createStatement(wrapper.sql(), generatedKey)) {
                // 2. set params
                setParams(st, wrapper.paramList());
                //3. execute sql
                updateCount[sqlNum] = st.executeUpdate();
                if (sqlNum == 0 && generatorMeta != null) {
                    // 4. extract generated key (optional)
                    extractGeneratedKey(generatorMeta, st, beanWrapper);
                }

            } catch (SQLException e) {
                throw new InsertException(ErrorCode.INSERT_ERROR, e, "sql execute error:\n%s"
                        , wrapper.toString(session.sessionFactory().dialect()));
            }

            if (traceEnabled) {
                LOG.trace("sql:{};update count:{}", wrapper.sql(), updateCount[sqlNum]);
            }
            sqlNum++;

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
                mappingType.nullSafeSet(st,value, i + 1);
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
                , fieldMeta.table().javaType().getName()
                , fieldMeta.propertyName()
        );
    }

    @Nullable
    private static GeneratorWrapper getAutoGeneratorWrapper(Session session
            , TableMeta<?> tableMeta) {
        TableMeta<?> parentMeta = tableMeta.parent();
        FieldMeta<?, ?> fieldMeta;
        if (parentMeta == null) {
            fieldMeta = tableMeta.primaryKey();
        } else {
            fieldMeta = parentMeta.primaryKey();
        }

        MultiGenerator multiGenerator = session.sessionFactory().fieldGeneratorMap().get(fieldMeta);
        GeneratorWrapper wrapper = null;
        if (multiGenerator instanceof PostMultiGenerator) {
            wrapper = new GeneratorWrapper(fieldMeta, (PostMultiGenerator) multiGenerator);
        }
        return wrapper;
    }

}
