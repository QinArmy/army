package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SortPart;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.FactoryMode;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.util.*;

public abstract class DialectUtils {

    protected DialectUtils() {
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

    public static SqlBuilder createSQLBuilder() {
        return new SQLBuilderImpl();
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDomain> Collection<FieldMeta<?, ?>> tableFields(TableMeta<T> tableMeta) {
        final Collection<?> collection = tableMeta.fieldCollection();
        return (Collection<FieldMeta<?, ?>>) collection;
    }

    public static String tableSuffix(final byte tableIndex) {
        final String suffix;
        if (tableIndex < 0) {
            throw new IllegalArgumentException(String.format("tableIndex[%s] must non-negative.", tableIndex));
        } else if (tableIndex == 0) {
            suffix = "";
        } else if (tableIndex < 10) {
            suffix = "_0" + tableIndex;
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
                throw DialectUtils.createTableFiledNoMatchException(childMeta, fieldMeta);
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

    public static boolean needAppendVisible(List<? extends TableWrapper> tableWrapperList) {
        final TableMeta<?> dual = SQLs.dual();
        boolean need = false;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

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

    public static ArmyRuntimeException createNotSupportClauseException(_TablesSqlContext context, Clause clause) {
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




    /*################################## blow private static innner class ##################################*/

    private static final class SQLBuilderImpl implements SqlBuilder {

        private final StringBuilder builder = new StringBuilder(128);

        private SQLBuilderImpl() {
        }

        @Override
        public SqlBuilder append(boolean b) {
            this.builder.append(b);
            return this;
        }

        @Override
        public SqlBuilder append(char ch) {
            this.builder.append(ch);
            return this;
        }

        @Override
        public SqlBuilder append(char[] charArray) {
            this.builder.append(charArray);
            return this;
        }

        @Override
        public SqlBuilder append(char[] charArray, int offset, int len) {
            this.builder.append(charArray, offset, len);
            return this;
        }

        @Override
        public SqlBuilder append(CharSequence s) {
            this.builder.append(s);
            return this;
        }

        @Override
        public SqlBuilder append(CharSequence s, int start, int end) {
            this.builder.append(s, start, end);
            return this;
        }

        @Override
        public SqlBuilder append(double d) {
            this.builder.append(d);
            return this;
        }

        @Override
        public SqlBuilder append(float f) {
            this.builder.append(f);
            return this;
        }

        @Override
        public SqlBuilder append(int i) {
            this.builder.append(i);
            return this;
        }

        @Override
        public SqlBuilder append(long lng) {
            this.builder.append(lng);
            return this;
        }

        @Override
        public SqlBuilder append(Object obj) {
            this.builder.append(obj);
            return this;
        }

        @Override
        public SqlBuilder append(String str) {
            this.builder.append(str);
            return this;
        }

        @Override
        public SqlBuilder appendCodePoint(int codePoint) {
            this.builder.append(codePoint);
            return this;
        }

        @Override
        public String toString() {
            return this.builder.toString();
        }
    }

}
