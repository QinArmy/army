package io.army.dialect;

import io.army.ErrorCode;
import io.army.annotation.UpdateMode;
import io.army.beans.ObjectWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;
import io.army.sharding.Route;
import io.army.sharding.ShardingRoute;
import io.army.sharding.TableRoute;
import io.army.stmt.*;
import io.army.struct.CodeEnum;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;
import io.qinarmy.util.Pair;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

abstract class DmlUtils {

    DmlUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a unmodified map
     */
    static Map<Byte, List<ObjectWrapper>> insertSharding(GenericRmSessionFactory factory, _ValuesInsert insert) {

        final FactoryMode mode = factory.factoryMode();
        final TableMeta<?> tableMeta = insert.table();
        final int databaseIndex = factory.databaseIndex();

        final List<FieldMeta<?, ?>> databaseFields, tableFields;
        databaseFields = tableMeta.databaseRouteFields();
        tableFields = tableMeta.tableRouteFields();
        if (databaseFields.size() == 0 && databaseIndex != 0) {
            throw _Exceptions.databaseRouteError(insert, factory);
        }

        final int tableCount = factory.tableCountPerDatabase();
        final Route route = factory.tableRoute(tableMeta);
        final DomainValuesGenerator generator = factory.domainValuesGenerator();

        final boolean checkDatabase = mode == FactoryMode.SHARDING && databaseFields.size() > 0;
        final boolean tableSharding = tableFields.size() > 0;
        final boolean migration = insert.migrationData();
        final Map<Byte, List<ObjectWrapper>> domainMap = new HashMap<>();
        for (ObjectWrapper domain : insert.domainList()) {

            generator.createValues(domain, migration); // create required values

            Object value;
            byte tableIndex;
            if (tableSharding) {
                tableIndex = -1;
                for (FieldMeta<?, ?> fieldMeta : tableFields) {
                    value = domain.get(fieldMeta.fieldName());
                    if (value == null) {
                        continue;
                    }
                    tableIndex = ((TableRoute) route).table(fieldMeta, value);
                    break;
                }
            } else {
                tableIndex = 0;
            }
            if (tableIndex < 0 || tableIndex >= tableCount) {
                throw _Exceptions.noTableRoute(insert, factory);
            }

            domainMap.computeIfAbsent(tableIndex, k -> new ArrayList<>())
                    .add(domain);

            if (!checkDatabase) {
                continue;
            }
            value = null;
            for (FieldMeta<?, ?> fieldMeta : databaseFields) {
                value = domain.get(fieldMeta.fieldName());
                if (value == null) {
                    continue;
                }
                if (((ShardingRoute) route).database(fieldMeta, value) != databaseIndex) {
                    throw _Exceptions.databaseRouteError(insert, factory);
                }
                break;
            }
            if (value == null) {
                throw _Exceptions.databaseRouteError(insert, factory);
            }

        }
        return Collections.unmodifiableMap(domainMap);
    }


    static void appendStandardValueInsert(final StandardValueInsertContext context) {
        final Dialect dialect = context.dialect;
        final StringBuilder builder = context.sqlBuilder();
        builder.append(Constant.INSERT_INTO)
                .append(Constant.SPACE);
        builder.append(dialect.safeTableName(context.actualTable, context.tableSuffix()));// append table name
        builder.append(Constant.LEFT_BRACKET);
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : context.fields()) {
            if (index > 0) {
                builder.append(Constant.COMMA);
            }
            builder.append(dialect.safeFieldName(fieldMeta));
            index++;
        }
        builder.append(Constant.RIGHT_BRACKET)
                .append(Constant.VALUES);

        final List<ObjectWrapper> domainList = context.domainList();
        final FieldMeta<?, ?> discriminator = context.table.discriminator();
        final List<FieldMeta<?, ?>> fieldList = context.fields();
        int batch = 0;
        final Map<FieldMeta<?, ?>, _Expression<?>> expMap = context.commonExpMap();
        _Expression<?> expression;
        for (ObjectWrapper domain : domainList) {
            if (batch > 0) {
                builder.append(Constant.COMMA);
            }
            builder.append(Constant.LEFT_BRACKET);
            index = 0;
            for (FieldMeta<?, ?> fieldMeta : fieldList) {
                if (index > 0) {
                    builder.append(Constant.COMMA);
                }
                if (fieldMeta == discriminator) {
                    builder.append(fieldMeta.tableMeta().discriminatorValue());
                } else if ((expression = expMap.get(fieldMeta)) != null) {
                    expression.appendSql(context);
                } else {
                    builder.append(Constant.PLACEHOLDER);
                    context.appendParam(ParamValue.build(fieldMeta, domain.get(fieldMeta.fieldName())));
                }
                index++;
            }
            builder.append(Constant.RIGHT_BRACKET);
            batch++;
        }

    }


    static List<FieldMeta<?, ?>> mergeInsertFields(final boolean parent, final _ValuesInsert insert) {
        final TableMeta<?> tableMeta;
        final List<FieldMeta<?, ?>> fieldList;
        if (parent) {
            tableMeta = ((ChildTableMeta<?>) insert.table()).parentMeta();
            fieldList = insert.parentFieldList();
        } else {
            tableMeta = insert.table();
            fieldList = insert.fieldList();
        }
        final List<FieldMeta<?, ?>> mergeFieldList;
        if (fieldList.isEmpty()) {
            final Collection<?> fieldCollection = tableMeta.fieldCollection();
            mergeFieldList = new ArrayList<>(fieldCollection.size());
            @SuppressWarnings("unchecked")
            Collection<FieldMeta<?, ?>> tableFields = (Collection<FieldMeta<?, ?>>) fieldCollection;
            for (FieldMeta<?, ?> fieldMeta : tableFields) {
                if (fieldMeta.insertable()) {
                    mergeFieldList.add(fieldMeta);
                }
            }
        } else {
            final Set<FieldMeta<?, ?>> fieldSet = new HashSet<>();
            for (FieldMeta<?, ?> fieldMeta : fieldList) {
                if (fieldMeta.tableMeta() != tableMeta) {
                    throw _Exceptions.notMatchInsertField(insert, fieldMeta);
                }
                if (!fieldMeta.insertable()) {
                    throw _Exceptions.nonInsertable(fieldMeta);
                }
                fieldSet.add(fieldMeta);
            }
            appendInsertFields(tableMeta, fieldSet);
            mergeFieldList = new ArrayList<>(fieldSet);
        }
        return Collections.unmodifiableList(mergeFieldList);
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


    static Pair<List<FieldMeta<?, ?>>, List<FieldMeta<?, ?>>> divideField(_ValuesInsert insert) {
        final TableMeta<?> tableMeta = insert.table();
        final ParentTableMeta<?> parentMeta;

        if (tableMeta instanceof ChildTableMeta) {
            parentMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
        } else {
            parentMeta = null;
        }
        final List<FieldMeta<?, ?>> fieldMetas = insert.fieldList();
        if (fieldMetas.isEmpty()) {
            final List<FieldMeta<?, ?>> fieldList, parentFieldList;
            fieldList = Collections.unmodifiableList(new ArrayList<>(tableMeta.fieldCollection()));

            if (parentMeta == null) {
                parentFieldList = Collections.emptyList();
            } else {
                parentFieldList = Collections.unmodifiableList(new ArrayList<>(parentMeta.fieldCollection()));
            }
            return new Pair<>(fieldList, parentFieldList);
        }

        TableMeta<?> belongOfTable;
        final Set<FieldMeta<?, ?>> fieldSet = new HashSet<>(), parentFieldSet;
        if (parentMeta == null) {
            parentFieldSet = Collections.emptySet();
        } else {
            parentFieldSet = new HashSet<>();
        }
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            belongOfTable = fieldMeta.tableMeta();
            if (belongOfTable == tableMeta) {
                fieldSet.add(fieldMeta);
            } else if (belongOfTable == parentMeta) {
                parentFieldSet.add(fieldMeta);
            }
        }
        fieldSet.addAll(tableMeta.generatorChain());
        fieldSet.add(tableMeta.id());
        appendInsertFields(tableMeta, fieldSet);
        if (parentMeta != null) {
            appendInsertFields(parentMeta, parentFieldSet);
        }
        final List<FieldMeta<?, ?>> fieldList, parentFieldList;
        fieldList = Collections.unmodifiableList(new ArrayList<>(fieldSet));
        if (parentMeta == null) {
            parentFieldList = Collections.emptyList();
        } else {
            parentFieldList = Collections.unmodifiableList(new ArrayList<>(parentFieldSet));
        }
        return new Pair<>(fieldList, parentFieldList);
    }


    static List<_Predicate> extractParentPredicatesForUpdate(ChildTableMeta<?> childMeta
            , Collection<FieldMeta<?, ?>> childUpdatedFieldList
            , List<_Predicate> predicateList) {

        List<_Predicate> parentPredicates;
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

    static List<_Predicate> createParentPredicates(ParentTableMeta<?> parentMeta, List<_Predicate> predicateList) {
        List<_Predicate> parentPredicateList;
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

    static List<_Predicate> extractParentPredicateForDelete(ChildTableMeta<?> childMeta
            , List<_Predicate> predicateList) {
        // 1. extract parent predicate from where predicate list
        final _Predicate firstPredicate = predicateList.get(0);
        // 1-1. check first predicate
        if (!(firstPredicate instanceof PrimaryValueEqualPredicate)) {
            throw createNoPrimaryPredicateException(childMeta);
        }
        List<_Predicate> parentPredicates = new ArrayList<>();
        // do extract parent predicate
        for (_Predicate predicate : predicateList) {
            if (predicate == firstPredicate) {
                continue;
            }
//            if (!predicate.containsFieldOf(childMeta)) {
//                parentPredicates.add(predicate);
//            }
        }

        // 2. append discriminator predicate
        parentPredicates.add(createDiscriminatorPredicate(childMeta));
        return Collections.unmodifiableList(parentPredicates);
    }

    static void assertUpdateSetAndWhereClause(_Update update) {
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
            , List<FieldMeta<?, ?>> fieldMetaList, List<_Expression<?>> valueExpList) {
//        if (tableMeta.immutable()) {
//            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] alias[%s] is immutable."
//                    , tableMeta, tableAlias);
//        }
//        Assert.isTrue(fieldMetaList.size() == valueExpList.size()
//                , "field list ifAnd value exp list size not match");
//        final MappingMode mappingMode = tableMeta.mappingMode();
//
//        if (mappingMode != MappingMode.PARENT) {
//            Assert.notEmpty(fieldMetaList, "set clause must not empty");
//        }
//
//        StringBuilder builder = context.sqlBuilder()
//                .append(" SET");
//
//        final int size = fieldMetaList.size();
//        for (int i = 0; i < size; i++) {
//            if (i > 0) {
//                builder.append(",");
//            }
//            FieldMeta<?, ?> fieldMeta = fieldMetaList.get(i);
//            Expression<?> valueExp = valueExpList.get(i);
//
//            DMLUtils.assertSingleUpdateSetClauseField(fieldMeta, tableMeta);
//
//            // fieldMeta self-describe
//            context.appendField(tableAlias, fieldMeta);
//            builder.append(" =");
//            // expression self-describe
//            valueExp.appendSql(context);
//
//        }
//        if (mappingMode != MappingMode.CHILD) {
//            if (!fieldMetaList.isEmpty()) {
//                builder.append(",");
//            }
//            // appendText version And updateTime
//            DMLUtils.setClauseFieldsManagedByArmy(context, tableMeta, tableAlias);
//        }
    }

    static void setClauseFieldsManagedByArmy(_TablesSqlContext context, TableMeta<?> tableMeta
            , String tableAlias) {
        //1. version field
        final FieldMeta<?, ?> versionField = tableMeta.getField(_MetaBridge.VERSION);
        StringBuilder builder = context.sqlBuilder();

        context.appendField(tableAlias, versionField);

        builder.append(" =");
        context.appendField(tableAlias, versionField);
        builder.append(" + 1 ,");

        //2. updateTime fieldπ
        final FieldMeta<?, ?> updateTimeField = tableMeta.getField(_MetaBridge.UPDATE_TIME);
        // updateTime field self-describe
        context.appendField(tableAlias, updateTimeField);
        builder.append(" =");

        final Dialect dialect = context.dialect();
        final ZonedDateTime now = ZonedDateTime.now(dialect.zoneId());

        if (updateTimeField.javaType() == LocalDateTime.class) {
            ((_Expression<?>) SQLs.param(updateTimeField.mappingMeta(), now.toLocalDateTime()))
                    .appendSql(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            if (!dialect.supportZone()) {
                throw new MetaException("dialec[%s]t not supported zone.", dialect.database());
            }
            ((_Expression<?>) SQLs.param(updateTimeField.mappingMeta(), now))
                    .appendSql(context);
        } else {
            throw new MetaException("createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }


    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.updateMode() == UpdateMode.IMMUTABLE) {
            throw new NonUpdateAbleException("FieldMeta[%s] is non-updatable."
                    , fieldMeta);
        }
        if (_MetaBridge.VERSION.equals(fieldMeta.fieldName())
                || _MetaBridge.UPDATE_TIME.equals(fieldMeta.fieldName())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "version or updateTime is managed by army.");
        }
    }

    static boolean hasVersionPredicate(List<_Predicate> predicateList) {
        boolean hasVersion = false;
        for (_Predicate predicate : predicateList) {
            if (predicate instanceof FieldValuePredicate) {
                GenericField<?, ?> fieldExp = ((FieldValuePredicate) predicate).fieldMeta();
                if (_MetaBridge.VERSION.equals(fieldExp.fieldName())) {
                    hasVersion = true;
                    break;
                }
            }
        }
        return hasVersion;
    }


    static void appendInsertFields(final TableMeta<?> domainTable, final Set<FieldMeta<?, ?>> fieldSet) {

        fieldSet.addAll(domainTable.generatorChain());

        fieldSet.add(domainTable.id());
        if (domainTable instanceof ParentTableMeta) {
            fieldSet.add(((ParentTableMeta<?>) domainTable).discriminator());
        }
        if (!(domainTable instanceof ChildTableMeta)) {
            fieldSet.add(domainTable.getField(_MetaBridge.CREATE_TIME));

            if (domainTable.containField(_MetaBridge.UPDATE_TIME)) {
                fieldSet.add(domainTable.getField(_MetaBridge.UPDATE_TIME));
            }
            if (domainTable.containField(_MetaBridge.VERSION)) {
                fieldSet.add(domainTable.getField(_MetaBridge.VERSION));
            }
            if (domainTable.containField(_MetaBridge.VISIBLE)) {
                fieldSet.add(domainTable.getField(_MetaBridge.VISIBLE));
            }
        }


    }

    static void createValueInsertForSimple(TableMeta<?> physicalTable, TableMeta<?> logicalTable
            , Collection<FieldMeta<?, ?>> fieldMetas, ReadonlyWrapper domainWrapper
            , StandardValueInsertContext context) {
//
//        final GenericSessionFactory sessionFactory = context.dialect.sessionFactory();
//        final SQLBuilder fieldBuilder = context.fieldsBuilder().append("INSERT INTO");
//        // append table name
//        context.appendTable(physicalTable, null);
//        context.fieldsBuilder().append(" (");
//
//        final SQLBuilder valueBuilder = context.sqlBuilder()
//                .append(" VALUE (");
//
//        Object value;
//        int count = 0;
//        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
//            if (!fieldMeta.insertable()) {
//                continue;
//            }
//            value = domainWrapper.getPropertyValue(fieldMeta.fieldName());
//            if (value == null && !fieldMeta.nullable()) {
//                continue;
//            }
//            if (count > 0) {
//                fieldBuilder.append(",");
//                valueBuilder.append(",");
//            }
//            // field
//            context.appendField(fieldMeta);
//            if (value == null) {
//                context.appendParam(ParamValue.build(fieldMeta.mappingMeta(), null));
//            } else if (isConstant(fieldMeta)) {
//                valueBuilder.append(createConstant(fieldMeta, logicalTable));
//            } else {
//                valueBuilder.append("?");
//                if (sessionFactory.fieldCodec(fieldMeta) != null) {
//                    context.appendParam(ParamValue.build(fieldMeta, value));
//                } else {
//                    context.appendParam(ParamValue.build(fieldMeta.mappingMeta(), value));
//                }
//
//            }
//            count++;
//        }
//
//        fieldBuilder.append(" )");
//        valueBuilder.append(" )");

    }


    static boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return false;
    }

    static Object createConstant(FieldMeta<?, ?> fieldMeta, TableMeta<?> logicalTable) {
//        Object value;
//        if (_MetaBridge.VERSION.equals(fieldMeta.fieldName())) {
//            value = 0;
//        } else if (fieldMeta == logicalTable.discriminator()) {
//            value = CodeEnum.resolve(fieldMeta.javaType(), logicalTable.discriminatorValue());
//            if (value == null) {
//                throw new MetaException("CodeEnum[%s] not found enum for code[%s]"
//                        , fieldMeta.javaType().getName(), logicalTable.discriminatorValue());
//            }
//        } else {
//            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
//                    , fieldMeta.tableMeta().javaType().getName()
//                    , fieldMeta.fieldName()));
//        }
        return null;
    }

    static Stmt createBatchSQLWrapper(List<? extends ReadonlyWrapper> namedParamList
            , final Stmt stmt) {

        List<List<ParamValue>> parentParamGroupList, childParamGroupList = new ArrayList<>(namedParamList.size());
        SimpleStmt parentWrapper, childWrapper;
        // extract parentWrapper,childWrapper
        List<ParamValue> parentPlaceholderList;
        if (stmt instanceof PairStmt) {
            PairStmt childSQLWrapper = (PairStmt) stmt;
            parentWrapper = childSQLWrapper.parentStmt();
            parentPlaceholderList = parentWrapper.paramGroup();
            childWrapper = childSQLWrapper.childStmt();
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
            if (stmt instanceof PairStmt) {
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
//        BatchSimpleStmt childBatchWrapper = BatchSimpleStmt.build(childWrapper.sql()
//                , childParamGroupList, childWrapper.hasVersion());
//        Stmt batchStmt;
//        if (stmt instanceof PairStmt) {
//            BatchSimpleStmt parentBatchWrapper = BatchSimpleStmt.build(
//                    parentWrapper.sql(), parentParamGroupList, parentWrapper.hasVersion());
//            batchStmt = PairBatchStmt.build(parentBatchWrapper, childBatchWrapper);
//        } else {
//            batchStmt = childBatchWrapper;
//        }
        return null;
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
                Object value = readonlyWrapper.get(((NamedParamExpression<?>) paramValue).name());
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
                Object value = beanWrapper.get(fieldMeta.fieldName());
                paramValueList.add(ParamValue.build(fieldMeta, value));
            } else {
                paramValueList.add(placeHolder);
            }
        }
        return Collections.unmodifiableList(paramValueList);
    }


    private static void doExtractParentPredicatesForUpdate(List<_Predicate> predicateList
            , Collection<FieldMeta<?, ?>> childUpdatedFields, List<_Predicate> newPredicates
            , final boolean firstIsPrimary) {
        for (_Predicate predicate : predicateList) {
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


    private static _Predicate createDiscriminatorPredicate(TableMeta<?> tableMeta) {
//        FieldMeta<?, ? extends CodeEnum> fieldMeta = tableMeta.discriminator();
//        Assert.notNull(fieldMeta, () -> String.format("TableMeta[%s] discriminator is null.", tableMeta));
//        @SuppressWarnings("unchecked")
//        FieldMeta<?, CodeEnum> enumFieldMeta = (FieldMeta<?, CodeEnum>) fieldMeta;
//        CodeEnum codeEnum = CodeEnum.resolve(enumFieldMeta.javaType(), tableMeta.discriminatorValue());
//
//        if (codeEnum == null) {
//            throw new MetaException("TableMeta[%s] discriminatorValue[%s] can't resolve CodeEnum."
//                    , tableMeta, tableMeta.discriminatorValue());
//        }
        return null;
    }

    private static boolean hasDiscriminatorPredicate(List<_Predicate> predicateList
            , FieldMeta<?, ? extends CodeEnum> discriminator) {
        final Class<?> discriminatorClass = discriminator.javaType();
        boolean has = false;
        for (_Predicate predicate : predicateList) {
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
