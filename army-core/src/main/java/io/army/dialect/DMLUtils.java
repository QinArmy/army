package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericRmSessionFactory;
import io.army.GenericSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.Sqls;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.stmt.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

abstract class DMLUtils {

    DMLUtils() {
        throw new UnsupportedOperationException();
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

    static List<IPredicate> createParentPredicates(ParentTableMeta<?> parentMeta, List<IPredicate> predicateList) {
        List<IPredicate> parentPredicateList;
        if (hasDiscriminatorPredicate(predicateList, parentMeta.discriminator())) {
            parentPredicateList = predicateList;
        } else {
            parentPredicateList = new ArrayList<>(predicateList.size() + 1);
            parentPredicateList.addAll(predicateList);
            parentPredicateList.add(createDiscriminatorPredicate(parentMeta));
            parentPredicateList = Collections.unmodifiableList(parentPredicateList);
        }
        return parentPredicateList;
    }

    static List<IPredicate> extractParentPredicateForDelete(ChildTableMeta<?> childMeta
            , List<IPredicate> predicateList) {
        // 1. extract parent predicate from where predicate list
        final IPredicate firstPredicate = predicateList.get(0);
        // 1-1. check first predicate
        if (!(firstPredicate instanceof PrimaryValueEqualPredicate)) {
            throw createNoPrimaryPredicateException(childMeta);
        }
        List<IPredicate> parentPredicates = new ArrayList<>();
        // do extract parent predicate
        for (IPredicate predicate : predicateList) {
            if (predicate == firstPredicate) {
                continue;
            }
            if (!predicate.containsFieldOf(childMeta)) {
                parentPredicates.add(predicate);
            }
        }

        // 2. append discriminator predicate
        parentPredicates.add(createDiscriminatorPredicate(childMeta));
        return Collections.unmodifiableList(parentPredicates);
    }

    static void assertUpdateSetAndWhereClause(InnerUpdate update) {
        List<? extends SetTargetPart> targetFieldList = update.targetFieldList();
        if (CollectionUtils.isEmpty(targetFieldList)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update must have set clause.");
        }
        List<? extends SetValuePart> valueExpList = update.valueExpList();
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
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] alias[%s] is immutable."
                    , tableMeta,tableAlias);
        }
        Assert.isTrue(fieldMetaList.size() == valueExpList.size()
                , "field list ifAnd value exp list size not match");
        final MappingMode mappingMode = tableMeta.mappingMode();

        if (mappingMode != MappingMode.PARENT) {
            Assert.notEmpty(fieldMetaList, "set clause must not empty");
        }

        SQLBuilder builder = context.sqlBuilder()
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
        SQLBuilder builder = context.sqlBuilder();

        context.appendField(tableAlias, versionField);

        builder.append(" =");
        context.appendField(tableAlias, versionField);
        builder.append(" + 1 ,");

        //2. updateTime field
        final FieldMeta<?, ?> updateTimeField = tableMeta.getField(TableMeta.UPDATE_TIME);
        // updateTime field self-describe
        context.appendField(tableAlias, updateTimeField);
        builder.append(" =");

        final Dialect dialect = context.dialect();
        final ZonedDateTime now = ZonedDateTime.now(dialect.zoneId());

        if (updateTimeField.javaType() == LocalDateTime.class) {
            Sqls.param(now.toLocalDateTime(), updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            if (!dialect.supportZone()) {
                throw new MetaException("dialec[%s]t not supported zone.", dialect.database());
            }
            Sqls.param(now, updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else {
            throw new MetaException("createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }


    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException("FieldMeta[%s] is non-updatable."
                    , fieldMeta);
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
                GenericField<?, ?> fieldExp = ((FieldValuePredicate) predicate).fieldMeta();
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
            fieldMetaSet.add(parentMeta.getField(TableMeta.CREATE_TIME));
        }
        if (parentMeta.mappingProp(TableMeta.UPDATE_TIME)) {
            fieldMetaSet.add(parentMeta.getField(TableMeta.UPDATE_TIME));
        }
        if (parentMeta.mappingProp(TableMeta.VERSION)) {
            fieldMetaSet.add(parentMeta.getField(TableMeta.VERSION));
        }
        return Collections.unmodifiableSet(fieldMetaSet);
    }

    static void createValueInsertForSimple(TableMeta<?> physicalTable, TableMeta<?> logicalTable
            , Collection<FieldMeta<?, ?>> fieldMetas, ReadonlyWrapper domainWrapper
            , StandardValueInsertContext context) {

        final GenericSessionFactory sessionFactory = context.dialect.sessionFactory();
        final SQLBuilder fieldBuilder = context.fieldsBuilder().append("INSERT INTO");
        // append table name
        context.appendTable(physicalTable, null);
        context.fieldsBuilder().append(" (");

        final SQLBuilder valueBuilder = context.sqlBuilder()
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
                context.appendParam(ParamValue.build(fieldMeta.mappingMeta(), null));
            } else if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, logicalTable));
            } else {
                valueBuilder.append("?");
                if (sessionFactory.fieldCodec(fieldMeta) != null) {
                    context.appendParam(ParamValue.build(fieldMeta, value));
                } else {
                    context.appendParam(ParamValue.build(fieldMeta.mappingMeta(), value));
                }

            }
            count++;
        }

        fieldBuilder.append(" )");
        valueBuilder.append(" )");

    }


    static void createStandardBatchInsertForSimple(TableMeta<?> physicalTable, TableMeta<?> logicalTable
            , Collection<FieldMeta<?, ?>> fieldMetas
            , StandardValueInsertContext context) {

        SQLBuilder fieldBuilder = context.fieldsBuilder()
                .append("INSERT INTO");
        // append table name
        context.appendTable(physicalTable, null);
        fieldBuilder.append(" ( ");

        /// VALUE clause
        SQLBuilder valueBuilder = context.sqlBuilder()
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
                valueBuilder.append(createConstant(fieldMeta, logicalTable));
            } else {
                valueBuilder.append("?");
                context.appendParam(new FieldParamValueImpl(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static Stmt createBatchInsertWrapper(InnerStandardBatchInsert insert
            , final Stmt stmt, GenericRmSessionFactory sessionFactory) {

        final List<DomainWrapper> domainWrapperList = insert.wrapperList();

        List<List<ParamValue>> parentParamGroupList, childParamGroupList = new ArrayList<>(domainWrapperList.size());
        SimpleStmt parentWrapper, childWrapper;
        List<ParamValue> parentPlaceholderList;
        // extract parentWrapper,childWrapper
        if (stmt instanceof ChildStmt) {
            ChildStmt childSQLWrapper = (ChildStmt) stmt;
            parentWrapper = childSQLWrapper.parentWrapper();
            parentPlaceholderList = parentWrapper.paramGroup();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupList = new ArrayList<>(domainWrapperList.size());
        } else if (stmt instanceof SimpleStmt) {
            parentWrapper = null;
            parentPlaceholderList = null;
            parentParamGroupList = Collections.emptyList();
            childWrapper = (SimpleStmt) stmt;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", stmt));
        }

        final List<ParamValue> childPlaceholderList = childWrapper.paramGroup();
        final DomainValuesGenerator generator = sessionFactory.domainValuesGenerator();
        final boolean migrationData = insert.migrationData();

        for (DomainWrapper wrapper : domainWrapperList) {
            //1. create domain  property value.
            generator.createValues(wrapper, migrationData);
            // 2. create paramWrapperList
            if (parentPlaceholderList != null) {
                // create paramWrapperList for parent
                parentParamGroupList.add(createBatchInsertParamList(wrapper, parentPlaceholderList));
            }
            //3. create paramWrapperList for child
            childParamGroupList.add(createBatchInsertParamList(wrapper, childPlaceholderList));
        }

        // 4. create BatchSimpleSQLWrapper
        BatchSimpleStmt childBatchWrapper = BatchSimpleStmt.build(childWrapper.sql(), childParamGroupList);
        Stmt batchStmt;
        if (stmt instanceof ChildStmt) {
            BatchSimpleStmt parentBatchWrapper = BatchSimpleStmt.build(
                    parentWrapper.sql(), parentParamGroupList);
            batchStmt = ChildBatchStmt.build(parentBatchWrapper, childBatchWrapper);
        } else {
            batchStmt = childBatchWrapper;
        }
        return batchStmt;
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

    static Stmt createBatchSQLWrapper(List<? extends ReadonlyWrapper> namedParamList
            , final Stmt stmt) {

        List<List<ParamValue>> parentParamGroupList, childParamGroupList = new ArrayList<>(namedParamList.size());
        SimpleStmt parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        List<ParamValue> parentPlaceholderList;
        if (stmt instanceof ChildStmt) {
            ChildStmt childSQLWrapper = (ChildStmt) stmt;
            parentWrapper = childSQLWrapper.parentWrapper();
            parentPlaceholderList = parentWrapper.paramGroup();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupList = new ArrayList<>(namedParamList.size());
        } else if (stmt instanceof SimpleStmt) {
            parentWrapper = null;
            parentPlaceholderList = Collections.emptyList();
            parentParamGroupList = Collections.emptyList();
            childWrapper = (SimpleStmt) stmt;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", stmt));
        }

        final List<ParamValue> childPlaceholderList = childWrapper.paramGroup();
        final int size = namedParamList.size();
        for (ReadonlyWrapper readonlyWrapper : namedParamList) {
            // 1. create access object
            // 2. create param group list
            if (stmt instanceof ChildStmt) {
                parentParamGroupList.add(
                        // create paramWrapperList for parent
                        createBatchNamedParamList(readonlyWrapper, parentPlaceholderList)
                );
            }
            childParamGroupList.add(
                    // create paramWrapperList for child
                    createBatchNamedParamList(readonlyWrapper, childPlaceholderList)
            );
        }

        // 3. create BatchSimpleSQLWrapper
        BatchSimpleStmt childBatchWrapper = BatchSimpleStmt.build(childWrapper.sql()
                , childParamGroupList, childWrapper.hasVersion());
        Stmt batchStmt;
        if (stmt instanceof ChildStmt) {
            BatchSimpleStmt parentBatchWrapper = BatchSimpleStmt.build(
                    parentWrapper.sql(), parentParamGroupList, parentWrapper.hasVersion());
            batchStmt = ChildBatchStmt.build(parentBatchWrapper, childBatchWrapper);
        } else {
            batchStmt = childBatchWrapper;
        }
        return batchStmt;
    }



    /*################################## blow private method ##################################*/

    /**
     * @return a unmodifiable list
     */
    private static List<ParamValue> createBatchNamedParamList(ReadonlyWrapper readonlyWrapper
            , List<ParamValue> placeHolder) {

        List<ParamValue> paramValueList = new ArrayList<>(placeHolder.size());
        for (ParamValue paramValue : placeHolder) {
            if (paramValue instanceof NamedParamExpression) {
                Object value = readonlyWrapper.getPropertyValue(((NamedParamExpression<?>) paramValue).name());
                paramValueList.add(ParamValue.build(paramValue.paramMeta(), value));
            } else {
                paramValueList.add(paramValue);
            }
        }
        return Collections.unmodifiableList(paramValueList);
    }

    /**
     * @return a unmodifiable list
     */
    private static List<ParamValue> createBatchInsertParamList(ReadonlyWrapper beanWrapper
            , List<ParamValue> placeHolderList) {

        List<ParamValue> paramValueList = new ArrayList<>(placeHolderList.size());
        for (ParamValue placeHolder : placeHolderList) {
            if (placeHolder instanceof FieldParamValue) {
                FieldMeta<?, ?> fieldMeta = ((FieldParamValue) placeHolder).paramMeta();
                Object value = beanWrapper.getPropertyValue(fieldMeta.propertyName());
                paramValueList.add(ParamValue.build(fieldMeta, value));
            } else {
                paramValueList.add(placeHolder);
            }
        }
        return Collections.unmodifiableList(paramValueList);
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
            if (predicate.containsField(childUpdatedFields)) {
                if (!firstIsPrimary) {
                    throw createNoPrimaryPredicateException(childUpdatedFields);
                }
            } else {
                newPredicates.add(predicate);
            }
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


    private static IPredicate createDiscriminatorPredicate(TableMeta<?> tableMeta) {
        FieldMeta<?, ? extends CodeEnum> fieldMeta = tableMeta.discriminator();
        Assert.notNull(fieldMeta, () -> String.format("TableMeta[%s] discriminator is null.", tableMeta));
        @SuppressWarnings("unchecked")
        FieldMeta<?, CodeEnum> enumFieldMeta = (FieldMeta<?, CodeEnum>) fieldMeta;
        CodeEnum codeEnum = CodeEnum.resolve(enumFieldMeta.javaType(), tableMeta.discriminatorValue());

        if (codeEnum == null) {
            throw new MetaException("TableMeta[%s] discriminatorValue[%s] can't resolve CodeEnum."
                    , tableMeta, tableMeta.discriminatorValue());
        }
        return enumFieldMeta.equal(codeEnum);
    }

    private static boolean hasDiscriminatorPredicate(List<IPredicate> predicateList
            , FieldMeta<?, ? extends CodeEnum> discriminator) {
        final Class<?> discriminatorClass = discriminator.javaType();
        boolean has = false;
        for (IPredicate predicate : predicateList) {
            if (predicate instanceof FieldValuePredicate) {
                FieldValuePredicate valuePredicate = (FieldValuePredicate) predicate;
                if (valuePredicate.operator() == DualPredicateOperator.EQ
                        && valuePredicate.fieldMeta().fieldMeta() == discriminator
                        && discriminatorClass.isInstance(valuePredicate.value())) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    private static final class FieldParamValueImpl implements FieldParamValue {


        private final FieldMeta<?, ?> fieldMeta;

        private FieldParamValueImpl(FieldMeta<?, ?> fieldMeta) {
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
