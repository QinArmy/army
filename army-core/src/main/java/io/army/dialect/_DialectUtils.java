package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.StringUtils;
import io.army.util._Exceptions;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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


    public static String tableSuffix(final byte tableIndex) {
        final String suffix;
        if (tableIndex < 0) {
            throw new IllegalArgumentException(String.format("tableIndex[%s] must non-negative.", tableIndex));
        } else if (tableIndex == 0) {
            suffix = "";
        } else if (tableIndex < 10) {
            suffix = "_0" + tableIndex;
        } else if (tableIndex < 100) {
            suffix = Byte.toString(tableIndex);
        } else {
            throw new IllegalArgumentException(String.format("tableIndex[%s] too large.", tableIndex));
        }
        return suffix;
    }


    public static String quoteIfNeed(MappingType mappingType, String textValue) {
        if (TEXT_JDBC_TYPE.contains(mappingType.jdbcType())) {
            return StringUtils.quote(textValue);
        }
        return textValue;
    }

    public static IllegalArgumentException createMappingModeUnknownException(MappingMode mappingMode) {
        throw new IllegalArgumentException(String.format("unknown MappingMode[%s]",
                mappingMode));
    }

    public static ArmyRuntimeException createArmyCriteriaException() {
        return new ArmyRuntimeException(ErrorCode.NONE, "Army criteria error.");
    }

    public static CriteriaException createTableAliasDuplicationException(String tableAlias, TablePart tableAble) {
        return new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "tableAlias[%s] duplication for TableAble[%s].", tableAlias, tableAble);
    }


    public static boolean parentJoinChild(List<IPredicate> onPredicateList, TableMeta<?> childMeta) {
        ParentChildJoinPredicate joinPredicate = findParentChildJoinPredicate(onPredicateList);
        return joinPredicate != null && joinPredicate.childMeta() == childMeta;
    }

    public static boolean childJoinParent(List<IPredicate> onPredicateList, TableMeta<?> parentMeta) {
        ParentChildJoinPredicate joinPredicate = findParentChildJoinPredicate(onPredicateList);
        return joinPredicate != null && joinPredicate.childMeta().parentMeta() == parentMeta;
    }

    public static CriteriaException createTableFiledNoMatchException(TableMeta<?> tableMeta
            , FieldMeta<?, ?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "TableMeta[%s] and FieldMeta[%s] not match.", tableMeta, fieldMeta);
    }

    public static IllegalArgumentException createUnknownLockModeException(LockMode lockMode, Database database) {
        return new IllegalArgumentException(String.format("unknown LockMode[%s] for database[%s]", lockMode, database));
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


    @Nullable
    private static ParentChildJoinPredicate findParentChildJoinPredicate(List<IPredicate> onPredicateList) {
        for (IPredicate predicate : onPredicateList) {
            if (predicate instanceof ParentChildJoinPredicate) {
                return (ParentChildJoinPredicate) predicate;
            }
        }
        return null;
    }

    public static String parentAlias(final String tableAlias) {
        return Constant.FORBID_ALIAS + "p_of_" + tableAlias;
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
