package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.StringUtils;
import io.army.util._Exceptions;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.EnumSet;

public abstract class _DialectUtils {

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }

    protected static final EnumSet<JDBCType> TEXT_JDBC_TYPE = EnumSet.of(
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.NVARCHAR,
            JDBCType.LONGVARCHAR,
            JDBCType.DATE,
            JDBCType.TIME,

            JDBCType.TIMESTAMP,
            JDBCType.TIME_WITH_TIMEZONE,
            JDBCType.TIMESTAMP_WITH_TIMEZONE
    );


    public static String quoteIfNeed(MappingType mappingType, String textValue) {
        if (TEXT_JDBC_TYPE.contains(mappingType.jdbcType())) {
            return StringUtils.quote(textValue);
        }
        return textValue;
    }


    public static CriteriaException createTableFiledNoMatchException(TableMeta<?> tableMeta
            , FieldMeta<?, ?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "TableMeta[%s] and FieldMeta[%s] not match.", tableMeta, fieldMeta);
    }

    public static void divideFields(ChildTableMeta<?> childMeta, Collection<FieldMeta<?, ?>> mergedFields
            , Collection<FieldMeta<?, ?>> parentFields, Collection<FieldMeta<?, ?>> childFields) {

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        for (FieldMeta<?, ?> fieldMeta : mergedFields) {
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


    public static CriteriaException createUnKnownFieldException(FieldMeta<?, ?> fieldMeta) {
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
        if (!StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }

    public static void validateTableAlias(final String tableAlias) {
        if (!StringUtils.hasText(tableAlias)) {
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
