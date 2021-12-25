package io.army.dialect;

import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.*;
import io.army.stmt.SimpleStmt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDQL extends AbstractDmlAndDql implements SqlDialect {

    public AbstractDQL(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DQL method ##################################*/

    @Override
    public final SimpleStmt select(Select select, final Visible visible) {
        select.prepared();

        SimpleStmt sqlWrapper;
        if (select instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeSelect = (_StandardComposeQuery) select;
            //1. assert composeQuery legal
            _CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. create compose select context
            ComposeSelectContext context = ComposeSelectContext.build(this.dialect, visible);
            // 3. append composeQuery
            composeSelect.appendSql(context);
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
            composeSelect.appendSql(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
            sqlWrapper = context.build();

        } else if (select instanceof _StandardSelect) {
            _StandardSelect standardSelect = (_StandardSelect) select;
            //1. assert standardSubQuery legal
            _CriteriaCounselor.assertStandardSelect(standardSelect);
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
    public final void select(Select select, _SqlContext original) {
        select.prepared();

        if (select instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeSelect = (_StandardComposeQuery) select;
            //1. assert composeQuery legal
            _CriteriaCounselor.assertStandardComposeSelect(composeSelect);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSql(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof _SpecialComposeQuery) {
            _SpecialComposeQuery composeSelect = (_SpecialComposeQuery) select;
            //1. assert composeQuery legal
            assertSpecialComposeSelect(composeSelect);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(composeSelect, original);
            // 3. append composeQuery
            composeSelect.appendSql(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeSelect, context);
        } else if (select instanceof _StandardSelect) {
            _StandardSelect standardSelect = (_StandardSelect) select;
            //1. assert standardSubQuery legal
            _CriteriaCounselor.assertStandardSelect(standardSelect);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(standardSelect, original);
            // 3. parse standard select
            standardSelect(standardSelect, context);

        } else if (select instanceof _SpecialSelect) {
            _SpecialSelect specialSelect = (_SpecialSelect) select;
            //1. assert specialSubQuery legal
            assertSpecialSelect(specialSelect);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(specialSelect, original);
            // 3. parse special select
            specialSelect(specialSelect, context);

        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
    }


    @Override
    public final void subQuery(SubQuery subQuery, _SqlContext original) {
        subQuery.prepared();
        if (subQuery instanceof _StandardComposeQuery) {
            _StandardComposeQuery composeQuery = (_StandardComposeQuery) subQuery;
            //1. assert composeQuery legal
            _CriteriaCounselor.assertStandardComposeSubQuery(composeQuery);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSql(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof _SpecialComposeQuery) {
            _SpecialComposeQuery composeQuery = (_SpecialComposeQuery) subQuery;
            //1. assert composeQuery legal
            assertSpecialComposeSubQuery(composeQuery);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(composeQuery, original);
            // 3. append composeQuery
            composeQuery.appendSql(context);
            // 4. append part query ,eg: order by ,limit
            partQuery(composeQuery, context);
        } else if (subQuery instanceof _StandardSubQuery) {
            _StandardSubQuery standardSubQuery = (_StandardSubQuery) subQuery;
            //1. assert standardSubQuery legal
            _CriteriaCounselor.assertStandardSubQuery(standardSubQuery);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(standardSubQuery, original);
            // 3. parse standard sub query
            standardSubQuery(standardSubQuery, context);

        } else if (subQuery instanceof _SpecialSubQuery) {
            _SpecialSubQuery specialSubQuery = (_SpecialSubQuery) subQuery;
            //1. assert specialSubQuery legal
            assertSpecialSubQuery(specialSubQuery);
            // 2. adapt context
            _TablesSqlContext context = adaptContext(specialSubQuery, original);
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

    protected void specialPartSelect(_SpecialComposeQuery select, _TablesSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected void specialSelect(_SpecialSelect specialSelect, _TablesSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected void specialSubQuery(_SpecialSubQuery composeQuery, _TablesSqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected final _TablesSqlContext adaptContext(_GeneralQuery query, _SqlContext context) {
        _TablesSqlContext adaptedContext;
        if (query instanceof _ComposeQuery) {
            adaptedContext = (_TablesSqlContext) context;
        } else if (query instanceof _Select) {
            adaptedContext = SelectContextImpl.build((_TablesSqlContext) context, (_Select) query);
        } else if (query instanceof _SubQuery) {
            adaptedContext = SubQueryContextImpl.build((_TablesSqlContext) context, (_SubQuery) query);
        } else {
            throw new UnKnownTypeException(query);
        }
        return adaptedContext;
    }


    protected abstract _TablesSqlContext createSpecialSelectContext(_TablesSqlContext original);

    protected abstract _TablesSqlContext createSpecialSubQueryContext(_TablesSqlContext original);


    protected abstract void limitClause(int offset, int rowCount, _TablesSqlContext context);

    protected abstract void lockClause(LockMode lockMode, _TablesSqlContext context);



    /*################################## blow final protected method ##################################*/

    protected final void selectClause(List<SQLModifier> modifierList, _TablesSqlContext context) {

        StringBuilder builder = context.sqlBuilder()
                .append(" SELECT");
        for (SQLModifier sqlModifier : modifierList) {
            builder.append(" ")
                    .append(sqlModifier.render());
        }
    }

    protected final void selectListClause(List<SelectPart> selectPartList, _TablesSqlContext context) {
        StringBuilder builder = context.sqlBuilder();
        int index = 0;
        for (SelectPart selectPart : selectPartList) {
            if (index > 0) {
                builder.append(",");
            }
            ((_SelfDescribed) selectPart).appendSql(context);
            index++;
        }
    }

    protected final void fromClause(List<? extends _TableBlock> tableWrapperList, _TablesSqlContext context) {
        context.sqlBuilder()
                .append(" FROM");
        Map<String, _TableBlock> aliasMap = new HashMap<>();
        for (_TableBlock tableBlock : tableWrapperList) {

            if (aliasMap.putIfAbsent(tableBlock.alias(), tableBlock) != null) {
                // avoid table alias duplication
                throw _DialectUtils.createTableAliasDuplicationException(
                        tableBlock.alias(), tableBlock.table());
            }
            // actual handle
            doTableWrapper(tableBlock, context);
        }
    }

    protected final void whereClause(List<? extends _TableBlock> tableWrapperList, List<_Predicate> predicateList
            , _TablesSqlContext context) {

        final boolean needAppendVisible = _DialectUtils.needAppendVisible(tableWrapperList);
        final boolean hasPredicate = !predicateList.isEmpty();
        if (hasPredicate || needAppendVisible) {
            context.sqlBuilder()
                    .append(" WHERE");
        }

        if (hasPredicate) {
            _DialectUtils.appendPredicateList(predicateList, context);
        }

        if (needAppendVisible) {
            appendVisiblePredicate(tableWrapperList, context, hasPredicate);
        }
    }

    protected final void groupByClause(List<_SortPart> sortPartList, _TablesSqlContext context) {
        if (!sortPartList.isEmpty()) {
            context.sqlBuilder()
                    .append(" GROUP BY");
            _DialectUtils.appendSortPartList(sortPartList, context);
        }
    }

    protected final void havingClause(List<_Predicate> havingList, _TablesSqlContext context) {
        if (!havingList.isEmpty()) {
            context.sqlBuilder()
                    .append(" HAVING");
            _DialectUtils.appendPredicateList(havingList, context);
        }
    }

    protected final void orderByClause(List<_SortPart> orderPartList, _TablesSqlContext context) {
        if (!orderPartList.isEmpty()) {
            context.sqlBuilder()
                    .append(" ORDER BY");
            _DialectUtils.appendSortPartList(orderPartList, context);
        }
    }

    /*################################## blow private method ##################################*/


    private void standardSelect(_StandardSelect select, _TablesSqlContext context) {
        genericQuery(select, context);
        // lock clause
        LockMode lockMode = select.lockMode();
        if (lockMode != null) {
            lockClause(lockMode, context);
        }

    }

    private void standardSubQuery(_StandardSubQuery subQuery, _TablesSqlContext context) {
        StringBuilder builder = context.sqlBuilder();

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


    private void genericQuery(_Query query, _TablesSqlContext context) {
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


    private void partQuery(_ComposeQuery query, _TablesSqlContext context) {
        // order by clause
        orderByClause(query.orderByList(), context);
        // limit clause
        limitClause(query.offset(), query.rowCount(), context);
    }


}
