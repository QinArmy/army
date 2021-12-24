package io.army.dialect;

import io.army.ErrorCode;
import io.army.annotation.UpdateMode;
import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.*;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;
import io.qinarmy.util.Pair;

import java.util.*;

public abstract class _DmlUtils {

    _DmlUtils() {
        throw new UnsupportedOperationException();
    }


    static final Collection<String> FORBID_INSERT_FIELDS = ArrayUtils.asUnmodifiableList(
            _MetaBridge.CREATE_TIME, _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION
    );


    public static void checkInsertExpField(final TableMeta<?> table, final FieldMeta<?, ?> field
            , final _Expression<?> value) {

        if (table instanceof ChildTableMeta) {
            final TableMeta<?> belongOf = field.tableMeta();
            if (belongOf != table && belongOf != ((ChildTableMeta<?>) table).parentMeta()) {
                throw _Exceptions.unknownColumn(null, field);
            }
        } else if (field.tableMeta() != table) {
            throw _Exceptions.unknownColumn(null, field);
        }
        if (!field.insertable()) {
            throw _Exceptions.nonInsertableField(field);
        }
        if (field == table.discriminator()) {
            throw _Exceptions.armyManageField(field);
        }
        if (!field.nullable() && value.nullableExp()) {
            throw _Exceptions.nonNullField(field);
        }
        if (FORBID_INSERT_FIELDS.contains(field.fieldName())) {
            throw _Exceptions.armyManageField(field);
        }
        if (field.databaseRoute() || field.tableRoute()) {
            throw _Exceptions.insertExpDontSupportField(field);
        }
        if (field.generator() != null) {
            throw _Exceptions.insertExpDontSupportField(field);
        }
    }


    @Deprecated
    public static _SetBlock createSetClause(TableMeta<?> table, String tableAlias
            , String safeTableAlias, boolean selfJoin
            , List<? extends SetTargetPart> targetParts, List<? extends SetValuePart> valueParts) {
        throw new UnsupportedOperationException();
    }


    static void appendStandardValueInsert(final boolean childBlock, final _ValueInsertContext context) {
        final Dialect dialect = context.dialect();
        final _InsertBlock blockContext;
        if (childBlock) {
            blockContext = context.childBlock();
            assert blockContext != null;
        } else {
            blockContext = context;
        }
        final StringBuilder builder = blockContext.sqlBuilder(); // use sql builder of blockContext
        final TableMeta<?> table = blockContext.table();

        // 1. INSERT INTO clause
        builder.append(_AbstractDialect.INSERT_INTO)
                .append(Constant.SPACE);
        //append table name
        if (context.tableIndex() == 0) {
            builder.append(dialect.safeTableName(table.tableName()));
        } else {
            builder.append(table.tableName())
                    .append(context.tableSuffix());
        }
        final List<FieldMeta<?, ?>> fieldList = blockContext.fieldLis();
        // 1.1 append table fields
        builder.append(AbstractSql.LEFT_BRACKET);
        int index = 0;
        for (FieldMeta<?, ?> field : fieldList) {
            if (index > 0) {
                builder.append(AbstractSql.COMMA);
            }
            builder.append(dialect.safeColumnName(field.columnName()));
            index++;
        }
        builder.append(AbstractSql.RIGHT_BRACKET);

        // 2. values clause
        builder.append(AbstractSql.VALUES_WORD);

        final List<? extends ReadWrapper> domainList = context.domainList();
        //2.1 get domainTable and discriminator
        final FieldMeta<?, ?> discriminator = context.table().discriminator();

        int batch = 0;
        final Map<FieldMeta<?, ?>, _Expression<?>> expMap = context.commonExpMap();
        _Expression<?> expression;
        Object value;
        //2.2 append values
        for (ReadWrapper domain : domainList) {
            if (batch > 0) {
                builder.append(AbstractSql.COMMA);
            }
            builder.append(AbstractSql.LEFT_BRACKET);
            index = 0;
            for (FieldMeta<?, ?> field : fieldList) {
                if (index > 0) {
                    builder.append(AbstractSql.COMMA);
                }
                if (field == discriminator) {
                    assert field != null;
                    builder.append(Constant.SPACE)
                            .append(dialect.literal(discriminator.mappingType(), context.discriminatorValue()));
                } else if ((expression = expMap.get(field)) != null) {
                    expression.appendSql(blockContext); // common append to block context
                } else {
                    value = domain.get(field.fieldName());

                    if (value == null) {
                        if (!field.nullable()) {
                            throw _Exceptions.nonNullField(field);
                        }
                        builder.append(" NULL");
                        continue;
                    }
                    final MappingType mappingType = field.mappingType();
                    if (mappingType instanceof _ArmyNoInjectionMapping) {
                        builder.append(Constant.SPACE)
                                .append(dialect.literal(field, value)); // for safe default mapping type, use literal to reduce '?' and add batch insert count.
                    } else {
                        blockContext.appendParam(ParamValue.build(field, value)); // parameter append block context
                    }

                }
                index++;
            }
            builder.append(AbstractSql.RIGHT_BRACKET);
            batch++;
        }

    }


    static Stmt createBatchStmt(final SimpleStmt simpleStmt, final List<ReadWrapper> wrapperList) {
        final List<ParamValue> paramGroup = simpleStmt.paramGroup();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(wrapperList.size());

        for (ReadWrapper wrapper : wrapperList) {
            final List<ParamValue> group = new ArrayList<>(paramSize);

            for (ParamValue param : paramGroup) {
                if (!(param instanceof NamedParam)) {
                    group.add(param);
                    continue;
                }
                final Object value = wrapper.get(((NamedParam<?>) param).name());
                if (value == null && param instanceof NonNullNamedParam) {
                    throw _Exceptions.nonNullNamedParam((NonNullNamedParam<?>) param);
                }
                group.add(ParamValue.build(param.paramMeta(), value));
            }

            groupList.add(group);

        }
        return Stmts.batch(simpleStmt, groupList);
    }


    static List<FieldMeta<?, ?>> mergeInsertFields(final boolean parent, final _ValuesInsert insert) {
        final TableMeta<?> table, relativeTable;
        final List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        final ParentTableMeta<?> parentTable;
        if (parent) {
            relativeTable = insert.table();
            parentTable = ((ChildTableMeta<?>) relativeTable).parentMeta();
            table = parentTable;
        } else {
            table = insert.table();
            if (table instanceof ChildTableMeta) {
                relativeTable = ((ChildTableMeta<?>) table).parentMeta();
            } else {
                relativeTable = null;
            }
        }
        final List<FieldMeta<?, ?>> mergeFieldList;
        if (fieldList.isEmpty()) {
            final Collection<?> fieldCollection = table.fieldCollection();
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
            TableMeta<?> belongOf;
            for (FieldMeta<?, ?> field : fieldList) {
                belongOf = field.tableMeta();
                if (belongOf == relativeTable) {
                    continue;
                }
                if (belongOf != table) {
                    throw _Exceptions.unknownColumn(null, field);
                }
                if (!field.insertable()) {
                    throw _Exceptions.nonInsertableField(field);
                }
                fieldSet.add(field);
            }
            appendInsertFields(table, fieldSet);
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
            if (fieldMetaList.get(i).mappingType() != selectionList.get(i).mappingType()) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "SubQuery Insert,index[%s] field MappingMeta[%s] and sub query MappingMeta[%s] not match."
                        , i, fieldMetaList.get(i).mappingType(), selectionList.get(i).mappingType());
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


    static void assertUpdateSetAndWhereClause(_Update update) {
        final List<FieldMeta<?, ?>> fieldList = update.fieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new CriteriaException("update must have set clause.");
        }
        final List<? extends SetValuePart> valueExpList = update.valueExpList();
        if (fieldList.size() != valueExpList.size()) {
            String m;
            m = String.format("update set clause field list size[%s] and value expression list size[%s] not match."
                    , fieldList.size(), valueExpList.size());
            throw new CriteriaException(m);
        }
        if (CollectionUtils.isEmpty(update.predicateList())) {
            throw new CriteriaException("update must have where clause.");
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

        return false;
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
            , Collection<FieldMeta<?, ?>> fieldMetas, ReadWrapper domainWrapper
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

    static Stmt createBatchSQLWrapper(List<? extends ReadWrapper> namedParamList
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
        for (ReadWrapper readonlyWrapper : namedParamList) {
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
    private static List<ParamValue> createBatchNamedParamList(ReadWrapper readonlyWrapper
            , List<ParamValue> placeHolder) {

        List<ParamValue> paramValueList = new ArrayList<>(placeHolder.size());
        for (ParamValue paramValue : placeHolder) {
            if (paramValue instanceof NamedParam) {
                Object value = readonlyWrapper.get(((NamedParam<?>) paramValue).name());
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
    private static List<ParamValue> createBatchInsertParamList(ReadWrapper beanWrapper
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

        return false;
    }


}
