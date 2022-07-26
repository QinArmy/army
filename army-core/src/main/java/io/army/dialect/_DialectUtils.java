package io.army.dialect;

import io.army.bean.ObjectAccessException;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class _DialectUtils {

    static final Collection<String> FORBID_INSERT_FIELDS = ArrayUtils.asUnmodifiableList(
            _MetaBridge.CREATE_TIME, _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION
    );

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }


    public static List<Selection> flatSelectItem(final List<? extends SelectItem> selectPartList) {
        final List<Selection> selectionList = new ArrayList<>(selectPartList.size());
        for (SelectItem selectItem : selectPartList) {
            if (selectItem instanceof Selection) {
                selectionList.add((Selection) selectItem);
            } else if (selectItem instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectItem).selectionList());
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }
        }
        return selectionList;
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


    static void standardInertIntoTable(final _ValueInsertContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.INSERT_INTO_SPACE);
        ((ArmyDialect) context.dialect()).safeObjectName(context.insertTable(), sqlBuilder);
    }

    static void appendConditionFields(final _SingleUpdateContext context
            , final @Nullable List<TableField> conditionFieldList) {
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final String safeTableAlias = context.safeTableAlias();
        final ArmyDialect dialect = (ArmyDialect) context.dialect();
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
                            .append(_Constant.SPACE_LEFT_PAREN)
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

    static void checkDefaultValueMap(final _Insert._ValuesSyntaxInsert insert) {
        if (insert.isMigration()) {
            return;
        }
        final TableMeta<?> table = insert.table();

        FieldMeta<?> field;
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.defaultValueMap().entrySet()) {
            field = e.getKey();
            checkInsertField(table, field, null);
            if (!field.nullable() && e.getValue().isNullValue()) {
                throw _Exceptions.nonNullField(field);
            }

        }

    }

    static boolean isDiscriminatorValue(TableMeta<?> domainTable, @Nullable Object value) {
        final FieldMeta<?> discriminator = domainTable.discriminator();
        assert discriminator != null;
        return value == CodeEnum.resolve(discriminator.javaType(), domainTable.discriminatorValue());
    }


    static void appendSingleTableField(final SingleTableMeta<?> insertTable, final List<FieldMeta<?>> fieldList
            , final Predicate<FieldMeta<?>> predicate) {

        FieldMeta<?> field;
        field = insertTable.id();
        if (field.insertable() && field.generatorType() != null) {
            fieldList.add(field);
        }

        field = insertTable.getField(_MetaBridge.CREATE_TIME);
        fieldList.add(field);

        field = insertTable.tryGetField(_MetaBridge.UPDATE_TIME);
        if (field != null) {
            fieldList.add(field);
        }

        field = insertTable.tryGetField(_MetaBridge.VERSION);
        if (field != null) {
            fieldList.add(field);
        }


        field = insertTable.tryGetField(_MetaBridge.VISIBLE);
        if (field != null && !predicate.test(field)) {
            fieldList.add(field);
        }

        if (insertTable instanceof ParentTableMeta) {
            fieldList.add(insertTable.discriminator());
        }

        for (FieldMeta<?> f : insertTable.fieldChain()) {
            if (f instanceof PrimaryFieldMeta) {
                continue;
            }
            fieldList.add(f);
        }


    }

    static void appendChildTableField(final ChildTableMeta<?> insertTable, final List<FieldMeta<?>> fieldList) {

        fieldList.add(insertTable.id());

        for (FieldMeta<?> field : insertTable.fieldChain()) {
            if (field instanceof PrimaryFieldMeta) {
                continue;
            }
            fieldList.add(field);
        }

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
        public final boolean isNull(final FieldMeta<?> field) {
            final _Expression expression;
            expression = this.getExpression(field);
            final boolean match;
            if (expression == null) {
                match = true;
            } else if (expression instanceof SqlValueParam.SingleNonNamedValue) {
                match = ((SqlValueParam.SingleNonNamedValue) expression).value() != null;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }

        /**
         * @return a unmodified map
         */
        @Nullable
        abstract Object getGeneratedValue(FieldMeta<?> field);

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
            Object value;
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

            if (!(expression instanceof SqlValueParam.SingleNonNamedValue)) {
                return null;
            }
            value = ((SqlValueParam.SingleNonNamedValue) expression).value();

            final Class<?> javaType = field.javaType();
            if (value == null || javaType.isInstance(value)) {
                return value;
            }
            value = field.mappingType().convert(this.mappingEnv, value);
            if (!javaType.isInstance(value)) {
                String m = String.format("%s convert method don't return instance of %s"
                        , field.mappingType().getClass().getName(), javaType.getName());
                throw new MetaException(m);
            }
            return value;
        }


    }//RowReadWrapper


}
