package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.wrapper.SQLWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDQL extends AbstractDMLAndDQL implements DQL {

    public AbstractDQL(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DQL method ##################################*/

    @Override
    public final List<SQLWrapper> select(Select select, final Visible visible) {
        Assert.isTrue(select.prepared(), "select not prepared");

        SQLWrapper sqlWrapper;
        if (select instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeSelect = (InnerStandardComposeQuery) select;
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            ClauseSQLContext context = createComposeSQLContext(composeSelect, visible);
            // compose select self describe
            composeSelect.appendSQL(context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ClauseSQLContext context = createComposeSQLContext(composeSelect, visible);
            // compose select self describe
            composeSelect.appendSQL(context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            CriteriaCounselor.assertStandardSelect(standardSelect);
            ClauseSQLContext context = createSelectContext(standardSelect, visible);
            // parse select sql
            standardSelect(standardSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialSelect) {
            InnerSpecialSelect specialSelect = (InnerSpecialSelect) select;
            assertSpecialSelect(specialSelect);
            ClauseSQLContext context = createSelectContext(specialSelect, visible);
            specialSelect(specialSelect, context);
            sqlWrapper = context.build();

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
        return Collections.singletonList(sqlWrapper);
    }


    @Override
    public final void select(Select select, SQLContext originalContext) {
        Assert.isTrue(select.prepared(), "select not prepared");

        if (select instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeSelect = (InnerStandardComposeQuery) select;
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            ClauseSQLContext context = adaptSelectContext(composeSelect, originalContext);
            // compose select self describe
            composeSelect.appendSQL(context);

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ClauseSQLContext context = adaptSelectContext(composeSelect, originalContext);
            // compose select self describe
            composeSelect.appendSQL(context);

        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            CriteriaCounselor.assertStandardSelect(standardSelect);
            ClauseSQLContext context = adaptSelectContext(standardSelect, originalContext);
            // parse select sql
            standardSelect(standardSelect, context);

        } else if (select instanceof InnerSpecialSelect) {
            InnerSpecialSelect specialSelect = (InnerSpecialSelect) select;
            assertSpecialSelect(specialSelect);
            ClauseSQLContext context = adaptSelectContext(specialSelect, originalContext);
            // parse select sql
            specialSelect(specialSelect, context);

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
    }

    @Override
    public final void partSelect(PartQuery select, SQLContext originalContext) {
        if (select instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeSelect = (InnerStandardComposeQuery) select;
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            ClauseSQLContext context = adaptSelectContext(composeSelect, originalContext);
            // compose select self describe
            standardPartQuery(composeSelect, context);

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ClauseSQLContext context = adaptSelectContext(composeSelect, originalContext);
            // compose select self describe
            specialPartSelect(composeSelect, context);

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown."
                    , select.getClass().getName()));
        }
    }

    @Override
    public final void partSubQuery(PartQuery subQuery, SQLContext originalContext) {
        if (subQuery instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeQuery = (InnerStandardComposeQuery) subQuery;
            ClauseSQLContext context = adaptSelectContext(composeQuery, originalContext);
            standardPartQuery(composeQuery, context);

        } else if (subQuery instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeQuery = (InnerSpecialComposeQuery) subQuery;
            ClauseSQLContext context = adaptSelectContext(composeQuery, originalContext);
            specialPartSelect(composeQuery, context);

        } else {
            throw new IllegalArgumentException(String.format("SubQuery[%s] type unknown."
                    , subQuery.getClass().getName()));
        }
    }

    @Override
    public final void subQuery(SubQuery subQuery, SQLContext originalContext) {
        if (subQuery instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeQuery = (InnerStandardComposeQuery) subQuery;
            CriteriaCounselor.assertStandardComposeSubQuery(composeQuery);
            ClauseSQLContext context = adaptSelectContext(composeQuery, originalContext);
            composeQuery.appendSQL(context);

        } else if (subQuery instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeQuery = (InnerSpecialComposeQuery) subQuery;
            assertSpecialComposeSubQuery(composeQuery);
            ClauseSQLContext context = adaptSelectContext(composeQuery, originalContext);
            composeQuery.appendSQL(context);

        } else if (subQuery instanceof InnerStandardSubQuery) {
            InnerStandardSubQuery standardSubQuery = (InnerStandardSubQuery) subQuery;
            CriteriaCounselor.assertStandardSubQuery(standardSubQuery);
            ClauseSQLContext context = adaptSelectContext(standardSubQuery, originalContext);
            standardSubQuery(standardSubQuery, context);

        } else if (subQuery instanceof InnerSpecialSubQuery) {
            InnerSpecialSubQuery specialSubQuery = (InnerSpecialSubQuery) subQuery;
            assertSpecialSubQuery(specialSubQuery);
            ClauseSQLContext context = adaptSelectContext(specialSubQuery, originalContext);
            specialSubQuery(specialSubQuery, context);

        } else {
            throw new IllegalArgumentException(String.format("SubQuery[%s] type unknown."
                    , subQuery.getClass().getName()));
        }
    }

    /*################################## blow protected template method ##################################*/


    protected abstract void assertSpecialComposeSelect(InnerSpecialComposeQuery select);

    protected abstract void assertSpecialSubQuery(InnerSpecialSubQuery subQuery);

    protected abstract void assertSpecialComposeSubQuery(InnerSpecialComposeQuery composeQuery);

    protected abstract void assertSpecialSelect(InnerSpecialSelect select);

    protected abstract void specialPartSelect(InnerSpecialComposeQuery select, ClauseSQLContext context);

    protected abstract void specialSelect(InnerSpecialSelect specialSelect, ClauseSQLContext context);

    protected abstract void specialSubQuery(InnerSpecialSubQuery composeQuery, ClauseSQLContext context);

    protected ClauseSQLContext createSelectContext(InnerSelect select, final Visible visible) {
        return new StandardSelectContext(this.dialect, visible, (InnerStandardSelect) select);
    }

    protected ClauseSQLContext adaptSelectContext(InnerGeneralQuery select, SQLContext context) {
        return null;
    }


    protected void doTableWrapper(TableWrapper tableWrapper, ClauseSQLContext context) {

    }

    protected abstract void limitClause(int offset, int rowCount, ClauseSQLContext context);

    protected abstract void lockClause(LockMode lockMode, ClauseSQLContext context);



    /*################################## blow final protected method ##################################*/

    protected final void selectClause(List<SQLModifier> modifierList, ClauseSQLContext context) {
        context.currentClause(Clause.SELECT);

        StringBuilder builder = context.sqlBuilder();
        for (SQLModifier sqlModifier : modifierList) {
            builder.append(sqlModifier.render());
        }
    }

    protected final void selectListClause(List<SelectPart> selectPartList, ClauseSQLContext context) {
        context.currentClause(Clause.SELECT_LIST);

        for (SelectPart selectPart : selectPartList) {
            if ((selectPart instanceof Selection) || (selectPart instanceof SelectionGroup)) {
                selectPart.appendSQL(context);
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "unknown SelectPart type[%s]", selectPart);
            }
        }
    }

    protected final void fromClause(List<TableWrapper> tableWrapperList, ClauseSQLContext context) {
        context.currentClause(Clause.FROM);
        context.sqlBuilder()
                .append(" ")
                .append(Keywords.FROM)
        ;
        Map<String, TableWrapper> aliasMap = new HashMap<>();
        for (TableWrapper tableWrapper : tableWrapperList) {

            if (aliasMap.putIfAbsent(tableWrapper.alias(), tableWrapper) != tableWrapper) {
                // avoid table alias duplication
                throw DialectUtils.createTableAliasDuplicationException(
                        tableWrapper.alias(), tableWrapper.tableAble());
            }
            doTableWrapper(tableWrapper, context);
        }
    }

    protected final void whereClause(List<TableWrapper> tableWrapperList, List<IPredicate> predicateList
            , ClauseSQLContext context) {

        context.currentClause(Clause.WHERE);

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.WHERE);
        int count = 0;
        for (IPredicate predicate : predicateList) {
            if (count > 0) {
                builder.append(" ").append(Keywords.AND);
            }
            predicate.appendSQL(context);
            count++;
        }
        // append visible predicates
        final TableMeta<?> dual = SQLS.dual();
        Map<String, ChildTableMeta<?>> childMap = new HashMap<>();
        TableWrapper preTableWrapper = null;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
            if ((tableAble instanceof TableMeta) && tableAble != dual) {
                appendVisibleIfNeed(tableWrapper, preTableWrapper, context, childMap);
            }
            preTableWrapper = tableWrapper;
        }

        if (!childMap.isEmpty()) {
            // child table add exists SubQuery
            for (Map.Entry<String, ChildTableMeta<?>> e : childMap.entrySet()) {
                visibleSubQueryPredicateForChild(context, e.getValue(), e.getKey());
            }
        }
    }

    protected final void groupByClause(List<SortPart> sortPartList, ClauseSQLContext context) {
        context.currentClause(Clause.GROUP_BY);

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.GROUP_BY);
        int count = 0;
        for (SortPart sortPart : sortPartList) {
            if (count > 0) {
                builder.append(",");
            }
            sortPart.appendSortPart(context);
            count++;
        }
    }

    protected final void havingClause(List<IPredicate> havingList, ClauseSQLContext context) {
        context.currentClause(Clause.HAVING);

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.HAVING);
        int count = 0;
        for (IPredicate predicate : havingList) {
            if (count > 0) {
                builder.append(" ")
                        .append(Keywords.AND)
                ;
            }
            predicate.appendSQL(context);
            count++;
        }
    }

    protected final void orderByClause(List<SortPart> orderPartList, ClauseSQLContext context) {
        context.currentClause(Clause.ORDER_BY);

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.ORDER_BY);
        int count = 0;
        for (SortPart sortPart : orderPartList) {
            if (count > 0) {
                builder.append(",");
            }
            sortPart.appendSortPart(context);
            count++;
        }
    }

    /*################################## blow private method ##################################*/


    private void standardSelect(InnerStandardSelect select, ClauseSQLContext context) {
        // select clause
        selectClause(select.modifierList(), context);
        // select list clause
        selectListClause(select.selectPartList(), context);
        // from clause
        fromClause(select.tableWrapperList(), context);
        // where clause
        whereClause(select.tableWrapperList(), select.predicateList(), context);
        // group by clause
        groupByClause(select.groupPartList(), context);
        // having clause
        havingClause(select.havingList(), context);
        // order by clause
        orderByClause(select.orderPartList(), context);
        // limit clause
        limitClause(select.offset(), select.rowCount(), context);
        // lock clause
        lockClause(select.lockMode(), context);

        context.currentClause(Clause.END);
    }

    private void standardSubQuery(InnerStandardSubQuery subQuery, ClauseSQLContext context) {

    }

    private void standardPartQuery(InnerStandardComposeQuery select, ClauseSQLContext context) {
        // order by clause
        orderByClause(select.orderPartList(), context);
        // limit clause
        limitClause(select.offset(), select.rowCount(), context);
    }


    private void appendVisibleIfNeed(TableWrapper tableWrapper, @Nullable TableWrapper preTableWrapper
            , ClauseSQLContext context, Map<String, ChildTableMeta<?>> childMap) {

        final TableMeta<?> tableMeta = (TableMeta<?>) tableWrapper.tableAble();
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                visibleConstantPredicate(context, tableMeta, tableWrapper.alias());
                break;
            case PARENT:
                visibleConstantPredicate(context, tableMeta, tableWrapper.alias());
                if (DialectUtils.childJoinParent(tableWrapper.onPredicateList(), tableMeta)) {
                    if (preTableWrapper != null) {
                        // remove child that joined by parent with primary key
                        childMap.remove(preTableWrapper.alias());
                    }
                }
                break;
            case CHILD:
                if (preTableWrapper == null) {
                    childMap.put(tableWrapper.alias(), (ChildTableMeta<?>) tableMeta);
                } else if (!DialectUtils.parentJoinChild(tableWrapper.onPredicateList(), tableMeta)) {
                    childMap.put(tableWrapper.alias(), (ChildTableMeta<?>) tableMeta);
                }
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(tableMeta.mappingMode());
        }
    }

    private ClauseSQLContext createComposeSQLContext(InnerComposeQuery select, Visible visible) {
        return new ComposeQuerySQLContext(visible);
    }


}
