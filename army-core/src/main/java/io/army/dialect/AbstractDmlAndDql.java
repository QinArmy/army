package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.inner.TableBlock;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDmlAndDql extends AbstractSql {

    static final byte FOLLOW_PRIMARY_ROUTE = Byte.MIN_VALUE + 1;


    protected String subQueryParentAlias(String parentTableName) {
        Random random = new Random();
        return "_" + parentTableName + random.nextInt(4) + "_";
    }

    protected boolean supportTableOnly() {
        return false;
    }

    @Deprecated
    protected void tableOnlyModifier(_SqlContext context) {

    }

    protected void doTableWrapper(TableBlock tableBlock, _TablesSqlContext context) {
        final StringBuilder builder = context.sqlBuilder();
        // 1. form/join type
        SQLModifier joinType = tableBlock.jointType();
        if (!"".equals(joinType.render())) {
            builder.append(" ")
                    .append(joinType.render());
        }
        //2. append ONLY keyword ,eg: postgre,oracle.(optional)
        this.tableOnlyModifier(context);
        //3. append table able
        TablePart tableAble = tableBlock.table();
        if (tableAble instanceof TableMeta) {
            context.appendTable((TableMeta<?>) tableAble, tableBlock.alias());
        } else {
            tableAble.appendSql(context);
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendIdentifier(tableBlock.alias());
        }

        List<IPredicate> predicateList = tableBlock.onPredicateList();
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
            visiblePredicate(context, table, tableAlias, hasPredicate);
        } else if (table instanceof ChildTableMeta) {
            visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) table, tableAlias, hasPredicate);
        } else {
            throw new IllegalArgumentException(String.format("unknown %s.", table));
        }
    }

    protected final void appendVisiblePredicate(List<? extends TableBlock> tableWrapperList, _TablesSqlContext context
            , boolean hasPredicate) {
        // append visible predicates
//        final TableMeta<?> dual = null;
//        Map<String, ChildTableMeta<?>> childMap = new HashMap<>();
//        TableWrapper preTableWrapper = null;
//        for (TableWrapper tableWrapper : tableWrapperList) {
//            TableAble tableAble = tableWrapper.tableAble();
//
//            if ((tableAble instanceof TableMeta) && tableAble != dual) {
//
//                TableMeta<?> temp = (TableMeta<?>) tableAble;
//                if (tableAble instanceof ChildTableMeta) {
//                    temp = ((ChildTableMeta<?>) temp).parentMeta();
//                }
//                if (temp.containField(_MetaBridge.VISIBLE)) {
//                    appendVisibleIfNeed(tableWrapper, preTableWrapper, context, childMap, hasPredicate);
//                }
//            }
//            preTableWrapper = tableWrapper;
//        }
//
//        if (!childMap.isEmpty()) {
//            // child table append exists SubQuery
//            for (Map.Entry<String, ChildTableMeta<?>> e : childMap.entrySet()) {
//                visibleSubQueryPredicateForChild(context, e.getValue(), e.getKey(), hasPredicate);
//            }
//        }
    }

    @Deprecated
    protected final void visiblePredicate(_TablesSqlContext context
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


    protected final void discriminator(TableMeta<?> table, String safeTableAlias, _StmtContext context) {
        final FieldMeta<?, ?> field;
        if (table instanceof ChildTableMeta) {
            field = ((ChildTableMeta<?>) table).discriminator();
        } else if (table instanceof ParentTableMeta) {
            field = ((ParentTableMeta<?>) table).discriminator();
        } else {
            throw new IllegalArgumentException("table error");
        }
        final Dialect dialect = context.dialect();
        context.sqlBuilder()
                .append(AND)
                .append(Constant.SPACE)
                .append(safeTableAlias)
                .append(Constant.POINT)
                .append(dialect.quoteIfNeed(field.columnName()))
                .append(EQUAL)
                .append(Constant.SPACE)
                .append(dialect.literal(field, table.discriminatorValue()));
    }

    protected final void visiblePredicate(SingleTableMeta<?> table, final String safeTableAlias
            , final _StmtContext context) {

        final FieldMeta<?, ?> field = table.getField(_MetaBridge.VISIBLE);
        final Boolean visibleValue;
        switch (context.visible()) {
            case ONLY_VISIBLE:
                visibleValue = Boolean.TRUE;
                break;
            case ONLY_NON_VISIBLE:
                visibleValue = Boolean.FALSE;
                break;
            case BOTH:
                visibleValue = null;
                break;
            default:
                throw _Exceptions.unexpectedEnum(context.visible());
        }
        if (visibleValue != null) {
            final Dialect dialect = context.dialect();
            final StringBuilder sqlBuilder = context.sqlBuilder();

            sqlBuilder.append(AND)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(dialect.quoteIfNeed(field.columnName()))
                    .append(EQUAL)
                    .append(dialect.literal(field.mappingType(), visibleValue));
        }

    }


    protected final void conditionUpdate(String safeTableAlias, List<GenericField<?, ?>> conditionFields
            , _StmtContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        for (GenericField<?, ?> field : conditionFields) {
            final char[] safeColumnAlias = dialect.quoteIfNeed(field.columnName()).toCharArray();
            sqlBuilder
                    .append(AND)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(safeColumnAlias);

            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    sqlBuilder.append(EQUAL)
                            .append(Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(LEFT_BRACKET)
                            .append(safeTableAlias)
                            .append(Constant.POINT)
                            .append(safeColumnAlias)
                            .append(RIGHT_BRACKET);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }

        }

    }


    protected final void dmlWhereClause(_DmlContext context) {
        final List<_Predicate> predicateList = context.predicates();
        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context);
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(WHERE_WORD);
        for (int i = 0; i < predicateCount; i++) {
            if (i > 0) {
                sqlBuilder.append(AND);
            }
            predicateList.get(i).appendSql(context);
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
        visiblePredicate(context, childMeta.parentMeta(), parentAlias, true);
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

    private void appendVisibleIfNeed(TableBlock tableBlock, @Nullable TableBlock preTableBlock
            , _TablesSqlContext context, Map<String, ChildTableMeta<?>> childMap, boolean hasPredicate) {

        final TableMeta<?> table = (TableMeta<?>) tableBlock.table();
        if (table instanceof SimpleTableMeta) {
            visiblePredicate(context, table, tableBlock.alias(), hasPredicate);
        } else if (table instanceof ParentTableMeta) {
            visiblePredicate(context, table, tableBlock.alias(), hasPredicate);
            if (_DialectUtils.childJoinParent(tableBlock.onPredicateList(), table)) {
                if (preTableBlock != null) {
                    // remove child that joined by parent with primary key
                    childMap.remove(preTableBlock.alias());
                }
            }
        } else if (table instanceof ChildTableMeta) {
            if (preTableBlock == null) {
                childMap.put(tableBlock.alias(), (ChildTableMeta<?>) table);
            } else if (!_DialectUtils.parentJoinChild(tableBlock.onPredicateList(), table)) {
                childMap.put(tableBlock.alias(), (ChildTableMeta<?>) table);
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
