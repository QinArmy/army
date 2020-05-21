package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.ObjectWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.codec.FieldCodec;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.wrapper.BatchSimpleSQLWrapper;
import io.army.wrapper.FieldParamWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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


    static void assertSingleUpdateSetClauseField(FieldMeta<?, ?> fieldMeta, TableMeta<?> tableMeta) {
        if (fieldMeta.tableMeta() == tableMeta) {
            throw new IllegalArgumentException(String.format(
                    "FieldMeta[%s] don't belong to TableMeta[%s]", fieldMeta, tableMeta));
        }

        assertSetClauseField(fieldMeta);
    }


    static List<IPredicate> extractParentPredicateList(ChildTableMeta<?> childMeta, List<FieldMeta<?, ?>> childFieldList
            , List<IPredicate> predicateList) {

        List<IPredicate> parentPredicates;
        // 1. extract parent predicate from where predicate list
        if (childFieldList.isEmpty()) {
            parentPredicates = new ArrayList<>(predicateList.size() + 1);
            parentPredicates.addAll(predicateList);
        } else {
            final boolean firstIsPrimary = predicateList.get(0) instanceof PrimaryValueEqualPredicate;
            final Collection<FieldMeta<?, ?>> childFields = childFieldList.size() > 5
                    ? new HashSet<>(childFieldList) : childFieldList;

            parentPredicates = new ArrayList<>();
            doExtractParentPredicate(predicateList, childFields, parentPredicates, firstIsPrimary);
        }

        // 2. append discriminator predicate
        FieldMeta<?, ? extends CodeEnum> fieldMeta = childMeta.discriminator();
        @SuppressWarnings("unchecked")
        FieldMeta<?, CodeEnum> enumFieldMeta = (FieldMeta<?, CodeEnum>) fieldMeta;
        CodeEnum codeEnum = CodeEnum.resolve(enumFieldMeta.javaType(), childMeta.discriminatorValue());

        if (codeEnum == null) {
            throw new MetaException(ErrorCode.META_ERROR
                    , "ChildTableMeta[%s] discriminatorValue[%s] can't resolve CodeEnum."
                    , childMeta, childMeta.discriminatorValue());
        }
        parentPredicates.add(enumFieldMeta.eq(codeEnum));
        return parentPredicates;
    }


    static void assertUpdateSetAndWhereClause(InnerUpdate update) {
        if (CollectionUtils.isEmpty(update.targetFieldList())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update must have where clause.");
        }
        List<FieldMeta<?, ?>> targetFieldList = update.targetFieldList();
        if (CollectionUtils.isEmpty(targetFieldList)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update must have set clause.");
        }
        List<Expression<?>> valueExpList = update.valueExpList();
        if (CollectionUtils.isEmpty(valueExpList)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update must have set clause.");
        }
        if (targetFieldList.size() != valueExpList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "update set clause target field list size and value expression list size not match.");
        }
    }


    static void standardSimpleUpdateSetClause(UpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldMetaList, List<Expression<?>> valueExpList) {
        if (tableMeta.immutable()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] is immutable.", tableAlias);
        }
        final MappingMode mappingMode = tableMeta.mappingMode();

        if (mappingMode != MappingMode.PARENT) {
            Assert.notEmpty(fieldMetaList, "set clause must not empty");
            Assert.isTrue(fieldMetaList.size() == valueExpList.size()
                    , "field list ifAnd value exp list size not match");
        }

        StringBuilder builder = context.sqlBuilder()
                .append(" SET");

        final int size = fieldMetaList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(",");
            }
            FieldMeta<?, ?> fieldMeta = fieldMetaList.get(i);
            Expression<?> valueExp = valueExpList.get(i);

            DMLUtils.assertSingleUpdateSetClauseField(fieldMeta, tableMeta);

            // fieldMeta self-describe
            context.appendField(tableAlias, fieldMeta);
            builder.append(" =");
            // expression self-describe
            valueExp.appendSQL(context);

        }
        if (mappingMode != MappingMode.CHILD) {
            if (!fieldMetaList.isEmpty()) {
                builder.append(",");
            }
            // appendText version And updateTime
            DMLUtils.setClauseFieldsManagedByArmy(context, tableMeta, tableAlias);
        }
    }

    static void setClauseFieldsManagedByArmy(TableContextSQLContext context, TableMeta<?> tableMeta
            , String tableAlias) {
        //1. version field
        final FieldMeta<?, ?> versionField = tableMeta.getField(TableMeta.VERSION);
        StringBuilder builder = context.sqlBuilder();

        context.appendField(tableAlias, versionField);

        builder.append(" =");
        context.appendField(tableAlias, versionField);
        builder.append(" + 1 ");

        //2. updateTime field
        final FieldMeta<?, ?> updateTimeField = tableMeta.getField(TableMeta.UPDATE_TIME);

        builder.append(",");
        // updateTime field self-describe
        context.appendField(tableAlias, updateTimeField);
        builder.append(" =");

        final Dialect dialect = context.dialect();
        final ZonedDateTime now = ZonedDateTime.now(dialect.zoneId());

        if (updateTimeField.javaType() == LocalDateTime.class) {
            SQLS.param(now.toLocalDateTime(), updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            if (dialect.supportZoneId()) {
                throw new MetaException(ErrorCode.META_ERROR
                        , "dialec[%s]t not supported zone.", dialect.sqlDialect());
            }
            SQLS.param(now, updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }


    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable."
                    , fieldMeta.tableMeta().javaType(), fieldMeta.propertyName()));
        }
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())
                || TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "version or updateTime is managed by army.");
        }
    }

    static boolean hasVersionPredicate(List<IPredicate> predicateList) {
        boolean hasVersion = false;
        for (IPredicate predicate : predicateList) {
            if (predicate instanceof FieldValuePredicate) {
                FieldExp<?, ?> fieldExp = ((FieldValuePredicate) predicate).fieldExp();
                if (TableMeta.VERSION.equals(fieldExp.propertyName())) {
                    hasVersion = true;
                    break;
                }
            }
        }
        return hasVersion;
    }

    /**
     * @return a unmodifiable set
     */
    static Set<FieldMeta<?, ?>> mergeInsertFields(TableMeta<?> logicalTable, Dialect dialect
            , Collection<FieldMeta<?, ?>> targetFields) {

        Set<FieldMeta<?, ?>> fieldMetaSet = new HashSet<>(targetFields);

        TableMeta<?> parentMeta = logicalTable;
        if (logicalTable instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) logicalTable;
            parentMeta = childMeta.parentMeta();
            appendGeneratorFields(fieldMetaSet, parentMeta, dialect.sessionFactory());
        }
        appendGeneratorFields(fieldMetaSet, logicalTable, dialect.sessionFactory());

        FieldMeta<?, ?> discriminator = logicalTable.discriminator();
        if (discriminator != null) {
            fieldMetaSet.add(discriminator);
        }
        if (parentMeta.mappingProp(TableMeta.CREATE_TIME)) {
            fieldMetaSet.add(logicalTable.getField(TableMeta.CREATE_TIME));
        }
        if (parentMeta.mappingProp(TableMeta.UPDATE_TIME)) {
            fieldMetaSet.add(logicalTable.getField(TableMeta.UPDATE_TIME));
        }
        if (parentMeta.mappingProp(TableMeta.VERSION)) {
            fieldMetaSet.add(logicalTable.getField(TableMeta.VERSION));
        }
        return Collections.unmodifiableSet(fieldMetaSet);
    }

    static void createStandardInsertForSimple(TableMeta<?> physicalTable, TableMeta<?> logicalTable
            , Collection<FieldMeta<?, ?>> fieldMetas, ReadonlyWrapper domainWrapper
            , StandardInsertContext context) {

        final GenericSessionFactory sessionFactory = context.dialect.sessionFactory();
        final StringBuilder fieldBuilder = context.fieldsBuilder().append("INSERT INTO");
        // append table name
        context.appendTable(physicalTable);
        context.fieldsBuilder().append(" (");

        final StringBuilder valueBuilder = context.sqlBuilder()
                .append(" VALUE (");

        Object value;
        int count = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = domainWrapper.getPropertyValue(fieldMeta.propertyName());
            if (value == null) {
                continue;
            }
            if (count > 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            context.appendField(fieldMeta);

            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, logicalTable));
            } else {
                valueBuilder.append("?");
                FieldCodec fieldCodec = sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec != null) {
                    context.appendParam(ParamWrapper.build(fieldMeta, value));
                } else {
                    context.appendParam(ParamWrapper.build(fieldMeta.mappingMeta(), value));
                }

            }
            count++;
        }

        fieldBuilder.append(" )");
        valueBuilder.append(" )");

    }


    static void createBatchInsertForSimple(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> fieldMetas
            , InsertContext context) {

        StringBuilder fieldBuilder = context.fieldsBuilder()
                .append("INSERT INTO");
        // append table name
        context.appendTable(tableMeta);
        fieldBuilder.append(" ( ");

        /// VALUE clause
        StringBuilder valueBuilder = context.sqlBuilder()
                .append(" VALUE ( ");

        final SQL sql = context.dql();
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            if (index > 0) {
                fieldBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));

            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, tableMeta));
            } else {
                valueBuilder.append("?");
                context.appendParam(FieldParamWrapper.build(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static List<BatchSimpleSQLWrapper> createBatchInsertWrapper(InnerStandardBatchInsert insert
            , List<SimpleSQLWrapper> sqlWrapperList, final FieldValuesGenerator valuesGenerator) {
        if (sqlWrapperList.size() < 3) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "sqlWrapperList size must less than 3.");
        }
        List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        List<IDomain> domainList = insert.valueList();
        final TableMeta<?> tableMeta = insert.tableMeta();

        ObjectWrapper beanWrapper;

        List<BatchSimpleSQLWrapper> batchWrapperList = new ArrayList<>(domainList.size() * sqlWrapperList.size());
        Map<IDomain, ObjectWrapper> beanWrapperMap = new HashMap<>();
        for (SimpleSQLWrapper sqlWrapper : sqlWrapperList) {
            List<List<ParamWrapper>> paramGroupList = new ArrayList<>(fieldList.size());

            for (IDomain domain : domainList) {

                beanWrapper = beanWrapperMap.computeIfAbsent(domain
                        // create required value
                        , key -> valuesGenerator.createValues(tableMeta, key));

                Assert.state(beanWrapper.getWrappedInstance() == domain
                        , () -> String.format("IDomain[%s] hasCode() and equals() implementation error.", domain));

                List<ParamWrapper> paramWrapperList = new ArrayList<>(sqlWrapper.paramList().size());
                for (ParamWrapper paramWrapper : sqlWrapper.paramList()) {
                    if (paramWrapper instanceof FieldParamWrapper) {
                        FieldParamWrapper wrapper = (FieldParamWrapper) paramWrapper;
                        paramWrapperList.add(
                                ParamWrapper.build(
                                        wrapper.paramMeta().mappingMeta()
                                        , beanWrapper.getPropertyValue(wrapper.paramMeta().propertyName())
                                )
                        );
                    } else {
                        paramWrapperList.add(paramWrapper);
                    }
                }
                paramGroupList.add(paramWrapperList);
            }
            batchWrapperList.add(
                    BatchSimpleSQLWrapper.build(
                            sqlWrapper.sql()
                            , Collections.unmodifiableList(paramGroupList)
                            , tableMeta
                    )
            );
        }
        return batchWrapperList;
    }

    static boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.tableMeta().discriminator()
                ;
    }

    static Object createConstant(FieldMeta<?, ?> fieldMeta, TableMeta<?> logicalTable) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == logicalTable.discriminator()) {
            value = logicalTable.discriminatorValue();
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    static List<IPredicate> mergeDomainUpdatePredicateList(List<IPredicate> originalPredicateList
            , IndexFieldMeta<?, ?> primaryField, Object primaryKeyValue) {

        List<IPredicate> mergedPredicateList = new ArrayList<>(originalPredicateList.size() + 1);
        final IndexFieldMeta<?, Object> primaryKey = (IndexFieldMeta<?, Object>) primaryField;
        Assert.isInstanceOf(primaryKey.javaType(), primaryKeyValue);

        // firstly ,add predicate sql fragment 'id = ?'
        mergedPredicateList.add(
                primaryKey.eq(
                        SQLS.param(primaryKeyValue, primaryKey.mappingMeta())
                )
        );
        // secondly, add update.predicateList()
        mergedPredicateList.addAll(originalPredicateList);

        return mergedPredicateList;
    }


    /*################################## blow private method ##################################*/

    private static void appendGeneratorFields(Set<FieldMeta<?, ?>> fieldMetaSet, TableMeta<?> physicalTable
            , GenericSessionFactory factory) {

        List<FieldMeta<?, ?>> chain = factory.generatorChain(physicalTable);
        FieldGenerator fieldGenerator;
        for (FieldMeta<?, ?> fieldMeta : chain) {
            fieldGenerator = factory.fieldGenerator(fieldMeta);
            if (fieldGenerator instanceof PreFieldGenerator) {
                fieldMetaSet.add(fieldMeta);
            }

        }

    }

    private static void doExtractParentPredicate(List<IPredicate> predicateList, Collection<FieldMeta<?, ?>> childFields
            , List<IPredicate> newPredicates, final boolean firstIsPrimary) {
        for (IPredicate predicate : predicateList) {
            if (predicate instanceof OrPredicate) {
                doExtractParentPredicatesFromOrPredicate((OrPredicate) predicate
                        , childFields, newPredicates, firstIsPrimary);
            } else if (predicate.containsField(childFields)) {
                if (!firstIsPrimary) {
                    throw createNoPrimaryPredicateException(childFields);
                }
            } else {
                newPredicates.add(predicate);
            }
        }
    }

    private static void doExtractParentPredicatesFromOrPredicate(OrPredicate orPredicate
            , Collection<FieldMeta<?, ?>> childFields, List<IPredicate> newPredicates
            , final boolean firstIsPrimary) {

        IPredicate left = orPredicate.leftPredicate(), newLeft = null;
        if (left instanceof OrPredicate) {
            List<IPredicate> newLeftList = new ArrayList<>();
            doExtractParentPredicatesFromOrPredicate((OrPredicate) left, childFields, newLeftList, firstIsPrimary);
            if (newLeftList.size() == 1) {
                newLeft = newLeftList.get(0);
            } else if (newLeftList.size() > 1) {
                // here ,left is drop for contains childField
                newPredicates.addAll(newLeftList);
            }
        } else if (left.containsField(childFields)) {
            if (!firstIsPrimary) {
                throw createNoPrimaryPredicateException(childFields);
            }
        } else {
            newLeft = left;
        }
        List<IPredicate> newRightPredicates = new ArrayList<>();
        doExtractParentPredicate(orPredicate.rightPredicate(), childFields, newRightPredicates, firstIsPrimary);

        if (newLeft == null) {
            newPredicates.addAll(newRightPredicates);
        } else if (newRightPredicates.isEmpty()) {
            newPredicates.add(newLeft);
        } else {
            newPredicates.add(newLeft.or(newRightPredicates));
        }
    }

    private static CriteriaException createNoPrimaryPredicateException(Collection<FieldMeta<?, ?>> childFields) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "detect ChildTableMeta set clause FieldMetas[%s] present where clause," +
                "but first predicate isn't primary field predicate."
                , childFields);
    }
}
