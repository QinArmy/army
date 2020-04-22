package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSpecialSelect;
import io.army.criteria.impl.inner.InnerStandardSelect;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

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
        if (select instanceof SelfDescribedSelect) {
            ClauseSQLContext context = createDefaultSQLContext(visible);
            ((SelfDescribedSelect) select).appendSQL(context);
            sqlWrapper = context.build();
        } else if (select instanceof InnerStandardSelect) {
            CriteriaCounselor.assertStandardSelect((InnerStandardSelect) select);
            SelectContext context = createSelectContext((InnerStandardSelect) select, visible);
            // parse select sql
            standardSelectDispatcher(select, context);
            sqlWrapper = context.build();

        } else if (select instanceof InnerSpecialSelect) {
            sqlWrapper = specialSelect((InnerSpecialSelect) select, visible);
        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
        return Collections.singletonList(sqlWrapper);
    }


    @Override
    public final void select(Select select, SQLContext originalContext) {
        if (select instanceof SelfDescribedSelect) {
            ((SelfDescribedSelect) select).appendSQL(originalContext);
        } else if (select instanceof InnerStandardSelect) {
            CriteriaCounselor.assertStandardSelect((InnerStandardSelect) select);
            // adapt originalContext
            SelectContext context = adaptSelectContext((InnerStandardSelect) select, originalContext);
            // parse select sql
            standardSelectDispatcher(select, context);
        } else if (select instanceof InnerSpecialSelect) {
            specialSelect((InnerSpecialSelect) select, originalContext);
        } else {
            throw new IllegalArgumentException(String.format("Select[%s] type unknown.", select.getClass().getName()));
        }
    }

    @Override
    public final void partQuery(QueryAfterSet select, SQLContext context) {

    }

    @Override
    public final void subQuery(SubQuery subQuery, SQLContext context) {

    }

    /*################################## blow protected template method ##################################*/

    protected abstract SQLWrapper specialSelect(InnerSpecialSelect specialSelect, final Visible visible);

    protected abstract SQLWrapper specialSelect(InnerSpecialSelect specialSelect, SQLContext context);

    protected SelectContext createSelectContext(InnerSelect select, final Visible visible) {
        return new StandardSelectContext(this.dialect, visible, (InnerStandardSelect) select);
    }

    protected SelectContext adaptSelectContext(InnerSelect select, SQLContext context) {
        return null;
    }


    protected void doTableWrapper(TableWrapper tableWrapper, ClauseSQLContext context) {

    }

    protected abstract void limitClause(int offset, int rowCount, ClauseSQLContext context);

    protected abstract void lockClause(LockMode lockMode, ClauseSQLContext context);



    /*################################## blow final protected method ##################################*/

    protected final void standardSelectDispatcher(Select select, SelectContext context) {
        if (select instanceof SelfDescribedSelect) {
            ((SelfDescribedSelect) select).appendSQL(context);
        } else {
            standardSelect(select, context);
        }
    }

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
                .append(SQLFormat.FROM)
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
                .append(SQLFormat.WHERE);
        int count = 0;
        for (IPredicate predicate : predicateList) {
            if (count > 0) {
                builder.append(" ").append(SQLFormat.AND);
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
                .append(SQLFormat.GROUP_BY);
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
                .append(SQLFormat.HAVING);
        int count = 0;
        for (IPredicate predicate : havingList) {
            if (count > 0) {
                builder.append(" ")
                        .append(SQLFormat.AND)
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
                .append(SQLFormat.ORDER_BY);
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


    private void standardSelect(Select standardSelect, SelectContext context) {
        InnerStandardSelect select = (InnerStandardSelect) standardSelect;
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


    private void appendVisibleIfNeed(TableWrapper tableWrapper, @Nullable TableWrapper preTableWrapper
            , SQLContext context, Map<String, ChildTableMeta<?>> childMap) {

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

    private ClauseSQLContext createDefaultSQLContext(Visible visible) {
        return AbstractSQLContext.buildDefault(this.dialect, visible);
    }


}
