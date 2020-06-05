package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.AccessorFactory;
import io.army.beans.BeanWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
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
import io.army.wrapper.*;

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

    /**
     * @return a unmodifiable List
     */
    static List<Selection> selectionList(SubQuery subQuery) {
        List<Selection> selectionList = new ArrayList<>();
        for (SelectPart selectPart : subQuery.selectPartList()) {
            if (selectPart instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectPart).selectionList());
            } else {
                selectionList.add((Selection) selectPart);
            }
        }
        return Collections.unmodifiableList(selectionList);
    }

    static void assertSubQueryInsert(List<FieldMeta<?, ?>> fieldMetaList, SubQuery subQuery) {
        List<Selection> selectionList = selectionList(subQuery);
        if (fieldMetaList.size() != selectionList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "SubQuery Insert,target field list size[%s] and sub query selection list size[%s] not match."
                    , fieldMetaList.size(), selectionList.size());
        }
        final int size = fieldMetaList.size();
        for (int i = 0; i < size; i++) {
            if (fieldMetaList.get(i).mappingMeta() != selectionList.get(i).mappingMeta()) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "SubQuery Insert,index[%s] field MappingMeta[%s] and sub query MappingMeta[%s] not match."
                        , i, fieldMetaList.get(i).mappingMeta(), selectionList.get(i).mappingMeta());
            }
        }
    }


    static void assertSingleUpdateSetClauseField(FieldMeta<?, ?> fieldMeta, TableMeta<?> tableMeta) {
        if (fieldMeta.tableMeta() != tableMeta) {
            throw new IllegalArgumentException(String.format(
                    "FieldMeta[%s] don't belong to TableMeta[%s]", fieldMeta, tableMeta));
        }

        assertSetClauseField(fieldMeta);
    }


    static List<IPredicate> extractParentPredicatesForUpdate(ChildTableMeta<?> childMeta
            , Collection<FieldMeta<?, ?>> childUpdatedFieldList
            , List<IPredicate> predicateList) {

        List<IPredicate> parentPredicates;
        // 1. extract parent predicate from where predicate list
        if (childUpdatedFieldList.isEmpty()) {
            parentPredicates = new ArrayList<>(predicateList.size() + 1);
            parentPredicates.addAll(predicateList);
        } else {
            boolean firstIsPrimary = predicateList.get(0) instanceof PrimaryValueEqualPredicate;

            final Collection<FieldMeta<?, ?>> childUpdatedFields = childUpdatedFieldList.size() > 5
                    ? new HashSet<>(childUpdatedFieldList) : childUpdatedFieldList;

            parentPredicates = new ArrayList<>();
            doExtractParentPredicatesForUpdate(predicateList, childUpdatedFields, parentPredicates, firstIsPrimary);
        }

        // 2. append discriminator predicate
        parentPredicates.add(createDiscriminatorPredicate(childMeta));
        return Collections.unmodifiableList(parentPredicates);
    }

    static List<IPredicate> extractParentPredicateForDelete(ChildTableMeta<?> childMeta
            , List<IPredicate> predicateList) {
        // 1. extract parent predicate from where predicate list
        boolean firstIsPrimary = predicateList.get(0) instanceof PrimaryValueEqualPredicate;
        List<IPredicate> parentPredicates = new ArrayList<>();
        doExtractParentPredicateForDelete(childMeta, predicateList, parentPredicates, firstIsPrimary);

        // 2. append discriminator predicate
        parentPredicates.add(createDiscriminatorPredicate(childMeta));
        return Collections.unmodifiableList(parentPredicates);
    }

    static void assertUpdateSetAndWhereClause(InnerUpdate update) {
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
        if (CollectionUtils.isEmpty(update.predicateList())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update must have where clause.");
        }
    }


    static void standardSimpleUpdateSetClause(UpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldMetaList, List<Expression<?>> valueExpList) {
        if (tableMeta.immutable()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] is immutable.", tableAlias);
        }
        Assert.isTrue(fieldMetaList.size() == valueExpList.size()
                , "field list ifAnd value exp list size not match");
        final MappingMode mappingMode = tableMeta.mappingMode();

        if (mappingMode != MappingMode.PARENT) {
            Assert.notEmpty(fieldMetaList, "set clause must not empty");
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
            if (!dialect.supportZoneId()) {
                throw new MetaException("dialec[%s]t not supported zone.", dialect.sqlDialect());
            }
            SQLS.param(now, updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else {
            throw new MetaException("createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }


    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException("domain[%s] field[%s] is non-updatable."
                    , fieldMeta.tableMeta().javaType(), fieldMeta.propertyName());
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
                FieldExpression<?, ?> fieldExp = ((FieldValuePredicate) predicate).fieldExp();
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
        if (parentMeta instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) parentMeta;
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

    static void createStandardInsertForSimple(TableMeta<?> physicalTable, Collection<FieldMeta<?, ?>> fieldMetas
            , ReadonlyWrapper domainWrapper, StandardInsertContext context) {

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
            if (value == null && !fieldMeta.nullable()) {
                continue;
            }
            if (count > 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            context.appendField(fieldMeta);
            if (value == null) {
                context.appendParam(ParamWrapper.build(fieldMeta.mappingMeta(), null));
            } else if (isConstant(fieldMeta)) {
                valueBuilder.append(fieldMeta.mappingMeta().nonNullTextValue(value));
            } else {
                valueBuilder.append("?");
                if (sessionFactory.fieldCodec(fieldMeta) != null) {
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


    static void createStandardBatchInsertForSimple(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> fieldMetas
            , StandardInsertContext context) {

        StringBuilder fieldBuilder = context.fieldsBuilder()
                .append("INSERT INTO");
        // append table name
        context.appendTable(tableMeta);
        fieldBuilder.append(" ( ");

        /// VALUE clause
        StringBuilder valueBuilder = context.sqlBuilder()
                .append(" VALUES ( ");

        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] can't insert ,can't create batch insert template.", fieldMeta);
            }
            if (index > 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            context.appendField(fieldMeta);

            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, tableMeta));
            } else {
                valueBuilder.append("?");
                context.appendParam(new FieldParamWrapperImpl(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static SQLWrapper createBatchInsertWrapper(InnerStandardBatchInsert insert
            , final SQLWrapper sqlWrapper, GenericSessionFactory sessionFactory) {

        final List<IDomain> domainList = insert.valueList();

        List<List<ParamWrapper>> parentParamGroupList, childParamGroupList = new ArrayList<>(domainList.size());
        SimpleSQLWrapper parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            parentWrapper = childSQLWrapper.parentWrapper();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupList = new ArrayList<>(domainList.size());
        } else if (sqlWrapper instanceof SimpleSQLWrapper) {
            parentWrapper = null;
            parentParamGroupList = Collections.emptyList();
            childWrapper = (SimpleSQLWrapper) sqlWrapper;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", sqlWrapper));
        }

        final FieldValuesGenerator valuesGenerator = sessionFactory.fieldValuesGenerator();
        final TableMeta<?> logicTable = insert.tableMeta();
        final boolean migrationData = insert.migrationData();

        for (IDomain domain : domainList) {
            // 1. create value for a domain
            BeanWrapper beanWrapper = valuesGenerator.createValues(logicTable, domain, migrationData);
            // 2. create paramWrapperList
            if (sqlWrapper instanceof ChildSQLWrapper) {
                // create paramWrapperList for parent
                parentParamGroupList.add(
                        createBatchInsertParamList(beanWrapper, sessionFactory, parentWrapper.paramList())
                );
            }
            // create paramWrapperList for child
            childParamGroupList.add(
                    createBatchInsertParamList(beanWrapper, sessionFactory, childWrapper.paramList())
            );
        }
        // 4. create BatchSimpleSQLWrapper
        BatchSimpleSQLWrapper childBatchWrapper = BatchSimpleSQLWrapper.build(childWrapper.sql(), childParamGroupList);
        SQLWrapper batchSQLWrapper;
        if (sqlWrapper instanceof ChildSQLWrapper) {
            BatchSimpleSQLWrapper parentBatchWrapper = BatchSimpleSQLWrapper.build(
                    parentWrapper.sql(), parentParamGroupList);
            batchSQLWrapper = ChildBatchSQLWrapper.build(parentBatchWrapper, childBatchWrapper);
        } else {
            batchSQLWrapper = childBatchWrapper;
        }
        return batchSQLWrapper;
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
            value = CodeEnum.resolve(fieldMeta.javaType(), logicalTable.discriminatorValue());
            if (value == null) {
                throw new MetaException("CodeEnum[%s] not found enum for code[%s]"
                        , fieldMeta.javaType().getName(), logicalTable.discriminatorValue());
            }
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    static SQLWrapper createBatchSQLWrapper(Collection<Object> namedParamList, final SQLWrapper sqlWrapper) {

        List<List<ParamWrapper>> parentParamGroupList, childParamGroupList = new ArrayList<>(namedParamList.size());
        SimpleSQLWrapper parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            parentWrapper = childSQLWrapper.parentWrapper();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupList = new ArrayList<>(namedParamList.size());
        } else if (sqlWrapper instanceof SimpleSQLWrapper) {
            parentWrapper = null;
            parentParamGroupList = Collections.emptyList();
            childWrapper = (SimpleSQLWrapper) sqlWrapper;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", sqlWrapper));
        }

        for (Object paramObject : namedParamList) {
            // 1. create access object
            ReadonlyWrapper readonlyWrapper = AccessorFactory.forReadonlyAccess(paramObject);
            // 2. create param group list
            if (sqlWrapper instanceof ChildSQLWrapper) {
                parentParamGroupList.add(
                        // create paramWrapperList for parent
                        createBatchNamedParamList(readonlyWrapper, parentWrapper.paramList())
                );
            }
            childParamGroupList.add(
                    // create paramWrapperList for child
                    createBatchNamedParamList(readonlyWrapper, childWrapper.paramList())
            );
        }
        // 3. create BatchSimpleSQLWrapper
        BatchSimpleSQLWrapper childBatchWrapper = BatchSimpleSQLWrapper.build(childWrapper.sql()
                , childParamGroupList, childWrapper.hasVersion());
        SQLWrapper batchSQLWrapper;
        if (sqlWrapper instanceof ChildSQLWrapper) {
            BatchSimpleSQLWrapper parentBatchWrapper = BatchSimpleSQLWrapper.build(
                    parentWrapper.sql(), parentParamGroupList, parentWrapper.hasVersion());
            batchSQLWrapper = ChildBatchSQLWrapper.build(parentBatchWrapper, childBatchWrapper);
        } else {
            batchSQLWrapper = childBatchWrapper;
        }
        return batchSQLWrapper;
    }



    /*################################## blow private method ##################################*/

    private static List<ParamWrapper> createBatchNamedParamList(ReadonlyWrapper readonlyWrapper
            , List<ParamWrapper> placeHolder) {

        List<ParamWrapper> paramWrapperList = new ArrayList<>(placeHolder.size());
        for (ParamWrapper paramWrapper : placeHolder) {
            if (paramWrapper instanceof NamedParamExpression) {
                Object value = readonlyWrapper.getPropertyValue(((NamedParamExpression<?>) paramWrapper).name());
                paramWrapperList.add(ParamWrapper.build(paramWrapper.paramMeta(), value));
            } else {
                paramWrapperList.add(paramWrapper);
            }
        }
        return paramWrapperList.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(paramWrapperList);
    }

    private static List<ParamWrapper> createBatchInsertParamList(BeanWrapper beanWrapper
            , GenericSessionFactory sessionFactory, List<ParamWrapper> placeHolderList) {

        List<ParamWrapper> paramWrapperList = new ArrayList<>(placeHolderList.size());
        for (ParamWrapper placeHolder : placeHolderList) {
            if (placeHolder instanceof FieldParamWrapper) {
                FieldMeta<?, ?> fieldMeta = ((FieldParamWrapper) placeHolder).paramMeta();
                ParamWrapper actualParamWrapper;
                Object value = beanWrapper.getPropertyValue(fieldMeta.propertyName());
                if (sessionFactory.fieldCodec(fieldMeta) != null) {
                    actualParamWrapper = ParamWrapper.build(fieldMeta, value);
                } else {
                    actualParamWrapper = ParamWrapper.build(fieldMeta.mappingMeta(), value);
                }
                paramWrapperList.add(actualParamWrapper);
            } else {
                paramWrapperList.add(placeHolder);
            }
        }
        return paramWrapperList.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(paramWrapperList);
    }


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
        fieldMetaSet.add(physicalTable.id());
    }

    private static void doExtractParentPredicatesForUpdate(List<IPredicate> predicateList
            , Collection<FieldMeta<?, ?>> childUpdatedFields, List<IPredicate> newPredicates
            , final boolean firstIsPrimary) {
        for (IPredicate predicate : predicateList) {
            if (predicate instanceof OrPredicate) {
                extractParentPredicatesFromOrPredicateForDelete((OrPredicate) predicate
                        , childUpdatedFields, newPredicates, firstIsPrimary);
            } else if (predicate.containsField(childUpdatedFields)) {
                if (!firstIsPrimary) {
                    throw createNoPrimaryPredicateException(childUpdatedFields);
                }
            } else {
                newPredicates.add(predicate);
            }
        }
    }

    private static void extractParentPredicatesFromOrPredicateForDelete(OrPredicate orPredicate
            , Collection<FieldMeta<?, ?>> childUpdatedFields, List<IPredicate> newPredicates
            , final boolean firstIsPrimary) {

        //firstly, handle left
        final IPredicate left = orPredicate.leftPredicate();
        IPredicate newLeft;
        if (left instanceof OrPredicate) {
            List<IPredicate> newLeftList = new ArrayList<>();
            extractParentPredicatesFromOrPredicateForDelete((OrPredicate) left, childUpdatedFields, newLeftList
                    , firstIsPrimary);
            if (newLeftList.size() == 0) {
                newLeft = ((OrPredicate) left).leftPredicate();
            } else if (newLeftList.size() == 1) {
                newLeft = newLeftList.get(0);
            } else {
                // here ,left is drop for contains childField
                newPredicates.addAll(newLeftList);
                newLeft = null;
            }
        } else if (left.containsField(childUpdatedFields)) {
            if (!firstIsPrimary) {
                throw createNoPrimaryPredicateException(childUpdatedFields);
            }
            newLeft = null;
        } else {
            newLeft = left;
        }
        // secondly,handle right
        List<IPredicate> newRightPredicates = new ArrayList<>();
        doExtractParentPredicatesForUpdate(orPredicate.rightPredicate(), childUpdatedFields, newRightPredicates, firstIsPrimary);

        if (newLeft == null) {
            newPredicates.addAll(newRightPredicates);
        } else if (newRightPredicates.isEmpty()) {
            newPredicates.add(newLeft);
        } else {
            newPredicates.add(newLeft.or(newRightPredicates));
        }
    }


    private static void doExtractParentPredicateForDelete(ChildTableMeta<?> childMeta
            , List<IPredicate> predicateList, List<IPredicate> newPredicates, boolean firstIsPrimary) {
        for (IPredicate predicate : predicateList) {
            if (predicate instanceof OrPredicate) {
                extractParentPredicatesFromOrPredicateForDelete((OrPredicate) predicate
                        , childMeta, newPredicates, firstIsPrimary);
            } else if (predicate.containsFieldOf(childMeta)) {
                if (!firstIsPrimary) {
                    throw createNoPrimaryPredicateException(childMeta);
                }
            } else {
                newPredicates.add(predicate);
            }
        }
    }

    private static void extractParentPredicatesFromOrPredicateForDelete(OrPredicate orPredicate
            , ChildTableMeta<?> childMeta, List<IPredicate> newPredicates
            , final boolean firstIsPrimary) {

        //firstly, handle left
        final IPredicate left = orPredicate.leftPredicate();
        IPredicate newLeft;
        if (left instanceof OrPredicate) {
            List<IPredicate> newLeftList = new ArrayList<>();
            extractParentPredicatesFromOrPredicateForDelete((OrPredicate) left, childMeta, newLeftList
                    , firstIsPrimary);
            if (newLeftList.size() == 0) {
                newLeft = ((OrPredicate) left).leftPredicate();
            } else if (newLeftList.size() == 1) {
                newLeft = newLeftList.get(0);
            } else {
                // here ,left is drop for contains childField
                newPredicates.addAll(newLeftList);
                newLeft = null;
            }
        } else if (left.containsFieldOf(childMeta)) {
            if (!firstIsPrimary) {
                throw createNoPrimaryPredicateException(childMeta);
            }
            newLeft = null;
        } else {
            newLeft = left;
        }
        // secondly,handle right
        List<IPredicate> newRightPredicates = new ArrayList<>();
        doExtractParentPredicateForDelete(childMeta, orPredicate.rightPredicate(), newRightPredicates, firstIsPrimary);

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

    private static CriteriaException createNoPrimaryPredicateException(ChildTableMeta<?> childMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "detect ChildTableMeta[%s] but first predicate isn't primary field predicate."
                , childMeta);
    }


    private static IPredicate createDiscriminatorPredicate(ChildTableMeta<?> tableMeta) {
        FieldMeta<?, ? extends CodeEnum> fieldMeta = tableMeta.discriminator();
        @SuppressWarnings("unchecked")
        FieldMeta<?, CodeEnum> enumFieldMeta = (FieldMeta<?, CodeEnum>) fieldMeta;
        CodeEnum codeEnum = CodeEnum.resolve(enumFieldMeta.javaType(), tableMeta.discriminatorValue());

        if (codeEnum == null) {
            throw new MetaException("ChildTableMeta[%s] discriminatorValue[%s] can't resolve CodeEnum."
                    , tableMeta, tableMeta.discriminatorValue());
        }
        return enumFieldMeta.equal(codeEnum);
    }

    private static final class FieldParamWrapperImpl implements FieldParamWrapper {


        private final FieldMeta<?, ?> fieldMeta;

        private FieldParamWrapperImpl(FieldMeta<?, ?> fieldMeta) {
            this.fieldMeta = fieldMeta;
        }


        @Override
        public final FieldMeta<?, ?> paramMeta() {
            return this.fieldMeta;
        }

        @Override
        public final Object value() {
            throw new UnsupportedOperationException();
        }
    }
}
