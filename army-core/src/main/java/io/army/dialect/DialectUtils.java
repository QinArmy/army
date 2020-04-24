package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.IPredicate;
import io.army.criteria.ParentChildJoinPredicate;
import io.army.criteria.TableAble;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.meta.mapping.MappingType;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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

    public static void separateFields(ChildTableMeta<?> childMeta, Collection<FieldMeta<?, ?>> fieldMetas
            , Collection<FieldMeta<?, ?>> parentFields, Collection<FieldMeta<?, ?>> childFields) {

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (TableMeta.ID.equals(fieldMeta.propertyName())) {
                childFields.add(childMeta.primaryKey());
                parentFields.add(parentMeta.primaryKey());
            } else if (fieldMeta.tableMeta() == parentMeta) {
                parentFields.add(fieldMeta);
            } else if (fieldMeta.tableMeta() == childMeta) {
                childFields.add(fieldMeta);
            } else {
                throw DialectUtils.createTableFiledNoMatchException(childMeta, fieldMeta);
            }
        }
    }

    public static ArmyRuntimeException createNotSupportClauseException(ClauseSQLContext context, Clause clause) {
        return new ArmyRuntimeException(ErrorCode.NONE, "%s not support %s clause."
                , context.getClass().getName(), clause);
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
