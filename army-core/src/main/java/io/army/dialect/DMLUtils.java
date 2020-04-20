package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.BeanWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.*;

abstract class DMLUtils {

    DMLUtils() {
        throw new UnsupportedOperationException();
    }

    static int selectionCount(SubQuery subQuery) {
        int count = 0;
        for (SelectPart selectPart : subQuery.selectPartList()) {
            if (selectPart instanceof SelectionGroup) {
                count += ((SelectionGroup) selectPart).selectionList().size();
            } else {
                count++;
            }
        }
        return count;
    }


    static void createInsertForSimple(TableMeta<?> tableMeta, Collection<? extends FieldMeta<?, ?>> fieldMetas
            , ReadonlyWrapper entityWrapper, InsertContext context) {

        StringBuilder fieldBuilder = context.fieldStringBuilder().append("INSERT INTO ");
        StringBuilder valueBuilder = context.stringBuilder().append(" VALUE (");

        final SQL sql = context.dql();
        final boolean defaultIfNull = context.defaultIfNull();
        context.appendTable(tableMeta);
        fieldBuilder.append("(");

        Object value;
        int count = 0;
        Expression<?> commonExp;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = entityWrapper.getPropertyValue(fieldMeta.propertyName());

            if (value == null && !fieldMeta.nullable() && !defaultIfNull) {
                continue;
            }
            if (count != 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));
            // value
            commonExp = context.commonExp(fieldMeta);
            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else if (commonExp != null) {
                commonExp.appendSQL(context);
            } else if (value == null && defaultIfNull) {
                valueBuilder.append("DEFAULT");
            } else {
                valueBuilder.append("?");
                context.appendParam(ParamWrapper.build(fieldMeta.mappingType(), value));
            }
            count++;
        }

        fieldBuilder.append(" )");
        valueBuilder.append(" )");

    }

    static void createBatchInsertForSimple(TableMeta<?> tableMeta, InsertContext context) {

        StringBuilder fieldBuilder = context.fieldStringBuilder().append("INSERT INTO ");
        StringBuilder valueBuilder = context.stringBuilder().append(" VALUE ( ");
        context.appendTable(tableMeta);
        fieldBuilder.append(" ( ");

        final SQL sql = context.dql();
        int index = 0;
        Expression<?> commonExp;
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            if (index > 0) {
                fieldBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));
            // value or placeholder
            commonExp = context.commonExp(fieldMeta);
            if (!fieldMeta.insertalbe()) {
                valueBuilder.append("DEFAULT");
            }
            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else if (commonExp != null) {
                commonExp.appendSQL(context);
            } else {
                valueBuilder.append("?");
                context.appendParam(new EmptyParamWrapper(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static List<BatchSQLWrapper> createBatchInsertWrapper(InnerStandardInsert insert
            , List<SQLWrapper> sqlWrapperList, final FieldValuesGenerator valuesGenerator) {
        if (sqlWrapperList.size() < 3) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "sqlWrapperList size must less than 3.");
        }
        List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        List<IDomain> domainList = insert.valueList();
        final TableMeta<?> tableMeta = insert.tableMeta();
        final boolean alwaysUseCommonExp = insert.alwaysUseCommonExp();

        BeanWrapper beanWrapper;

        List<BatchSQLWrapper> batchWrapperList = new ArrayList<>(domainList.size() * sqlWrapperList.size());
        Map<IDomain, BeanWrapper> beanWrapperMap = new HashMap<>();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            List<List<ParamWrapper>> paramGroupList = new ArrayList<>(fieldList.size());

            for (IDomain domain : domainList) {

                beanWrapper = beanWrapperMap.computeIfAbsent(domain
                        // create required value
                        , key -> valuesGenerator.createValues(tableMeta, key, alwaysUseCommonExp));

                Assert.state(beanWrapper.getWrappedInstance() == domain
                        , () -> String.format("IDomain[%s] hasCode() and equals() implementation error.", domain));

                List<ParamWrapper> paramWrapperList = new ArrayList<>(sqlWrapper.paramList().size());
                for (ParamWrapper paramWrapper : sqlWrapper.paramList()) {
                    if (paramWrapper instanceof EmptyParamWrapper) {
                        EmptyParamWrapper wrapper = (EmptyParamWrapper) paramWrapper;
                        paramWrapperList.add(
                                ParamWrapper.build(
                                        wrapper.fieldMeta.mappingType()
                                        , beanWrapper.getPropertyValue(wrapper.fieldMeta.propertyName())
                                )
                        );
                    } else {
                        paramWrapperList.add(paramWrapper);
                    }
                }
                paramGroupList.add(paramWrapperList);
            }
            batchWrapperList.add(
                    BatchSQLWrapper.build(
                            sqlWrapper.sql()
                            , Collections.unmodifiableList(paramGroupList)
                    )
            );
        }
        return Collections.unmodifiableList(batchWrapperList);
    }


    static SQLWrapper createSQLWrapper(InsertContext context) {
        return SQLWrapper.build(
                context.fieldStringBuilder().toString() + context.stringBuilder().toString()
                , context.paramWrapper()
        );
    }

    static BeanSQLWrapper createSQLWrapper(InsertContext context, BeanWrapper beanWrapper) {
        return BeanSQLWrapper.build(
                context.fieldStringBuilder().toString() + context.stringBuilder().toString()
                , context.paramWrapper()
                , beanWrapper
        );
    }

    static boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.tableMeta().discriminator()
                ;
    }

    static Object createConstant(FieldMeta<?, ?> fieldMeta) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == fieldMeta.tableMeta().discriminator()) {
            value = fieldMeta.tableMeta().discriminatorValue();
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    private static final class EmptyParamWrapper implements ParamWrapper {

        private final FieldMeta<?, ?> fieldMeta;

        private EmptyParamWrapper(FieldMeta<?, ?> fieldMeta) {
            this.fieldMeta = fieldMeta;
        }

        @Override
        public MappingType mappingType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object value() {
            throw new UnsupportedOperationException();
        }
    }

}
