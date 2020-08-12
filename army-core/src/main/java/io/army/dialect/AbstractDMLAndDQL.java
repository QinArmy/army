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


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDMLAndDQL extends AbstractSQL {

    protected AbstractDMLAndDQL(Dialect dialect) {
        super(dialect);
    }

    protected String subQueryParentAlias(String parentTableName) {
        Random random = new Random();
        return "_" + parentTableName + random.nextInt(4) + "_";
    }


    protected void tableOnlyModifier(SQLContext context) {

    }

    protected void doTableWrapper(TableWrapper tableWrapper, TableContextSQLContext context) {
        final SQLBuilder builder = context.sqlBuilder();
        // 1. form/join type
        SQLModifier joinType = tableWrapper.jointType();
        if (!"".equals(joinType.render())) {
            builder.append(" ")
                    .append(joinType.render());
        }
        //2. append ONLY keyword ,eg: postgre,oracle.(optional)
        this.tableOnlyModifier(context);
        //3. append table able
        TableAble tableAble = tableWrapper.tableAble();
        if (tableAble instanceof TableMeta) {
            context.appendTable((TableMeta<?>) tableAble, tableWrapper.alias());
        } else {
            tableAble.appendSQL(context);
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendText(tableWrapper.alias());
        }

        List<IPredicate> predicateList = tableWrapper.onPredicateList();
        if (predicateList.isEmpty()) {
            //TODO zoro ,think and validate this design.
            return;
        }
        //5.  on clause
        builder.append(" ON");
        DialectUtils.appendPredicateList(tableWrapper.onPredicateList(), context);

    }


    /*################################## blow final protected method ##################################*/


    protected final void appendVisiblePredicate(TableMeta<?> tableMeta, String tableAlias
            , TableContextSQLContext context, boolean hasPredicate) {
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
            case PARENT:
                visibleConstantPredicate(context, tableMeta, tableAlias, hasPredicate);
                break;
            case CHILD:
                visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) tableMeta, tableAlias, hasPredicate);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s].", tableMeta.mappingMode()));
        }
    }

    protected final void appendVisiblePredicate(List<? extends TableWrapper> tableWrapperList, TableContextSQLContext context
            , boolean hasPredicate) {
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
                if (temp.mappingProp(TableMeta.VISIBLE)) {
                    appendVisibleIfNeed(tableWrapper, preTableWrapper, context, childMap, hasPredicate);
                }
            }
            preTableWrapper = tableWrapper;
        }

        if (!childMap.isEmpty()) {
            // child table append exists SubQuery
            for (Map.Entry<String, ChildTableMeta<?>> e : childMap.entrySet()) {
                visibleSubQueryPredicateForChild(context, e.getValue(), e.getKey(), hasPredicate);
            }
        }
    }

    protected final void visibleConstantPredicate(TableContextSQLContext context
            , TableMeta<?> tableMeta, String tableAlias, boolean hasPredicate) {
        switch (context.visible()) {
            case ONLY_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.TRUE, tableMeta, tableAlias, hasPredicate);
                break;
            case ONLY_NON_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.FALSE, tableMeta, tableAlias, hasPredicate);
                break;
            case BOTH:
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown Visible[%s]", context.visible()));
        }
    }

    protected final void visibleSubQueryPredicateForChild(TableContextSQLContext context
            , ChildTableMeta<?> childMeta, String childAlias, boolean hasPredicate) {
        if (context.visible() == Visible.BOTH) {
            return;
        }

        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final String parentAlias = obtainParentAlias(context, childAlias);
        // append exists SubQuery
        SQLBuilder builder = context.sqlBuilder();
        if (hasPredicate) {
            builder.append(" AND");
        }
        builder.append(" EXISTS ( SELECT");
        context.appendField(parentAlias, parentMeta.id());
        // from clause
        builder.append(" FROM");
        // append parent table name and route suffix.
        context.appendParentOf(childMeta, childAlias);
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(parentAlias);
        // where clause
        builder.append(" WHERE");
        context.appendField(parentAlias, parentMeta.id());
        builder.append(" =");

        context.appendField(childAlias, childMeta.id());

        // visible predicate
        visibleConstantPredicate(context, childMeta.parentMeta(), parentAlias, true);
        builder.append(" )");
    }


    private void doVisibleConstantPredicate(TableContextSQLContext context, Boolean visible
            , TableMeta<?> tableMeta, String tableAlias, boolean hasPredicate) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        SQLBuilder builder = context.sqlBuilder();

        if (hasPredicate) {
            builder.append(" AND");
        }
        context.appendField(tableAlias, visibleField);

        builder.append(" = ")
                .append(visibleField.mappingMeta().toConstant(null, visible));
    }

    private void appendVisibleIfNeed(TableWrapper tableWrapper, @Nullable TableWrapper preTableWrapper
            , TableContextSQLContext context, Map<String, ChildTableMeta<?>> childMap, boolean hasPredicate) {

        final TableMeta<?> tableMeta = (TableMeta<?>) tableWrapper.tableAble();
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                visibleConstantPredicate(context, tableMeta, tableWrapper.alias(), hasPredicate);
                break;
            case PARENT:
                visibleConstantPredicate(context, tableMeta, tableWrapper.alias(), hasPredicate);
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


    private static String obtainParentAlias(TableContextSQLContext context, String childAlias) {
        String parentAlias;
        if (context instanceof SingleTableDMLContext) {
            parentAlias = ((SingleTableDMLContext) context).relationAlias();
        } else {
            parentAlias = TableContext.PARENT_ALIAS_PREFIX + childAlias;
        }
        return parentAlias;
    }
}
