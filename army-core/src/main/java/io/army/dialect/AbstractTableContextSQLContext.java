package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.DomainWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.*;

public abstract class AbstractTableContextSQLContext implements TableContextSQLContext {


    protected static TableContext createFromContext(List<TableWrapper> tableWrapperList) {
        Map<TableMeta<?>, Integer> tableCountMap = new HashMap<>();
        Map<String, TableMeta<?>> aliasTableMap = new HashMap<>();

        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                Integer count = tableCountMap.computeIfAbsent(tableMeta, key -> 0);
                tableCountMap.replace(tableMeta, count, count + 1);
                if (aliasTableMap.putIfAbsent(tableWrapper.alias(), tableMeta) != tableMeta) {
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


    protected final Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamWrapper> paramList;

    protected final Stack<Clause> clauseStack = new Stack<>();

    protected final TableContext tableContext;

    boolean finished;

    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TableContext tableContext) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder();
        this.paramList = new ArrayList<>();
        this.tableContext = tableContext;
    }

    protected AbstractTableContextSQLContext(TableContextSQLContext original, TableContext tableContext) {
        this.dialect = original.dialect();
        this.visible = original.visible();
        this.sqlBuilder = original.sqlBuilder();
        this.paramList = original.paramList();
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
    public final void appendFieldPair(FieldPairDualPredicate predicate) {
        predicate.left().appendSQL(this);
        this.sqlBuilder
                .append(" ")
                .append(predicate.operator().rendered());
        predicate.right().appendSQL(this);

    }

    @Override
    public final void appendTable(TableMeta<?> tableMeta) {
        if (this.tableContext.tableCountMap.containsKey(tableMeta)) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
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

    @Override
    public final DQL dql() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return sqlBuilder;
    }

    @Override
    public final void appendParam(ParamWrapper paramWrapper) {
        paramList.add(paramWrapper);
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return paramList;
    }

    @Override
    public final SimpleSQLWrapper build() {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        clauseStack.clear();
        return doBuild();
    }

    @Override
    public final DomainSQLWrapper build(DomainWrapper beanWrapper) {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        clauseStack.clear();
        return doBuild(beanWrapper);
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


    @Nullable
    protected final Clause currentClause() {
        return this.clauseStack.isEmpty() ? null : this.clauseStack.peek();
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

    protected SimpleSQLWrapper doBuild() {
        return SimpleSQLWrapper.build(
                sqlBuilder.toString()
                , paramList
        );
    }

    protected DomainSQLWrapper doBuild(DomainWrapper beanWrapper) {
        return DomainSQLWrapper.build(
                sqlBuilder.toString()
                , paramList
                , beanWrapper
        );
    }

}
