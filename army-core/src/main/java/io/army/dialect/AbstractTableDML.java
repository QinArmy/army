package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerSingleDeleteAble;
import io.army.criteria.impl.inner.InnerSingleUpdateAble;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractTableDML implements TableDML {

    private final SQL sql;

    public AbstractTableDML(SQL sql) {
        this.sql = sql;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public final String quoteIfNeed(String identifier) {
        return sql.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return sql.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return sql.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return sql.sessionFactory();
    }

    /*################################## blow TableDML method ##################################*/

    @Override
    public final List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entityWrapper, "entity required");
        Assert.isTrue(tableMeta.javaType() == entityWrapper.getWrappedClass(), "tableMata then entity not match");

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = createInsertForSimple(tableMeta, entityWrapper);
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild(tableMeta, entityWrapper);
                break;
            case PARENT:
                sqlWrapperList = createInsertForParent(tableMeta, entityWrapper);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return sqlWrapperList;
    }

    @Override
    public List<SQLWrapper> update(SingleUpdateAble updateAble, Visible visible) {
        Assert.isInstanceOf(InnerSingleUpdateAble.class, updateAble, "");

        InnerSingleUpdateAble innerAble = (InnerSingleUpdateAble) updateAble;
        TableMeta<?> tableMeta = innerAble.tableMeta();

        List<SQLWrapper> wrapperList;
        if (tableMeta.parentMeta() == null) {
            wrapperList = new ArrayList<>(1);
            // create update sql for simple(parentMeta) mapping mode
            wrapperList.add(createUpdateForSimple(innerAble, visible));
        } else {
            // create update sql for child mapping mode
            wrapperList = createUpdateForChild(innerAble, visible);
        }
        return Collections.unmodifiableList(wrapperList);
    }

    @Override
    public List<SQLWrapper> delete(SingleDeleteAble singleDeleteAble, Visible visible) {
        InnerSingleDeleteAble deleteAble = (InnerSingleDeleteAble) singleDeleteAble;
        TableMeta<?> tableMeta = deleteAble.tableMeta();
        List<SQLWrapper> list;
        if (tableMeta.parentMeta() == null) {
            list = createDeleteForSimple(deleteAble, visible);
        } else {
            list = createDeleteForChild(deleteAble, visible);
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow protected template method ##################################*/

    /*################################## blow protected method ##################################*/

    /*################################## blow private method ##################################*/

    private List<SQLWrapper> createDeleteForChild(InnerSingleDeleteAble deleteAble, Visible visible) {
        return Collections.emptyList();
    }


    private List<SQLWrapper> createDeleteForSimple(InnerSingleDeleteAble deleteAble, Visible visible) {

        final SQLContext context = new DefaultSQLContext(this.sql);
        TableMeta<?> tableMeta = deleteAble.tableMeta();

        final String tableName = this.sql.quoteIfNeed(tableMeta.tableName());
        // 1. delete clause
        appendDeleteClause(context, tableName, deleteAble);
        // 2. where clause
        appendDeleteWhereClause(context, deleteAble, visible);
        // 3. order by clause
        appendOrderByClause(context, deleteAble.orderExpList(), deleteAble.ascList());
        // 4. limit clause
        appendLimitClause(context, deleteAble.rowCount());

        SQLWrapper sqlWrapper = SQLWrapper.build(context.stringBuilder().toString(), context.paramWrapper());
        List<SQLWrapper> list = new ArrayList<>(1);
        list.add(sqlWrapper);
        return list;
    }

    private void appendDeleteClause(SQLContext context, String tableName, InnerSingleDeleteAble deleteAble) {
        context.stringBuilder()
                .append("DELETE FROM ")
                .append(tableName);

    }

    private void appendDeleteWhereClause(SQLContext context, InnerSingleDeleteAble deleteAble, Visible visible) {

        List<IPredicate> predicateList = deleteAble.predicateList();
        Assert.notEmpty(predicateList, "no where clause forbidden by army");

        StringBuilder builder = context.stringBuilder()
                .append(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
        }
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            FieldMeta<?, ?> visibleField = deleteAble.tableMeta().getField(TableMeta.VISIBLE);
            String textValue = visibleField.mappingType().nonNullTextValue(visibleValue);
            builder.append(" AND ")
                    .append(this.quoteIfNeed(visibleField.fieldName()))
                    .append(" = ")
                    .append(DialectUtils.quoteIfNeed(visibleField.mappingType(), textValue))
            ;
        }
    }


    private List<SQLWrapper> createUpdateForChild(InnerSingleUpdateAble innerAble, Visible visible) {
        return Collections.emptyList();
    }


    private SQLWrapper createUpdateForSimple(InnerSingleUpdateAble innerAble, Visible visible) {

        StringBuilder builder = new StringBuilder();
        List<ParamWrapper> paramWrapperList = new ArrayList<>();

        String tableAlias = innerAble.tableAlias();

        if (StringUtils.hasText(tableAlias)) {
            tableAlias = this.sql.quoteIfNeed(tableAlias);
        } else {
            tableAlias = "";
        }
        // build sql context
        final SQLContext context = new SingleUpdateSQLContext(this.sql, builder, paramWrapperList
                , tableAlias, innerAble.tableMeta());

        //1. update clause
        appendUpdateClause(context, tableAlias, innerAble);
        //2. set clause
        appendSetClause(context, tableAlias, innerAble);
        //3. where clause
        boolean hasVersion;
        hasVersion = appendWhereClause(context, tableAlias, innerAble, visible);
        //4. order clause
        appendOrderByClause(context, innerAble.orderExpList(), innerAble.ascExpList());
        //5. limit clause
        appendLimitClause(context, innerAble.rowCount());

        return SQLWrapper.build(builder.toString(), paramWrapperList, hasVersion);
    }

    private void appendUpdateClause(SQLContext context, String tableAlias, InnerSingleUpdateAble innerAble) {
        context.stringBuilder().append("UPDATE ")
                .append(this.sql.quoteIfNeed(innerAble.tableMeta().tableName()));

        if (StringUtils.hasText(tableAlias)) {
            context.stringBuilder()
                    .append(" AS ")
                    .append(tableAlias);
        }
    }

    private void appendSetClause(SQLContext context, final String tableAlias, InnerSingleUpdateAble innerAble) {
        TableMeta<?> tableMeta = innerAble.tableMeta();

        List<FieldMeta<?, ?>> fieldMetaList = innerAble.targetFieldList();
        List<Expression<?>> valueExpList = innerAble.valueExpressionList();

        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "updateAble error");
        StringBuilder builder = context.stringBuilder();
        builder.append(" SET");
        final int size = fieldMetaList.size();
        for (int i = 0; i < size; i++) {
            FieldMeta<?, ?> fieldMeta = fieldMetaList.get(i);
            assertTargetField(fieldMeta, tableMeta);

            if (StringUtils.hasText(tableAlias)) {
                builder.append(" ")
                        .append(tableAlias)
                        .append(".");
            }
            builder.append(this.sql.quoteIfNeed(fieldMeta.fieldName()))
                    .append(" =");
            Expression<?> valueExp = valueExpList.get(i);
            // assert not null
            assertFieldAndValueExpressionMatch(fieldMeta, valueExp);
            // append expression
            valueExp.appendSQL(context);
            if (i < size - 1) {
                builder.append(",");
            }

        }
        // append update_time and version field
        appendFieldsManagedByArmy(tableMeta, context, tableAlias);
    }

    private void appendFieldsManagedByArmy(TableMeta<?> tableMeta, SQLContext context, final String tableAlias) {
        //1. version field
        FieldMeta<?, ?> fieldMeta = tableMeta.getField(TableMeta.VERSION);
        StringBuilder builder = context.stringBuilder();
        builder.append(",");
        String qualifiedName = this.sql.quoteIfNeed(fieldMeta.fieldName());
        if (StringUtils.hasText(tableAlias)) {
            qualifiedName = " " + tableAlias + "." + qualifiedName;
        }
        builder.append(qualifiedName)
                .append(" = ")
                .append(qualifiedName)
                .append(" + 1")
        ;

        //2. updateTime field
        fieldMeta = tableMeta.getField(TableMeta.UPDATE_TIME);
        builder.append(",");
        qualifiedName = this.sql.quoteIfNeed(fieldMeta.fieldName());
        if (StringUtils.hasText(tableAlias)) {
            qualifiedName = " " + tableAlias + "." + qualifiedName;
        }
        builder.append(qualifiedName)
                .append(" =");

        if (fieldMeta.javaType() == LocalDateTime.class) {
            SQLS.param(LocalDateTime.now()).appendSQL(context);
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            SQLS.param(ZonedDateTime.now(this.zoneId())).appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private boolean appendWhereClause(SQLContext context, final String tableAlias, InnerSingleUpdateAble innerAble
            , Visible visible) {

        final List<IPredicate> IPredicateList = innerAble.predicateList();
        Assert.notEmpty(IPredicateList, "where clause must be not empty");

        StringBuilder builder = context.stringBuilder().append(" WHERE");

        IPredicate IPredicate;
        final TableMeta<?> tableMeta = innerAble.tableMeta();

        final FieldMeta<?, ?> versionField = tableMeta.getField(TableMeta.VERSION);

        boolean hasVersion = false;
        for (Iterator<IPredicate> iterator = IPredicateList.iterator(); iterator.hasNext(); ) {
            IPredicate = iterator.next();
            // append predicate
            IPredicate.appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
            if (IPredicate instanceof DualIPredicate) {
                DualIPredicate dualPredicate = (DualIPredicate) IPredicate;
                // version = expresion
                if (versionField == dualPredicate.leftExpression()
                        && dualPredicate.dualOperator() == DualOperator.EQ) {
                    hasVersion = true;
                }
            }
        }
        // append visible predicate
        appendVisiblePredicate(tableMeta, context, tableAlias, visible);
        return hasVersion;
    }

    private void appendVisiblePredicate(TableMeta<?> tableMeta, SQLContext context
            , final String tableAlias, Visible visible) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        Assert.state(visibleField.javaType() == Boolean.class, "visible prop class type only is Boolean");
        // append visible field
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            StringBuilder builder = context.stringBuilder().append(" AND ");
            if (StringUtils.hasText(tableAlias)) {
                builder.append(tableAlias)
                        .append(".");
            }
            builder.append(this.sql.quoteIfNeed(visibleField.fieldName()))
                    .append(" = ? ");
            context.appendParam(ParamWrapper.build(visibleField.mappingType(), visibleValue));
        }

    }

    private void appendOrderByClause(SQLContext context, List<Expression<?>> orderExpList, List<Boolean> ascExpList) {
        if(CollectionUtils.isEmpty(orderExpList)){
            return;
        }
        Assert.isTrue(orderExpList.size() == ascExpList.size(), "orderExpList size and ascExpList size not match.");

        StringBuilder builder = context.stringBuilder()
                .append(" ORDER BY");

        Expression<?> orderExp;
        Boolean ascExp;
        final int size = orderExpList.size();
        for (int i = 0; i < size; i++) {
            orderExp = orderExpList.get(i);
            orderExp.appendSQL(context);
            ascExp = ascExpList.get(i);

            if (Boolean.TRUE.equals(ascExp)) {
                builder.append(" ASC");
            } else if (Boolean.FALSE.equals(ascExp)) {
                builder.append(" DESC");
            }
            if (i < size - 1) {
                builder.append(",");
            }
        }
    }

    private void appendLimitClause(SQLContext context, int rowCount) {
        if (rowCount > -1) {
            context.stringBuilder().append(" LIMIT ?");
            context.appendParam(
                    ParamWrapper.build(MappingFactory.getDefaultMapping(Integer.class), rowCount)
            );
        }
    }


    /**
     * @return a modifiable list
     */
    private <T extends IDomain> List<SQLWrapper> createInsertForSimple(TableMeta<T> tableMeta
            , ReadonlyWrapper entityWrapper) {
        StringBuilder nameBuilder = new StringBuilder("INSERT INTO "), valueBuilder = new StringBuilder(" VALUE(");
        final List<ParamWrapper> paramWrapperList = new ArrayList<>();

        nameBuilder.append(quoteIfNeed(tableMeta.tableName()));
        nameBuilder.append("(");

        Object value;
        int count = 0;
        for (FieldMeta<T, ?> fieldMeta : tableMeta.fieldCollection()) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = entityWrapper.getPropertyValue(fieldMeta.propertyName());
            if (value == null && !fieldMeta.nullable()) {
                continue;
            }
            if (count != 0) {
                nameBuilder.append(",");
                valueBuilder.append(",");
            }
            // name
            nameBuilder.append(quoteIfNeed(fieldMeta.fieldName()));
            // value
            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else {
                valueBuilder.append("?");
                paramWrapperList.add(ParamWrapper.build(fieldMeta.mappingType(), value));
            }
            count++;
        }

        nameBuilder.append(")");
        valueBuilder.append(")");

        return Collections.singletonList(
                SQLWrapper.build(
                        nameBuilder.toString() + valueBuilder.toString()
                        , paramWrapperList)
        );

    }

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> createInsertForParent(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        return createInsertForSimple(tableMeta, entityWrapper);
    }

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> createInsertForChild(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        TableMeta<?> parentMeta = tableMeta.parentMeta();
        Assert.state(parentMeta != null, () -> String.format("entity[%s] parentMeta", tableMeta.javaType().getName()));

        List<SQLWrapper> parentSqlList = createInsertForParent(parentMeta, entityWrapper);
        List<SQLWrapper> sqlWrapperList = createInsertForSimple(tableMeta, entityWrapper);

        List<SQLWrapper> actualSqlList = new ArrayList<>(parentSqlList.size() + sqlWrapperList.size());
        actualSqlList.addAll(parentSqlList);
        actualSqlList.addAll(sqlWrapperList);

        return Collections.unmodifiableList(actualSqlList);
    }

    private Object createConstant(FieldMeta<?, ?> fieldMeta) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == fieldMeta.tableMeta().discriminator()) {
            value = fieldMeta.tableMeta().discriminatorValue();
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    private boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.tableMeta().discriminator()
                ;
    }

    private void assertTargetField(FieldMeta<?, ?> fieldMeta, TableMeta<?> tableMeta) {
        Assert.isTrue(fieldMeta.tableMeta() == tableMeta, () -> String.format(
                "field[%s] don'table belong to tableMeta[%s]", fieldMeta, tableMeta));

        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable"
                    , fieldMeta.tableMeta().javaType().getName(), fieldMeta.propertyName()));
        }
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())
                || TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())) {
            throw new IllegalArgumentException("version or updateTime is managed by army.");
        }
    }

    private void assertFieldAndValueExpressionMatch(FieldMeta<?, ?> fieldMeta, Expression<?> valueExp) {
        if (!fieldMeta.nullable()
                && valueExp instanceof ParamExpression
                && ((ParamExpression<?>) valueExp).value() == null) {
            throw new IllegalArgumentException(String.format("domain[%s] mapping prop[%s] non-null"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
    }


}
