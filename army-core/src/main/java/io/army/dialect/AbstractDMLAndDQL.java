package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractDMLAndDQL extends AbstractSQL {

    protected AbstractDMLAndDQL(InnerDialect dialect) {
        super(dialect);
    }

    protected String subQueryParentAlias(String parentTableName) {
        Random random = new Random();
        return "_" + parentTableName + random.nextInt(4) + "_";
    }

    protected abstract boolean tableAliasAfterAs();

    protected void tableOnlyModifier(SQLContext context) {

    }

    protected void doTableWrapper(TableWrapper tableWrapper, ClauseSQLContext context) {

        final StringBuilder builder = context.sqlBuilder();
        //1. append ONLY keyword ,eg: postgre,oracle.(optional)
        tableOnlyModifier(context);
        //2. append table able
        tableWrapper.tableAble().appendSQL(context);
        if (tableAliasAfterAs()) {
            builder.append(" ")
                    .append(Keywords.AS);
        }
        // 3. table alias
        context.appendText(tableWrapper.alias());
        // 4. join type
        SQLModifier joinType = tableWrapper.jointType();
        if (!"".equals(joinType.render())) {
            builder.append(" ")
                    .append(joinType.render());
        }
        List<IPredicate> predicateList = tableWrapper.onPredicateList();
        if (predicateList.isEmpty()) {
            return;
        }

        //5.  on clause
        context.currentClause(Clause.ON);
        int index = 0;
        for (IPredicate predicate : predicateList) {
            if (index > 0) {
                builder.append(" ")
                        .append(Keywords.AND);
            }
            predicate.appendSQL(context);
            index++;
        }

    }


    /*################################## blow final protected method ##################################*/


    protected final void appendVisiblePredicate(TableMeta<?> tableMeta, String tableAlias, ClauseSQLContext context) {
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
            case PARENT:
                visibleConstantPredicate(context, tableMeta, tableAlias);
                break;
            case CHILD:
                visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) tableMeta, tableAlias);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s].", tableMeta.mappingMode()));
        }
    }

    protected final void appendVisiblePredicate(List<TableWrapper> tableWrapperList, ClauseSQLContext context) {
        // append visible predicates
        final TableMeta<?> dual = SQLS.dual();
        Map<String, ChildTableMeta<?>> childMap = new HashMap<>();
        TableWrapper preTableWrapper = null;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if ((tableAble instanceof TableMeta) && tableAble != dual) {

                TableMeta<?> temp = (TableMeta<?>) tableAble;
                if (tableAble instanceof ChildTableMeta) {
                    temp = ((ChildTableMeta<?>) temp).parentMeta();
                }
                if (temp.isMappingProp(TableMeta.VISIBLE)) {
                    appendVisibleIfNeed(tableWrapper, preTableWrapper, context, childMap);
                }
            }
            preTableWrapper = tableWrapper;
        }

        if (!childMap.isEmpty()) {
            // child table append exists SubQuery
            for (Map.Entry<String, ChildTableMeta<?>> e : childMap.entrySet()) {
                visibleSubQueryPredicateForChild(context, e.getValue(), e.getKey());
            }
        }
    }

    protected final void visibleConstantPredicate(ClauseSQLContext context
            , TableMeta<?> tableMeta, String tableAlias) {
        switch (context.visible()) {
            case ONLY_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.TRUE, tableMeta, tableAlias);
                break;
            case ONLY_NON_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.FALSE, tableMeta, tableAlias);
                break;
            case BOTH:
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown Visible[%s]", context.visible()));
        }
    }


    protected final void visibleSubQueryPredicateForChild(ClauseSQLContext context
            , ChildTableMeta<?> childMeta, String childAlias) {
        if (context.visible() == Visible.BOTH) {
            return;
        }

        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final String parentAlias = subQueryParentAlias(parentMeta.tableName());
        // append exists SubQuery
        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.AND)
                .append(" ")
                .append(Keywords.EXISTS)
                .append(" ( ")
                .append(Keywords.SELECT);

        context.appendField(parentAlias, parentMeta.primaryKey());
        // from clause
        builder.append(" ")
                .append(Keywords.FROM);
        context.appendParentTableOf(childMeta);

        if (tableAliasAfterAs()) {
            builder.append(" ")
                    .append(Keywords.AS);
        }
        context.appendText(parentAlias);
        // where clause
        builder.append(" ")
                .append(Keywords.WHERE);
        context.appendField(parentAlias, parentMeta.primaryKey());
        builder.append(" =");

        context.appendField(childAlias, childMeta.primaryKey());

        // visible predicate
        visibleConstantPredicate(context, childMeta, childAlias);
        builder.append(" )");
    }


    private void doVisibleConstantPredicate(ClauseSQLContext context, Boolean visible
            , TableMeta<?> tableMeta, String tableAlias) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.AND);

        context.appendField(tableAlias, visibleField);

        builder.append(" =");
        SQLS.constant(visible, visibleField.mappingType())
                .appendSQL(context);

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


}
