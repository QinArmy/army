package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collection;

public abstract class _DialectUtils {

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }



    public static String quoteIfNeed(MappingType mappingType, String textValue) {

        return "";
    }


    public static CriteriaException createTableFiledNoMatchException(TableMeta<?> tableMeta
            , FieldMeta<?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "TableMeta[%s] and FieldMeta[%s] not match.", tableMeta, fieldMeta);
    }

    public static void divideFields(ChildTableMeta<?> childMeta, Collection<FieldMeta<?>> mergedFields
            , Collection<FieldMeta<?>> parentFields, Collection<FieldMeta<?>> childFields) {

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        for (FieldMeta<?> fieldMeta : mergedFields) {
            if (fieldMeta instanceof PrimaryFieldMeta && _MetaBridge.ID.equals(fieldMeta.fieldName())) {
                childFields.add(childMeta.id());
                parentFields.add(parentMeta.id());
            } else if (fieldMeta.tableMeta() == parentMeta) {
                parentFields.add(fieldMeta);
            } else if (fieldMeta.tableMeta() == childMeta) {
                childFields.add(fieldMeta);
            } else {
                throw _DialectUtils.createTableFiledNoMatchException(childMeta, fieldMeta);
            }
        }
    }


    public static CriteriaException createUnKnownFieldException(FieldMeta<?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "unknown logical table for FieldMeta[%s] in current context,please check criteria code.", fieldMeta);
    }


    /*################################## blow package method ##################################*/



    public static String parentAlias(final String tableAlias) {
        return "p_of_" + tableAlias;
    }

    public static String childAlias(final String tableAlias) {
        return "_c_of_" + tableAlias;
    }


    public static void validateUpdateTableAlias(final TableMeta<?> table, final String tableAlias) {
        if (table instanceof SimpleTableMeta && table.immutable()) {
            throw _Exceptions.immutableTable(table);
        }
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }

    public static void validateTableAlias(final String tableAlias) {
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }


    /*################################## blow private static innner class ##################################*/


}
