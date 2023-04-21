package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class _DialectUtils {

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isOnConflictDoNothing(final _Insert stmt) {
        return stmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) stmt).isIgnorableConflict();
    }

    /**
     * @return true : representing insert {@link ChildTableMeta} and syntax error
     * , statement executor couldn't get the auto increment primary key of {@link ParentTableMeta}
     */
    public static boolean isForbidChildInsert(final _Insert._ChildInsert stmt) {
        final _Insert parentStmt;
        parentStmt = stmt.parentStmt();

        final boolean needReturnId, cannotReturnId;
        needReturnId = !(parentStmt instanceof _Insert._QueryInsert || ((_Insert._InsertOption) parentStmt).isMigration())
                && parentStmt.insertTable().id().generatorType() == GeneratorType.POST;

        cannotReturnId = needReturnId
                && parentStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) parentStmt).hasConflictAction()
                && parentStmt.insertRowCount() > 1
                && (!(parentStmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) parentStmt).isIgnorableConflict());
        return needReturnId && cannotReturnId;
    }

    public static boolean isDoNothing(final _Insert._ChildInsert childStmt) {
        final _Insert parentStmt = childStmt.parentStmt();
        final boolean parentDoNothing, childDoNothing;
        parentDoNothing = parentStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) parentStmt).isDoNothing();

        childDoNothing = childStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) childStmt).isDoNothing();
        //here, validate do nothing only,rest is validate by statement executor.
        return parentDoNothing || childDoNothing;
    }

    /**
     * @param stmt non {@link io.army.criteria.impl.inner._Insert._ChildInsert}
     */
    public static boolean isCannotReturnId(final _Insert._DomainInsert stmt) {
        final TableMeta<?> table = stmt.insertTable();
        if (table instanceof ChildTableMeta) {
            //here,stmt not tow statement mode,for example postgre insert with cte.
            return false;
        }
        final boolean needReturnId, cannotReturnId;
        needReturnId = stmt instanceof PrimaryStatement
                && !stmt.isMigration()
                && table.id().generatorType() == GeneratorType.POST
                && !stmt.isIgnoreReturnIds();

        cannotReturnId = stmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) stmt).hasConflictAction()
                && stmt.insertRowCount() > 1
                && (!(stmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) stmt).isIgnorableConflict());
        return needReturnId && cannotReturnId;

    }


    /**
     * @return a unmodified list
     */
    public static List<Selection> flatSelectItem(final List<? extends SelectItem> selectPartList) {
        final List<Selection> selectionList = new ArrayList<>(selectPartList.size());
        for (SelectItem selectItem : selectPartList) {
            if (selectItem instanceof Selection) {
                selectionList.add((Selection) selectItem);
            } else if (selectItem instanceof _SelectionGroup) {
                selectionList.addAll(((_SelectionGroup) selectItem).selectionList());
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }
        }
        return Collections.unmodifiableList(selectionList);
    }


    /**
     * @see ArmyParser#safeObjectName(DatabaseObject)
     */
    public static boolean isSimpleIdentifier(final String objectName) {
        final int length = objectName.length();
        char ch;
        // empty string isn't safe identifier
        boolean match = length > 0;
        for (int i = 0; i < length; i++) {
            ch = objectName.charAt(i);
            if ((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || ch == '_') {
                continue;
            } else if (i > 0 && (ch >= '0' && ch <= '9')) {
                continue;
            }
            match = false;
            break;
        }
        return match;
    }


    public static void checkInsertField(final TableMeta<?> table, final FieldMeta<?> field
            , final @Nullable BiFunction<FieldMeta<?>, Function<FieldMeta<?>, CriteriaException>, CriteriaException> function) {

        if (!field.insertable()) {
            if (function == null) {
                throw _Exceptions.nonInsertableField(field);
            }
            throw function.apply(field, _Exceptions::nonInsertableField);
        }
        if (field.tableMeta() != table) {
            if (function == null) {
                throw _Exceptions.unknownColumn(null, field);
            }
            throw function.apply(field, _Exceptions::unknownColumn);
        }
        if (field == table.discriminator()) {
            if (function == null) {
                throw _Exceptions.armyManageField(field);
            }
            throw function.apply(field, _Exceptions::armyManageField);
        }

        switch (field.fieldName()) {
            case _MetaBridge.ID:
            case _MetaBridge.CREATE_TIME:
            case _MetaBridge.UPDATE_TIME:
            case _MetaBridge.VERSION: {
                if (function == null) {
                    throw _Exceptions.armyManageField(field);
                }
                throw function.apply(field, _Exceptions::armyManageField);
            }
            default:
                //no-op
        }

        if (field.generatorType() != null) {
            if (function == null) {
                throw _Exceptions.insertExpDontSupportField(field);
            }
            throw function.apply(field, _Exceptions::insertExpDontSupportField);
        }


    }


    /*################################## blow package method ##################################*/


    static String parentAlias(final String tableAlias) {
        return "p_of_" + tableAlias;
    }

    static void validateTableAlias(final String tableAlias) {
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(_Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, _Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }


    static boolean hasOptimistic(List<_Predicate> predicateList) {
        boolean match = false;
        for (_Predicate predicate : predicateList) {
            if (predicate.isOptimistic()) {
                match = true;
                break;
            }
        }
        return match;
    }

    static void appendConditionFields(final _SingleUpdateContext context
            , final @Nullable List<? extends TableField> conditionFieldList) {
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final String safeTableAlias = context.safeTargetTableAlias();
        final ArmyParser dialect = (ArmyParser) context.parser();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        String safeColumnName;
        for (TableField field : conditionFieldList) {
            sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);

            safeColumnName = dialect.safeObjectName(field);
            sqlBuilder.append(safeColumnName);
            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.LEFT_PAREN)
                            .append(_Constant.SPACE)
                            .append(safeTableAlias)
                            .append(_Constant.POINT)
                            .append(safeColumnName)
                            .append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());

            }

        }
    }

    static boolean isIllegalConflict(final _Insert stmt, final Visible visible) {
        if (visible == Visible.BOTH || !stmt.insertTable().containComplexField(_MetaBridge.VISIBLE)) {
            return false;
        }

        final _Insert nonChildStmt;
        if (stmt instanceof _Insert._ChildInsert) {
            nonChildStmt = ((_Insert._ChildInsert) stmt).parentStmt();
        } else {
            nonChildStmt = stmt;
        }
        final boolean nonChildIllegal, childIllegal;
        nonChildIllegal = nonChildStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) nonChildStmt).hasConflictAction()
                && !((_Insert._SupportConflictClauseSpec) nonChildStmt).isIgnorableConflict();

        if (nonChildStmt == stmt) {
            childIllegal = false;
        } else {
            childIllegal = stmt instanceof _Insert._SupportConflictClauseSpec
                    && ((_Insert._SupportConflictClauseSpec) stmt).hasConflictAction()
                    && !((_Insert._SupportConflictClauseSpec) stmt).isIgnorableConflict();
        }

        return nonChildIllegal || childIllegal;
    }

    static void checkDefaultValueMap(final _Insert._ValuesSyntaxInsert insert) {
        if (insert.isMigration()) {
            return;
        }
        final TableMeta<?> table = insert.insertTable();

        FieldMeta<?> field;
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.defaultValueMap().entrySet()) {
            field = e.getKey();
            checkInsertField(table, field, null);
            if (!field.nullable() && e.getValue().isNullValue()) {
                throw _Exceptions.nonNullField(field);
            }

        }

    }

    /**
     * @return a unmodified map
     */
    static Map<String, Boolean> createKeyWordMap(final Set<String> keyWordSet) {
        final Map<String, Boolean> map;
        map = _Collections.hashMap((int) (keyWordSet.size() / 0.75f));
        for (String keyWord : keyWordSet) {
            map.putIfAbsent(keyWord.toUpperCase(Locale.ROOT), Boolean.TRUE);
        }

        for (String keyWord : requiredKeyWordMap().keySet()) {
            map.putIfAbsent(keyWord.toUpperCase(Locale.ROOT), Boolean.TRUE);
        }
        return Collections.unmodifiableMap(map);
    }


    static int generatedFieldSize(final TableMeta<?> domainTable, final boolean manageVisible) {
        int size = 1; //create time

        if (domainTable instanceof SingleTableMeta) {
            if (domainTable.containField(_MetaBridge.UPDATE_TIME)) {
                size++;
            }
            if (domainTable.containField(_MetaBridge.VERSION)) {
                size++;
            }
            if (manageVisible && domainTable.containField(_MetaBridge.VISIBLE)) {
                size++;
            }
        }

        if (!(domainTable instanceof SimpleTableMeta)) {
            size++; //discriminator
        }


        size += domainTable.fieldChain().size();

        if (domainTable instanceof ChildTableMeta) {
            size += ((ChildTableMeta<?>) domainTable).parentMeta().fieldChain().size();
        }
        return size;
    }


    @Nullable
    static Object readParamValue(final FieldMeta<?> field, final @Nullable _Expression expression
            , final MappingEnv mappingEnv) {
        if (!(expression instanceof SqlValueParam.SingleAnonymousValue)) {
            return null;
        }
        Object value;
        value = ((SqlValueParam.SingleAnonymousValue) expression).value();

        final Class<?> javaType = field.javaType();
        if (value == null || javaType.isInstance(value)) {
            return value;
        }
        value = field.mappingType().convert(mappingEnv, value);
        if (!javaType.isInstance(value)) {
            String m = String.format("%s convert method don't return instance of %s"
                    , field.mappingType().getClass().getName(), javaType.getName());
            throw new MetaException(m);
        }
        return value;

    }




    /*-------------------below private method -------------------*/


    /**
     * @see #createKeyWordMap(Set)
     */
    private static Map<String, Boolean> requiredKeyWordMap() {
        final Map<String, Boolean> map;
        map = _Collections.hashMap();

        map.put("SELECT", Boolean.TRUE); //TODO
        map.put("DELETE", Boolean.TRUE);
        return map;
    }


    /*################################## blow private static innner class ##################################*/


    static abstract class ExpRowWrapper implements RowWrapper {

        final TableMeta<?> domainTable;

        private final ReadWrapper readWrapper;

        ExpRowWrapper(TableMeta<?> domainTable, MappingEnv mappingEnv) {
            this.domainTable = domainTable;
            this.readWrapper = new RowReadWrapper(this, mappingEnv);
        }


        @Override
        public final boolean isNullValueParam(final FieldMeta<?> field) {
            final _Expression expression;
            expression = this.getExpression(field);
            final boolean match;
            if (expression == null) {
                match = true;
            } else if (expression instanceof SqlValueParam.SingleAnonymousValue) {
                match = ((SqlValueParam.SingleAnonymousValue) expression).value() == null;
            } else {
                match = true; //the fields that is managed by field must be value param
            }
            return match;
        }

        @Override
        public final ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }


        @Nullable
        abstract Object getGeneratedValue(FieldMeta<?> field);

        /**
         * <p>
         * Must read row value not default value of column
         * </p>
         */
        @Nullable
        abstract _Expression getExpression(FieldMeta<?> field);


    }//ExpRowWrapper


    private static final class RowReadWrapper implements ReadWrapper {

        private final ExpRowWrapper wrapper;

        private final MappingEnv mappingEnv;

        private RowReadWrapper(ExpRowWrapper wrapper, MappingEnv mappingEnv) {
            this.wrapper = wrapper;
            this.mappingEnv = mappingEnv;
        }

        @Override
        public boolean isReadable(final String propertyName) {
            return this.wrapper.domainTable.containComplexField(propertyName);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final ExpRowWrapper wrapper = this.wrapper;
            final TableMeta<?> domainTable = wrapper.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(domainTable, propertyName);
            }
            final Object value;
            value = wrapper.getGeneratedValue(field);
            if (value != null) {
                return value;
            }

            final _Expression expression;
            if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
                expression = wrapper.getExpression(field.tableMeta().nonChildId());
            } else {
                expression = wrapper.getExpression(field);
            }
            return readParamValue(field, expression, this.mappingEnv);
        }


    }//RowReadWrapper


}
