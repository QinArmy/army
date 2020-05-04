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
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. create standard select context
            StandardSelectContext context = StandardSelectContext.build(this.dialect, visible);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            //1. assert composeQuery legal
            assertSpecialComposeSelect(composeSelect);
            // 2. create special context
            ClauseSQLContext context = createSpecialContext(visible);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSelect(standardSelect);
            // 2. create standard select context
            StandardSelectContext context = StandardSelectContext.build(this.dialect, visible);
            // 3. parse standard select
            standardSelect(standardSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialSelect) {
            InnerSpecialSelect specialSelect = (InnerSpecialSelect) select;
            //1. assert specialSubQuery legal
            assertSpecialSelect(specialSelect);
            // 2. create special context
            ClauseSQLContext context = createSpecialContext(visible);
            // 3. parse special select
            specialSelect(specialSelect, context);
            sqlWrapper = context.build();

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
        return Collections.singletonList(sqlWrapper);
    }


    @Override
    public final void select(Select select, SQLContext original) {
        Assert.isTrue(select.prepared(), "select not prepared");

        if (select instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeSelect = (InnerStandardComposeQuery) select;
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeSelect = (InnerSpecialComposeQuery) select;
            //1. assert composeQuery legal
            assertSpecialComposeSelect(composeSelect);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof InnerStandardSelect) {
            InnerStandardSelect standardSelect = (InnerStandardSelect) select;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSelect(standardSelect);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(standardSelect, original);
            // 3. parse standard select
            standardSelect(standardSelect, context);

        } else if (select instanceof InnerSpecialSelect) {
            InnerSpecialSelect specialSelect = (InnerSpecialSelect) select;
            //1. assert specialSubQuery legal
            assertSpecialSelect(specialSelect);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(specialSelect, original);
            // 3. parse special select
            specialSelect(specialSelect, context);

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
    }


    @Override
    public final void subQuery(SubQuery subQuery, SQLContext original) {
        Assert.isTrue(subQuery.prepared(), "PartQuery not prepared");
        if (subQuery instanceof InnerStandardComposeQuery) {
            InnerStandardComposeQuery composeQuery = (InnerStandardComposeQuery) subQuery;
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSubQuery(composeQuery);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof InnerSpecialComposeQuery) {
            InnerSpecialComposeQuery composeQuery = (InnerSpecialComposeQuery) subQuery;
            //1. assert composeQuery legal
            assertSpecialComposeSubQuery(composeQuery);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof InnerStandardSubQuery) {
            InnerStandardSubQuery standardSubQuery = (InnerStandardSubQuery) subQuery;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSubQuery(standardSubQuery);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(standardSubQuery, original);
            // 3. parse standard sub query
            standardSubQuery(standardSubQuery, context);

        } else if (subQuery instanceof InnerSpecialSubQuery) {
            InnerSpecialSubQuery specialSubQuery = (InnerSpecialSubQuery) subQuery;
            //1. assert specialSubQuery legal
            assertSpecialSubQuery(specialSubQuery);
            // 2. adapt context
            ClauseSQLContext context = adaptContext(specialSubQuery, original);
            // 3. parse special sub query
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

    protected ClauseSQLContext createSpecialContext(final Visible visible) {
        return new StandardSelectContext(this.dialect, visible);
    }

    protected final ClauseSQLContext adaptContext(InnerGeneralQuery query, SQLContext context) {
        ClauseSQLContext adaptedContext;
        if (query instanceof InnerComposeQuery) {
            adaptedContext = (ClauseSQLContext) context;
        } else if (query instanceof InnerSelect) {
            adaptedContext = adaptSelectContext((InnerSelect) query, context);
        } else if (query instanceof InnerSubQuery) {
            adaptedContext = adaptSubQueryContext((InnerSubQuery) query, context);
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
        genericQuery(select, context);
        // lock clause
        lockClause(select.lockMode(), context);

    }

    private void standardSubQuery(InnerStandardSubQuery subQuery, ClauseSQLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append(" ( ");
        genericQuery(subQuery, context);

        builder.append(" ) ");
    }


    private void genericQuery(InnerQuery query, ClauseSQLContext context) {
        // select clause
        selectClause(query.modifierList(), context);
        // select list clause
        selectListClause(query.selectPartList(), context);
        // from clause
        fromClause(query.tableWrapperList(), context);
        // where clause
        whereClause(query.tableWrapperList(), query.predicateList(), context);
        // group by clause
        groupByClause(query.groupPartList(), context);
        // having clause
        havingClause(query.havingList(), context);
        // order by clause
        orderByClause(query.orderPartList(), context);
        // limit clause
        limitClause(query.offset(), query.rowCount(), context);
    }


    private void partQuery(InnerComposeQuery query, ClauseSQLContext context) {
        context.currentClause(Clause.PART_START);
        // order by clause
        orderByClause(query.orderPartList(), context);
        // limit clause
        limitClause(query.offset(), query.rowCount(), context);

        context.currentClause(Clause.PART_END);
    }


    private ClauseSQLContext adaptSelectContext(InnerSelect select, SQLContext context) {
        ClauseSQLContext adaptedContext;
        if (select instanceof InnerStandardSelect) {
            if (context instanceof StandardSelectContext) {
                adaptedContext = (ClauseSQLContext) context;
            } else if (select instanceof InnerSpecialSelect) {
                adaptedContext = createSpecialSelectContext((ClauseSQLContext) context);
            } else {
                throw new UnKnownTypeException(select);
            }
        } else if (select instanceof InnerSpecialSelect) {
            adaptedContext = createSpecialSelectContext((ClauseSQLContext) context);
        } else {
            throw new UnKnownTypeException(select);
        }
        return adaptedContext;
    }

    private ClauseSQLContext adaptSubQueryContext(InnerSubQuery subQuery, SQLContext context) {
        ClauseSQLContext adaptedContext;
        if (subQuery instanceof InnerStandardSubQuery) {
            adaptedContext = StandardSubQueryContext.build((ClauseSQLContext) context);
        } else {
            adaptedContext = createSpecialSubQueryContext((ClauseSQLContext) context);
        }
        return adaptedContext;
    }


}
