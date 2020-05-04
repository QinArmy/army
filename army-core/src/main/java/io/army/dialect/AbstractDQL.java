package io.army.dialect;

import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.util.Assert;
import io.army.wrapper.SimpleSQLWrapper;

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
    public final List<SimpleSQLWrapper> select(Select select, final Visible visible) {
        Assert.isTrue(select.prepared(), "select not prepared");

        SimpleSQLWrapper sqlWrapper;
        if (select instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeSelect = (InnerStandardComposeQuery) select;
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            ComposeQueryContext context = new ComposeQueryContext(visible);
            // compose select self describe
            composeSelect.appendSQL(context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ComposeQueryContext context = new ComposeQueryContext(visible);
            // compose select self describe
            composeSelect.appendSQL(context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            CriteriaCounselor.assertStandardSelect(standardSelect);
            StandardSelectContext context = StandardSelectContext.build(this.dialect, visible, standardSelect);
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
            ClauseSQLContext context = adaptContext(composeSelect, originalContext);
            // compose select self describe
            composeSelect.appendSQL(context);

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ClauseSQLContext context = adaptContext(composeSelect, originalContext);
            // compose select self describe
            composeSelect.appendSQL(context);

        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            CriteriaCounselor.assertStandardSelect(standardSelect);
            ClauseSQLContext context = adaptContext(standardSelect, originalContext);
            // parse select sql
            standardSelect(standardSelect, context);

        } else if (select instanceof InnerSpecialSelect) {
            InnerSpecialSelect specialSelect = (InnerSpecialSelect) select;
            assertSpecialSelect(specialSelect);
            ClauseSQLContext context = adaptContext(specialSelect, originalContext);
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
            ClauseSQLContext context = adaptContext(composeSelect, originalContext);
            // compose select self describe
            standardPartQuery(composeSelect, context);

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            assertSpecialComposeSelect(composeSelect);
            ClauseSQLContext context = adaptContext(composeSelect, originalContext);
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
            ClauseSQLContext context = adaptContext(composeQuery, originalContext);
            standardPartQuery(composeQuery, context);

        } else if (subQuery instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeQuery = (InnerSpecialComposeQuery) subQuery;
            ClauseSQLContext context = adaptContext(composeQuery, originalContext);
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
            ClauseSQLContext context = adaptContext(composeQuery, originalContext);
            composeQuery.appendSQL(context);

        } else if (subQuery instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeQuery = (InnerSpecialComposeQuery) subQuery;
            assertSpecialComposeSubQuery(composeQuery);
            ClauseSQLContext context = adaptContext(composeQuery, originalContext);
            composeQuery.appendSQL(context);

        } else if (subQuery instanceof InnerStandardSubQuery) {
            InnerStandardSubQuery standardSubQuery = (InnerStandardSubQuery) subQuery;
            CriteriaCounselor.assertStandardSubQuery(standardSubQuery);
            ClauseSQLContext context = adaptContext(standardSubQuery, originalContext);
            standardSubQuery(standardSubQuery, context);

        } else if (subQuery instanceof InnerSpecialSubQuery) {
            InnerSpecialSubQuery specialSubQuery = (InnerSpecialSubQuery) subQuery;
            assertSpecialSubQuery(specialSubQuery);
            ClauseSQLContext context = adaptContext(specialSubQuery, originalContext);
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
        return new StandardSelectContext(this.dialect, visible);
    }

    protected final ClauseSQLContext adaptContext(InnerGeneralQuery query, SQLContext context) {
        ClauseSQLContext adaptedContext;
        if (query instanceof InnerComposeQuery) {
            adaptedContext = (ClauseSQLContext) context;
        } else if (query instanceof InnerSelect) {
            if (query instanceof InnerStandardSelect) {
                if (context instanceof ComposeQueryContext) {
                    adaptedContext = StandardSelectContext.build(this.dialect, (ComposeQueryContext) context);
                } else if (context instanceof StandardSelectContext) {
                    adaptedContext = (ClauseSQLContext) context;
                } else {
                    adaptedContext = StandardSelectContext.build((ClauseSQLContext) context);
                }
            } else {
                adaptedContext = createSpecialSelectContext((ClauseSQLContext) context);
            }
        } else if (query instanceof InnerSubQuery) {
            if (query instanceof InnerStandardSubQuery) {
                adaptedContext = StandardSubQueryContext.build((ClauseSQLContext) context);
            } else {
                adaptedContext = createSpecialSubQueryContext((ClauseSQLContext) context);
            }
        } else {
            throw new UnKnownTypeException(query);
        }
        return adaptedContext;
    }


    protected abstract ClauseSQLContext createSpecialSelectContext(ClauseSQLContext original);

    protected abstract ClauseSQLContext createSpecialSubQueryContext(ClauseSQLContext original);


    protected abstract void limitClause(int offset, int rowCount, ClauseSQLContext context);

    protected abstract void lockClause(LockMode lockMode, ClauseSQLContext context);



    /*################################## blow final protected method ##################################*/

    protected final void selectClause(List<SQLModifier> modifierList, ClauseSQLContext context) {
        context.currentClause(Clause.SELECT);

        StringBuilder builder = context.sqlBuilder();
        for (SQLModifier sqlModifier : modifierList) {
            builder.append(" ")
                    .append(sqlModifier.render());
        }
    }

    protected final void selectListClause(List<SelectPart> selectPartList, ClauseSQLContext context) {
        context.currentClause(Clause.SELECT_LIST);

        for (SelectPart selectPart : selectPartList) {
            if ((selectPart instanceof Selection) || (selectPart instanceof SelectionGroup)) {
                selectPart.appendSQL(context);
            } else {
                throw new UnKnownTypeException(selectPart);
            }
        }
    }

    protected final void fromClause(List<TableWrapper> tableWrapperList, ClauseSQLContext context) {
        context.currentClause(Clause.FROM);

        Map<String, TableWrapper> aliasMap = new HashMap<>();
        for (TableWrapper tableWrapper : tableWrapperList) {

            if (aliasMap.putIfAbsent(tableWrapper.alias(), tableWrapper) != tableWrapper) {
                // avoid table alias duplication
                throw DialectUtils.createTableAliasDuplicationException(
                        tableWrapper.alias(), tableWrapper.tableAble());
            }
            // actual handle
            doTableWrapper(tableWrapper, context);
        }
    }

    protected final void whereClause(List<TableWrapper> tableWrapperList, List<IPredicate> predicateList
            , ClauseSQLContext context) {

        final boolean needAppendVisible = DialectUtils.needAppendVisible(tableWrapperList);

        if (!predicateList.isEmpty() || needAppendVisible) {
            context.currentClause(Clause.WHERE);
        }

        if (!predicateList.isEmpty()) {
            DialectUtils.appendPredicateList(predicateList, context);
        }

        if (needAppendVisible) {
            appendVisiblePredicate(tableWrapperList, context);
        }
    }

    protected final void groupByClause(List<SortPart> sortPartList, ClauseSQLContext context) {
        if (!sortPartList.isEmpty()) {
            context.currentClause(Clause.GROUP_BY);
            DialectUtils.appendSortPartList(sortPartList, context);
        }
    }

    protected final void havingClause(List<IPredicate> havingList, ClauseSQLContext context) {
        if (!havingList.isEmpty()) {
            context.currentClause(Clause.HAVING);
            DialectUtils.appendPredicateList(havingList, context);
        }
    }

    protected final void orderByClause(List<SortPart> orderPartList, ClauseSQLContext context) {
        if (!orderPartList.isEmpty()) {
            context.currentClause(Clause.ORDER_BY);
            DialectUtils.appendSortPartList(orderPartList, context);
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


}
