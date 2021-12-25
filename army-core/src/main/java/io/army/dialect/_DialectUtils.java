package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SortPart;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.FactoryMode;
import io.army.util.StringUtils;
import io.army.util._Exceptions;

import java.sql.JDBCType;
import java.util.*;

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

    public static void appendPredicateList(List<_Predicate> predicateList, _TablesSqlContext context) {

        StringBuilder builder = context.sqlBuilder();
        int count = 0;
        for (_Predicate predicate : predicateList) {
            if (count > 0) {
                builder.append(" AND");
            }
            predicate.appendSql(context);
            count++;
        }
    }

    public static void appendSortPartList(List<_SortPart> sortPartList, _TablesSqlContext context) {
        StringBuilder builder = context.sqlBuilder();

        int count = 0;
        for (_SortPart sortPart : sortPartList) {
            if (count > 0) {
                builder.append(",");
            }
            sortPart.appendSortPart(context);
            count++;
        }
    }

    public static boolean needAppendVisible(TableMeta<?> tableMeta) {
        TableMeta<?> temp = tableMeta;
        if (temp instanceof ChildTableMeta) {
            temp = ((ChildTableMeta<?>) temp).parentMeta();
        }
        return temp.containField(_MetaBridge.VISIBLE);
    }

    public static boolean needAppendVisible(List<? extends _TableBlock> tableWrapperList) {
        final TableMeta<?> dual = null;
        boolean need = false;
        for (_TableBlock tableBlock : tableWrapperList) {
            TablePart tableAble = tableBlock.table();

            if ((tableAble instanceof TableMeta) && tableAble != dual) {

                TableMeta<?> temp = (TableMeta<?>) tableAble;
                if (tableAble instanceof ChildTableMeta) {
                    temp = ((ChildTableMeta<?>) temp).parentMeta();
                }
                if (temp.containField(_MetaBridge.VISIBLE)) {
                    need = true;
                    break;
                }
            }
        }
        return need;
    }


    public static CriteriaException createNoLogicalTableException(FieldMeta<?, ?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "not found logical table for [%s], please use SQLS.field(String,FieldMeta) method.", fieldMeta);
    }

    public static CriteriaException createNoLogicalTableException(String tableAlias) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "not found logical table[%s], please check criteria.", tableAlias);
    }

    public static CriteriaException createUnKnownFieldException(FieldMeta<?, ?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "unknown logical table for FieldMeta[%s] in current context,please check criteria code.", fieldMeta);
    }

    public static CriteriaException createUnKnownTableException(TableMeta<?> tableMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                , "unknown TableMeta[%s] in current context,please check criteria code.", tableMeta);
    }

    /*################################## blow package method ##################################*/


    static List<Selection> extractSelectionList(List<SelectPart> selectPartList) {
        List<Selection> selectionList = new ArrayList<>();
        for (SelectPart selectPart : selectPartList) {
            if (selectPart instanceof Selection) {
                selectionList.add((Selection) selectPart);
            } else if (selectPart instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectPart).selectionList());
            } else {
                throw new UnKnownTypeException(selectPart);
            }
        }
        return Collections.unmodifiableList(selectionList);
    }

    static void assertShardingMode(Dialect dialect, @Nullable Set<Integer> domainIndexSet) {
        if (domainIndexSet != null && dialect.sessionFactory().factoryMode() == FactoryMode.NO_SHARDING) {
            throw new IllegalArgumentException("domainIndexSet must be null in NO_SHARDING mode.");
        }
    }

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
