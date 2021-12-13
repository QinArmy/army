package io.army.dialect;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TableAble;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

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


    protected void tableOnlyModifier(_SqlContext context) {

    }

    protected void doTableWrapper(TableWrapper tableWrapper, _TablesSqlContext context) {
        final StringBuilder builder = context.sqlBuilder();
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
            tableAble.appendSql(context);
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendIdentifier(tableWrapper.alias());
        }

        List<IPredicate> predicateList = tableWrapper.onPredicateList();
        if (predicateList.isEmpty()) {
            //TODO zoro ,think and validate this design.
            return;
        }
        //5.  on clause
        builder.append(" ON");
        //DialectUtils.appendPredicateList(tableWrapper.onPredicateList(), context);

    }


    /*################################## blow final protected method ##################################*/


    protected final void appendVisiblePredicate(TableMeta<?> table, String tableAlias
            , _TablesSqlContext context, boolean hasPredicate) {
        if (table instanceof SingleTableMeta) {
            visibleConstantPredicate(context, table, tableAlias, hasPredicate);
        } else if (table instanceof ChildTableMeta) {
            visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) table, tableAlias, hasPredicate);
        } else {
            throw new IllegalArgumentException(String.format("unknown %s.", table));
        }
    }

    protected final void appendVisiblePredicate(List<? extends TableWrapper> tableWrapperList, _TablesSqlContext context
            , boolean hasPredicate) {
        // append visible predicates
        final TableMeta<?> dual = SQLs.dual();
        Map<String, ChildTableMeta<?>> childMap = new HashMap<>();
        TableWrapper preTableWrapper = null;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if ((tableAble instanceof TableMeta) && tableAble != dual) {

                TableMeta<?> temp = (TableMeta<?>) tableAble;
                if (tableAble instanceof ChildTableMeta) {
                    temp = ((ChildTableMeta<?>) temp).parentMeta();
                }
                if (temp.containField(_MetaBridge.VISIBLE)) {
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

    protected final void visibleConstantPredicate(_TablesSqlContext context
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

    protected final void visibleSubQueryPredicateForChild(_TablesSqlContext context
            , ChildTableMeta<?> childMeta, String childAlias, boolean hasPredicate) {
        if (context.visible() == Visible.BOTH) {
            return;
        }

        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final String parentAlias = obtainParentAlias(context, childAlias);
        // append exists SubQuery
        StringBuilder builder = context.sqlBuilder();
        if (hasPredicate) {
            builder.append(" AND");
        }
        builder.append(" EXISTS ( SELECT");
        context.appendField(parentAlias, parentMeta.id());
        // from clause
        builder.append(Constant.FROM);
        // append parent table name and route suffix.
        context.appendParentOf(childMeta, childAlias);
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendIdentifier(parentAlias);
        // where clause
        builder.append(Constant.WHERE);
        context.appendField(parentAlias, parentMeta.id());
        builder.append(" =");

        context.appendField(childAlias, childMeta.id());

        // visible predicate
        visibleConstantPredicate(context, childMeta.parentMeta(), parentAlias, true);
        builder.append(')');
    }


    private void doVisibleConstantPredicate(_TablesSqlContext context, Boolean visible
            , TableMeta<?> tableMeta, String tableAlias, boolean hasPredicate) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(_MetaBridge.VISIBLE);

        StringBuilder builder = context.sqlBuilder();

        if (hasPredicate) {
            builder.append(" AND");
        }
        context.appendField(tableAlias, visibleField);

//        builder.append(" = ")
//                .append(visibleField.mappingMeta().toConstant(null, visible));
    }

    private void appendVisibleIfNeed(TableWrapper tableWrapper, @Nullable TableWrapper preTableWrapper
            , _TablesSqlContext context, Map<String, ChildTableMeta<?>> childMap, boolean hasPredicate) {

        final TableMeta<?> table = (TableMeta<?>) tableWrapper.tableAble();
        if (table instanceof SimpleTableMeta) {
            visibleConstantPredicate(context, table, tableWrapper.alias(), hasPredicate);
        } else if (table instanceof ParentTableMeta) {
            visibleConstantPredicate(context, table, tableWrapper.alias(), hasPredicate);
            if (DialectUtils.childJoinParent(tableWrapper.onPredicateList(), table)) {
                if (preTableWrapper != null) {
                    // remove child that joined by parent with primary key
                    childMap.remove(preTableWrapper.alias());
                }
            }
        } else if (table instanceof ChildTableMeta) {
            if (preTableWrapper == null) {
                childMap.put(tableWrapper.alias(), (ChildTableMeta<?>) table);
            } else if (!DialectUtils.parentJoinChild(tableWrapper.onPredicateList(), table)) {
                childMap.put(tableWrapper.alias(), (ChildTableMeta<?>) table);
            }
        } else {
            throw _Exceptions.unknownTableType(table);
        }
    }


    private static String obtainParentAlias(_TablesSqlContext context, String childAlias) {
        String parentAlias;
        if (context instanceof SingleTableDMLContext) {
            parentAlias = ((SingleTableDMLContext) context).relationAlias();
        } else {
            parentAlias = TablesContext.PARENT_ALIAS_PREFIX + childAlias;
        }
        return parentAlias;
    }
}
