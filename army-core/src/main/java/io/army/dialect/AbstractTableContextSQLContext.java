package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTableContextSQLContext extends AbstractSQLContext implements TableContextSQLContext {


    protected static TableContext createFromContext(List<TableWrapper> tableWrapperList) {
        Map<TableMeta<?>, Integer> tableCountMap = new HashMap<>();
        Map<String, TableMeta<?>> aliasTableMap = new HashMap<>();

        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                Integer count = tableCountMap.computeIfAbsent(tableMeta, key -> 0);
                tableCountMap.replace(tableMeta, count, count + 1);
                if (aliasTableMap.putIfAbsent(tableWrapper.alias(), tableMeta) != null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] alias[%s] duplication."
                            , tableMeta, tableWrapper.alias());
                }
            }
        }

        Map<TableMeta<?>, String> tableAliasMap = new HashMap<>();

        final Integer one = 1;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                if (one.equals(tableCountMap.get(tableMeta))) {

                    tableAliasMap.putIfAbsent(tableMeta, tableWrapper.alias());
                }
            }

        }

        return new TableContext(tableCountMap, aliasTableMap, tableAliasMap);
    }


    protected final TableContext tableContext;


    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible);
        this.tableContext = tableContext;
    }

    protected AbstractTableContextSQLContext(TableContextSQLContext original, TableContext tableContext) {
        super(original);
        this.tableContext = tableContext;
    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        this.appendField(findTableAlias(fieldMeta), fieldMeta);
    }

    @Override
    public void appendPredicate(SpecialPredicate predicate) {

    }

    @Override
    public final void appendTable(TableMeta<?> tableMeta) {
        if (!this.tableContext.tableCountMap.containsKey(tableMeta)) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));
    }


    @Override
    public final TableContext tableContext() {
        return this.tableContext;
    }

    @Override
    public void appendParentTableOf(ChildTableMeta<?> childTableMeta) {
        appendTable(childTableMeta.parentMeta());
    }

    @Override
    public final void appendText(String textValue) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(textValue));
    }

    @Override
    public final void appendTextValue(MappingMeta mappingType, Object value) {
        sqlBuilder.append(
                DialectUtils.quoteIfNeed(
                        mappingType
                        , mappingType.nonNullTextValue(value)
                )
        );
    }





    protected final String findTableAlias(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        Integer count = this.tableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            tableAlias = findTableAliasFromParent(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = this.tableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        if (tableAlias == null) {
            // fromContext or parentFromContext error.
            throw DialectUtils.createArmyCriteriaException();
        }
        return tableAlias;
    }

    protected String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        throw DialectUtils.createUnKnownFieldException(fieldMeta);
    }



}
