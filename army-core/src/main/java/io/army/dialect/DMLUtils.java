package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.lang.Nullable;
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
            if (!dialect.supportZone()) {
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

    static void createValueInsertForSimple(TableMeta<?> physicalTable, Collection<FieldMeta<?, ?>> fieldMetas
            , ReadonlyWrapper domainWrapper, StandardValueInsertContext context) {

        final GenericSessionFactory sessionFactory = context.dialect.sessionFactory();
        final StringBuilder fieldBuilder = context.fieldsBuilder().append("INSERT INTO");
        // append table name
        context.appendTable(physicalTable, null);
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
            , StandardValueInsertContext context) {

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
            , final SQLWrapper sqlWrapper, @Nullable Set<Integer> domainIndexSet
            , GenericSessionFactory sessionFactory) {

        final List<IDomain> domainList = insert.valueList();

        int groupMapCapacity;
        if (domainIndexSet == null) {
            groupMapCapacity = (int) (domainList.size() / 0.75F);
        } else {
            groupMapCapacity = (int) (domainIndexSet.size() / 0.75F);
        }

        Map<Integer, List<ParamWrapper>> parentParamGroupMap, childParamGroupMap = new HashMap<>(groupMapCapacity);
        SimpleSQLWrapper parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            parentWrapper = childSQLWrapper.parentWrapper();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupMap = new HashMap<>(groupMapCapacity);
        } else if (sqlWrapper instanceof SimpleSQLWrapper) {
            parentWrapper = null;
            parentParamGroupMap = Collections.emptyMap();
            childWrapper = (SimpleSQLWrapper) sqlWrapper;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", sqlWrapper));
        }

        final List<ParamWrapper> childPlaceholderList = childWrapper.paramList();

        if (domainIndexSet == null) {
            final int size = domainList.size();
            for (int i = 0; i < size; i++) {
                // 1. create value for a domain
                ReadonlyWrapper beanWrapper = ObjectAccessorFactory.forReadonlyAccess(domainList.get(i));
                // 2. create paramWrapperList
                if (sqlWrapper instanceof ChildSQLWrapper) {
                    // create paramWrapperList for parent
                    parentParamGroupMap.put(i
                            , createBatchInsertParamList(beanWrapper, sessionFactory, parentWrapper.paramList()));
                }
                //3. create paramWrapperList for child
                childParamGroupMap.put(i
                        , createBatchInsertParamList(beanWrapper, sessionFactory, childPlaceholderList));
            }
        } else {
            for (Integer domainIndex : domainIndexSet) {
                // 1. create value for a domain
                ReadonlyWrapper beanWrapper = ObjectAccessorFactory.forReadonlyAccess(domainList.get(domainIndex));
                // 2. create paramWrapperList
                if (sqlWrapper instanceof ChildSQLWrapper) {
                    // create paramWrapperList for parent
                    parentParamGroupMap.put(domainIndex
                            , createBatchInsertParamList(beanWrapper, sessionFactory, parentWrapper.paramList()));
                }
                //3. create paramWrapperList for child
                childParamGroupMap.put(domainIndex
                        , createBatchInsertParamList(beanWrapper, sessionFactory, childPlaceholderList));
            }
        }

        // 4. create BatchSimpleSQLWrapper
        BatchSimpleSQLWrapper childBatchWrapper = BatchSimpleSQLWrapper.build(childWrapper.sql(), childParamGroupMap);
        SQLWrapper batchSQLWrapper;
        if (sqlWrapper instanceof ChildSQLWrapper) {
            BatchSimpleSQLWrapper parentBatchWrapper = BatchSimpleSQLWrapper.build(
                    parentWrapper.sql(), parentParamGroupMap);
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

    static SQLWrapper createBatchSQLWrapper(List<Object> namedParamList, @Nullable Set<Integer> namedParamIexSet
            , final SQLWrapper sqlWrapper) {

        int groupMapCapacity;
        if (namedParamIexSet == null) {
            groupMapCapacity = (int) (namedParamList.size() / 0.75F);
        } else {
            groupMapCapacity = (int) (namedParamIexSet.size() / 0.75F);
        }

        Map<Integer, List<ParamWrapper>> parentParamGroupMap, childParamGroupMap = new HashMap<>(groupMapCapacity);
        SimpleSQLWrapper parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            parentWrapper = childSQLWrapper.parentWrapper();
            childWrapper = childSQLWrapper.childWrapper();
            parentParamGroupMap = new HashMap<>(groupMapCapacity);
        } else if (sqlWrapper instanceof SimpleSQLWrapper) {
            parentWrapper = null;
            parentParamGroupMap = Collections.emptyMap();
            childWrapper = (SimpleSQLWrapper) sqlWrapper;
        } else {
            throw new IllegalArgumentException(String.format("SQLWrapper[%s] supported", sqlWrapper));
        }

        final List<ParamWrapper> childPlaceholderList = childWrapper.paramList();
        if (namedParamIexSet == null) {
            final int size = namedParamList.size();
            for (int i = 0; i < size; i++) {
                // 1. create access object
                ReadonlyWrapper readonlyWrapper = ObjectAccessorFactory.forReadonlyAccess(namedParamList.get(i));
                // 2. create param group list
                if (sqlWrapper instanceof ChildSQLWrapper) {
                    parentParamGroupMap.put(i
                            // create paramWrapperList for parent
                            , createBatchNamedParamList(readonlyWrapper, parentWrapper.paramList())
                    );
                }
                childParamGroupMap.put(i
                        // create paramWrapperList for child
                        , createBatchNamedParamList(readonlyWrapper, childPlaceholderList)
                );
            }
        } else {
            for (Integer namedParamIndex : namedParamIexSet) {
                // 1. create access object
                ReadonlyWrapper readonlyWrapper = ObjectAccessorFactory.forReadonlyAccess(
                        namedParamList.get(namedParamIndex));
                // 2. create param group list
                if (sqlWrapper instanceof ChildSQLWrapper) {
                    parentParamGroupMap.put(namedParamIndex
                            // create paramWrapperList for parent
                            , createBatchNamedParamList(readonlyWrapper, parentWrapper.paramList())
                    );
                }
                childParamGroupMap.put(namedParamIndex
                        // create paramWrapperList for child
                        , createBatchNamedParamList(readonlyWrapper, childPlaceholderList)
                );
            }
        }

        // 3. create BatchSimpleSQLWrapper
        BatchSimpleSQLWrapper childBatchWrapper = BatchSimpleSQLWrapper.build(childWrapper.sql()
                , childParamGroupMap, childWrapper.hasVersion());
        SQLWrapper batchSQLWrapper;
        if (sqlWrapper instanceof ChildSQLWrapper) {
            BatchSimpleSQLWrapper parentBatchWrapper = BatchSimpleSQLWrapper.build(
                    parentWrapper.sql(), parentParamGroupMap, parentWrapper.hasVersion());
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
        return Collections.unmodifiableList(paramWrapperList);
    }

    private static List<ParamWrapper> createBatchInsertParamList(ReadonlyWrapper beanWrapper
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
        return Collections.unmodifiableList(paramWrapperList);
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
                        && valuePredicate.fieldExp().fieldMeta() == discriminator
                        && discriminatorClass.isInstance(valuePredicate.value())) {
                    has = true;
                    break;
                }
            }
        }
        return has;
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
