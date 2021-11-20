package io.army.dialect;

import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.stmt.SimpleStmt;
import io.army.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDQL extends AbstractDMLAndDQL implements DqlDialect {

    public AbstractDQL(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DQL method ##################################*/

    @Override
    public final SimpleStmt select(Select select, final Visible visible) {
        Assert.isTrue(select.prepared(), "select not prepared");

        SimpleStmt sqlWrapper;
        if (select instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeSelect = (_StandardComposeQuery) select;
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. create compose select context
            ComposeSelectContext context = ComposeSelectContext.build(this.dialect, visible);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof _SpecialComposeQuery) {
            _SpecialComposeQuery composeSelect = (_SpecialComposeQuery) select;
            //1. assert composeQuery legal
            assertSpecialComposeSelect(composeSelect);
            // 2. create compose select context
            SelectContext context = ComposeSelectContext.build(this.dialect, visible);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof _StandardSelect) {
            _StandardSelect standardSelect = (_StandardSelect) select;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSelect(standardSelect);
            // 2. create standard select context
            SelectContext context = SelectContextImpl.build(standardSelect, this.dialect, visible);
            // 3. parse standard select
            standardSelect(standardSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof _SpecialSelect) {
            _SpecialSelect specialSelect = (_SpecialSelect) select;
            //1. assert specialSubQuery legal
            assertSpecialSelect(specialSelect);
            // 2. create special context
            SelectContext context = SelectContextImpl.build(specialSelect, this.dialect, visible);
            // 3. parse special select
            specialSelect(specialSelect, context);
            sqlWrapper = context.build();

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
        return sqlWrapper;
    }


    @Override
    public final void select(Select select, SqlContext original) {
        Assert.isTrue(select.prepared(), "select not prepared");

        if (select instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeSelect = (_StandardComposeQuery) select;
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. adapt context
            _TableSqlContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof _SpecialComposeQuery) {
            _SpecialComposeQuery composeSelect = (_SpecialComposeQuery) select;
            //1. assert composeQuery legal
            assertSpecialComposeSelect(composeSelect);
            // 2. adapt context
            _TableSqlContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof _StandardSelect) {
            _StandardSelect standardSelect = (_StandardSelect) select;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSelect(standardSelect);
            // 2. adapt context
            _TableSqlContext context = adaptContext(standardSelect, original);
            // 3. parse standard select
            standardSelect(standardSelect, context);

        } else if (select instanceof _SpecialSelect) {
            _SpecialSelect specialSelect = (_SpecialSelect) select;
            //1. assert specialSubQuery legal
            assertSpecialSelect(specialSelect);
            // 2. adapt context
            _TableSqlContext context = adaptContext(specialSelect, original);
            // 3. parse special select
            specialSelect(specialSelect, context);

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
    }


    @Override
    public final void subQuery(SubQuery subQuery, SqlContext original) {
        Assert.isTrue(subQuery.prepared(), "PartQuery not prepared");
        if (subQuery instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeQuery = (_StandardComposeQuery) subQuery;
            //1. assert composeQuery legal
            CriteriaCounselor.assertStandardComposeSubQuery(composeQuery);
            // 2. adapt context
            _TableSqlContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof _SpecialComposeQuery) {
            _SpecialComposeQuery composeQuery = (_SpecialComposeQuery) subQuery;
            //1. assert composeQuery legal
            assertSpecialComposeSubQuery(composeQuery);
            // 2. adapt context
            _TableSqlContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSQL(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof _StandardSubQuery) {
            _StandardSubQuery standardSubQuery = (_StandardSubQuery) subQuery;
            //1. assert standardSubQuery legal
            CriteriaCounselor.assertStandardSubQuery(standardSubQuery);
            // 2. adapt context
            _TableSqlContext context = adaptContext(standardSubQuery, original);
            // 3. parse standard sub query
            standardSubQuery(standardSubQuery, context);

        } else if (subQuery instanceof _SpecialSubQuery) {
            _SpecialSubQuery specialSubQuery = (_SpecialSubQuery) subQuery;
            //1. assert specialSubQuery legal
            assertSpecialSubQuery(specialSubQuery);
            // 2. adapt context
            _TableSqlContext context = adaptContext(specialSubQuery, original);
            // 3. parse special sub query
            specialSubQuery(specialSubQuery, context);

        } else {
            throw new IllegalArgumentException(String.format("SubQuery[%s] type unknown."
                    , subQuery.getClass().getName()));
        }
    }


    /*################################## blow protected template method ##################################*/


    protected void assertSpecialComposeSelect(_SpecialComposeQuery select) {
        throw new UnsupportedOperationException();
    }

    protected void assertSpecialSubQuery(_SpecialSubQuery subQuery) {
        throw new UnsupportedOperationException();
    }

    protected void assertSpecialComposeSubQuery(_SpecialComposeQuery composeQuery) {
        throw new UnsupportedOperationException();
    }

    protected void assertSpecialSelect(_SpecialSelect select) {
        throw new UnsupportedOperationException();
    }

    protected void specialPartSelect(_SpecialComposeQuery select, _TableSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected void specialSelect(_SpecialSelect specialSelect, _TableSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected void specialSubQuery(_SpecialSubQuery composeQuery, _TableSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected final _TableSqlContext adaptContext(_GeneralQuery query, SqlContext context) {
        _TableSqlContext adaptedContext;
        if (query instanceof _ComposeQuery) {
            adaptedContext = (_TableSqlContext) context;
        } else if (query instanceof _Select) {
            adaptedContext = SelectContextImpl.build((_TableSqlContext) context, (_Select) query);
        } else if (query instanceof _SubQuery) {
            adaptedContext = SubQueryContextImpl.build((_TableSqlContext) context, (_SubQuery) query);
        } else {
            throw new UnKnownTypeException(query);
        }
        return adaptedContext;
    }


    protected abstract _TableSqlContext createSpecialSelectContext(_TableSqlContext original);

    protected abstract _TableSqlContext createSpecialSubQueryContext(_TableSqlContext original);


    protected abstract void limitClause(int offset, int rowCount, _TableSqlContext context);

    protected abstract void lockClause(LockMode lockMode, _TableSqlContext context);



    /*################################## blow final protected method ##################################*/

    protected final void selectClause(List<SQLModifier> modifierList, _TableSqlContext context) {

        SqlBuilder builder = context.sqlBuilder()
                .append(" SELECT");
        for (SQLModifier sqlModifier : modifierList) {
            builder.append(" ")
                    .append(sqlModifier.render());
        }
    }

    protected final void selectListClause(List<SelectPart> selectPartList, _TableSqlContext context) {
        SqlBuilder builder = context.sqlBuilder();
        int index = 0;
        for (SelectPart selectPart : selectPartList) {
            if (index > 0) {
                builder.append(",");
            }
            selectPart.appendSQL(context);
            index++;
        }
    }

    protected final void fromClause(List<? extends TableWrapper> tableWrapperList, _TableSqlContext context) {
        context.sqlBuilder()
                .append(" FROM");
        Map<String, TableWrapper> aliasMap = new HashMap<>();
        for (TableWrapper tableWrapper : tableWrapperList) {

            if (aliasMap.putIfAbsent(tableWrapper.alias(), tableWrapper) != null) {
                // avoid table alias duplication
                throw DialectUtils.createTableAliasDuplicationException(
                        tableWrapper.alias(), tableWrapper.tableAble());
            }
            // actual handle
            doTableWrapper(tableWrapper, context);
        }
    }

    protected final void whereClause(List<? extends TableWrapper> tableWrapperList, List<IPredicate> predicateList
            , _TableSqlContext context) {

        final boolean needAppendVisible = DialectUtils.needAppendVisible(tableWrapperList);
        final boolean hasPredicate = !predicateList.isEmpty();
        if (hasPredicate || needAppendVisible) {
            context.sqlBuilder()
                    .append(" WHERE");
        }

        if (hasPredicate) {
            DialectUtils.appendPredicateList(predicateList, context);
        }

        if (needAppendVisible) {
            appendVisiblePredicate(tableWrapperList, context, hasPredicate);
        }
    }

    protected final void groupByClause(List<SortPart> sortPartList, _TableSqlContext context) {
        if (!sortPartList.isEmpty()) {
            context.sqlBuilder()
                    .append(" GROUP BY");
            DialectUtils.appendSortPartList(sortPartList, context);
        }
    }

    protected final void havingClause(List<IPredicate> havingList, _TableSqlContext context) {
        if (!havingList.isEmpty()) {
            context.sqlBuilder()
                    .append(" HAVING");
            DialectUtils.appendPredicateList(havingList, context);
        }
    }

    protected final void orderByClause(List<SortPart> orderPartList, _TableSqlContext context) {
        if (!orderPartList.isEmpty()) {
            context.sqlBuilder()
                    .append(" ORDER BY");
            DialectUtils.appendSortPartList(orderPartList, context);
        }
    }

    /*################################## blow private method ##################################*/


    private void standardSelect(_StandardSelect select, _TableSqlContext context) {
        genericQuery(select, context);
        // lock clause
        LockMode lockMode = select.lockMode();
        if (lockMode != null) {
            lockClause(lockMode, context);
        }

    }

    private void standardSubQuery(_StandardSubQuery subQuery, _TableSqlContext context) {
        SqlBuilder builder = context.sqlBuilder();

        final boolean standardSubQueryInsert = context.parentContext() instanceof SubQueryInsertContext;
        if (standardSubQueryInsert) {
            builder.append(" ( ");
        } else {
            builder.append(" ");
        }

        genericQuery(subQuery, context);

        if (standardSubQueryInsert) {
            builder.append(" ) ");
        }

    }


    private void genericQuery(_Query query, _TableSqlContext context) {
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


    private void partQuery(_ComposeQuery query, _TableSqlContext context) {
        // order by clause
        orderByClause(query.orderByList(), context);
        // limit clause
        limitClause(query.offset(), query.rowCount(), context);
    }


}
