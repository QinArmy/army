package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class _DialectUtils {

    static final Collection<String> FORBID_INSERT_FIELDS = ArrayUtils.asUnmodifiableList(
            _MetaBridge.ID, _MetaBridge.CREATE_TIME, _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION
    );

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }





    /*################################## blow package method ##################################*/



    public static String parentAlias(final String tableAlias) {
        return "p_of_" + tableAlias;
    }

    public static void validateTableAlias(final String tableAlias) {
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(_Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, _Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
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

    public static boolean hasOptimistic(List<_Predicate> predicateList) {
        boolean match = false;
        for (_Predicate predicate : predicateList) {
            if (predicate.isOptimistic()) {
                match = true;
                break;
            }
        }
        return match;
    }

    public static void checkInsertExpField(final TableMeta<?> table, final FieldMeta<?> field
            , final _Expression value) {

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
        if (!field.nullable() && value.isNullableValue()) {
            throw _Exceptions.nonNullField(field);
        }
        if (FORBID_INSERT_FIELDS.contains(field.fieldName())) {
            throw _Exceptions.armyManageField(field);
        }
        if (field.generator() != null) {
            throw _Exceptions.insertExpDontSupportField(field);
        }
    }

    static void standardInertIntoTable(final _ValueInsertContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.INSERT_INTO_SPACE);
        ((ArmyDialect) context.dialect()).safeObjectName(context.table(), sqlBuilder);
    }


    /*################################## blow private static innner class ##################################*/


}
