package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;


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

    @Deprecated
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


    protected final void visibleConstantPredicate(SingleTableMeta<?> table, @Nullable String safeTableAlias
            , _StmtContext context) {

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

            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.AND)
                    .append(Constant.SPACE);
            if (safeTableAlias != null) {
                sqlBuilder.append(safeTableAlias)
                        .append(Constant.POINT);
            }
            sqlBuilder.append(dialect.safeColumnName(field))
                    .append(Constant.SPACE)
                    .append(Constant.EQUAL)
                    .append(Constant.SPACE)
                    .append(dialect.constant(field.mappingMeta(), visibleValue));
        }

    }


    protected final void conditionUpdate(String safeTableAlias, List<FieldMeta<?, ?>> conditionFields
            , _StmtContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final Dialect dialect = context.dialect();
        for (FieldMeta<?, ?> field : conditionFields) {
            sqlBuilder
                    .append(Constant.SPACE)
                    .append(Constant.AND)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(dialect.safeColumnName(field))
                    .append(Constant.SPACE);

            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(Constant.IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(Constant.EQUAL);
                    dialect.defaultFunc(field, sqlBuilder);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }

        }

    }

    protected final List<GenericField<?, ?>> setClause(final boolean childTable, final _UpdateContext context) {

        final List<? extends SetTargetPart> targetPartList;
        final List<? extends SetValuePart> valuePartList;
        final TableMeta<?> table;
        final String safeTableAlias;
        if (childTable) {
            final _ChildUpdateContext childCtx = (_ChildUpdateContext) context;
            targetPartList = childCtx.childTargetParts();
            valuePartList = childCtx.childValueParts();
            table = childCtx.childTable();
            safeTableAlias = childCtx.childTableAlias();
        } else {
            targetPartList = context.targetParts();
            valuePartList = context.valueParts();
            table = context.table();
            safeTableAlias = context.safeTableAlias();
        }
        final int targetCount = targetPartList.size();

        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        List<GenericField<?, ?>> conditionFields = null;
        for (int i = 0; i < targetCount; i++) {
            if (i > 0) {
                sqlBuilder
                        .append(Constant.SPACE)
                        .append(Constant.COMMA);
            }
            final SetTargetPart targetPart = targetPartList.get(i);
            final SetValuePart valuePart = valuePartList.get(i);
            if (targetPart instanceof Row) {
                if (!(valuePart instanceof RowSubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
                }
                sqlBuilder.append(Constant.LEFT_BRACKET);
                int index = 0;
                for (FieldMeta<?, ?> field : ((Row<?>) targetPart).columnList()) {
                    if (field.tableMeta() != table) {
                        throw _Exceptions.unknownColumn(safeTableAlias, field);
                    }
                    if (index > 0) {
                        sqlBuilder
                                .append(Constant.SPACE)
                                .append(Constant.COMMA);
                    }
                    if (context instanceof _MultiUpdateContext) {
                        sqlBuilder.append(Constant.SPACE)
                                .append(safeTableAlias)
                                .append(Constant.POINT);
                    }
                    sqlBuilder.append(dialect.quoteIfNeed(field.columnName()));
                    index++;
                }
                sqlBuilder.append(Constant.RIGHT_BRACKET)
                        .append(Constant.SPACE)
                        .append(Constant.EQUAL);
                dialect.subQuery((SubQuery) targetPart, context);
                continue;
            } else if (!(targetPart instanceof FieldMeta)) {
                throw _Exceptions.unknownSetTargetPart(targetPart);
            } else if (!(valuePart instanceof _Expression)) {
                throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
            }
            final FieldMeta<?, ?> field = (FieldMeta<?, ?>) targetPart;
            switch (field.updateMode()) {
                case UPDATABLE:
                    // no-op
                    break;
                case IMMUTABLE:
                    throw _Exceptions.immutableField(field);

                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    if (conditionFields == null) {
                        conditionFields = new ArrayList<>();
                    }
                    conditionFields.add(field);
                }
                break;
                case ONLY_NULL: {
                    if (conditionFields == null) {
                        conditionFields = new ArrayList<>();
                    }
                    conditionFields.add(field);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.SET)
                    .append(Constant.SPACE)
                    .append(dialect.safeColumnName(field));

            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.EQUAL);

            ((_Expression<?>) valuePart).appendSql(context);
        }

        final List<FieldMeta<?, ?>> list;
        if (conditionFields == null) {
            list = Collections.emptyList();
        } else {
            list = CollectionUtils.unmodifiableList(conditionFields);
        }
        return list;
    }

    protected final void dmlWhereClause(_DmlContext context) {
        final List<_Predicate> predicateList = context.predicateList();
        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context.statement());
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(Constant.SPACE)
                .append(Constant.WHERE);
        for (int i = 0; i < predicateCount; i++) {
            if (i > 0) {
                sqlBuilder
                        .append(Constant.SPACE)
                        .append(Constant.AND);
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
        visibleConstantPredicate(context, childMeta.parentMeta(), parentAlias, true);
        builder.append(')');
    }


    private GenericField<?, ?> appendTargetValue()


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
