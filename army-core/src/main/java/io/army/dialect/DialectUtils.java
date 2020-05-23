package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerGeneralQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.meta.mapping.MappingMeta;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.util.*;

public abstract class DialectUtils {


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


    public static String quoteIfNeed(MappingMeta mappingType, String textValue) {
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

    public static CriteriaException createTableAliasDuplicationException(String tableAlias, TableAble tableAble) {
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

    public static void divideFields(ChildTableMeta<?> childMeta, Collection<FieldMeta<?, ?>> mergedFields
            , Collection<FieldMeta<?, ?>> parentFields, Collection<FieldMeta<?, ?>> childFields) {

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        boolean appendedId = false;
        for (FieldMeta<?, ?> fieldMeta : mergedFields) {
            if (!appendedId && TableMeta.ID.equals(fieldMeta.propertyName())) {
                childFields.add(childMeta.id());
                parentFields.add(parentMeta.id());
                appendedId = true;
            } else if (fieldMeta.tableMeta() == parentMeta) {
                parentFields.add(fieldMeta);
            } else if (fieldMeta.tableMeta() == childMeta) {
                childFields.add(fieldMeta);
            } else {
                throw DialectUtils.createTableFiledNoMatchException(childMeta, fieldMeta);
            }
        }
    }

    public static void appendPredicateList(List<IPredicate> predicateList, TableContextSQLContext context) {

        StringBuilder builder = context.sqlBuilder();
        int count = 0;
        for (IPredicate predicate : predicateList) {
            if (count > 0) {
                builder.append(" AND");
            }
            predicate.appendSQL(context);
            count++;
        }
    }

    public static void appendSortPartList(List<SortPart> sortPartList, TableContextSQLContext context) {
        StringBuilder builder = context.sqlBuilder();

        int count = 0;
        for (SortPart sortPart : sortPartList) {
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
        return temp.mappingProp(TableMeta.VISIBLE);
    }

    public static boolean needAppendVisible(List<TableWrapper> tableWrapperList) {
        final TableMeta<?> dual = SQLS.dual();
        boolean need = false;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if ((tableAble instanceof TableMeta) && tableAble != dual) {

                TableMeta<?> temp = (TableMeta<?>) tableAble;
                if (tableAble instanceof ChildTableMeta) {
                    temp = ((ChildTableMeta<?>) temp).parentMeta();
                }
                if (temp.mappingProp(TableMeta.VISIBLE)) {
                    need = true;
                    break;
                }
            }
        }
        return need;
    }

    public static ArmyRuntimeException createNotSupportClauseException(TableContextSQLContext context, Clause clause) {
        return new ArmyRuntimeException(ErrorCode.NONE, "%s not support %s clause."
                , context.getClass().getName(), clause);
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

    static List<Selection> extractSelectionList(Select select) {
        List<Selection> selectionList = new ArrayList<>();
        for (SelectPart selectPart : ((InnerGeneralQuery) select).selectPartList()) {
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

    @Nullable
    private static ParentChildJoinPredicate findParentChildJoinPredicate(List<IPredicate> onPredicateList) {
        for (IPredicate predicate : onPredicateList) {
            if (predicate instanceof ParentChildJoinPredicate) {
                return (ParentChildJoinPredicate) predicate;
            }
        }
        return null;
    }

}
