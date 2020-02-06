package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerSingleUpdateAble;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.util.Assert;
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

    /*################################## blow protected template method ##################################*/

    /*################################## blow protected method ##################################*/

    /*################################## blow private method ##################################*/


    private List<SQLWrapper> createUpdateForChild(InnerSingleUpdateAble innerAble, Visible visible) {
        return Collections.emptyList();
    }


    private SQLWrapper createUpdateForSimple(InnerSingleUpdateAble innerAble, Visible visible) {

        StringBuilder builder = new StringBuilder();
        List<ParamWrapper> paramWrapperList = new ArrayList<>();

        //1. update clause
        String tableAlias = appendUpdateClause(builder, innerAble);
        //2. set clause
        appendSetClause(builder, paramWrapperList, tableAlias, innerAble);
        //3. where clause
        boolean hasVersion;
        hasVersion = appendWhereClause(builder, paramWrapperList, tableAlias, innerAble, visible);
        //4. order clause
        appendOrderByClause(builder, paramWrapperList, innerAble);
        //5. limit clause
        appendLimitClause(builder, paramWrapperList, innerAble.rowCount());

        return SQLWrapper.build(builder.toString(), paramWrapperList, hasVersion);
    }

    @Nullable
    private String appendUpdateClause(StringBuilder builder, InnerSingleUpdateAble innerAble) {
        TableMeta<?> tableMeta = innerAble.tableMeta();
        builder.append("UPDATE ")
                .append(quoteIfNeed(tableMeta.tableName()))
                .append(" ")
        ;
        String tableAlias = innerAble.tableAlias();

        if (StringUtils.hasText(tableAlias)) {
            builder.append("AS ")
                    .append(quoteIfNeed(tableAlias))
                    .append(" ");
        } else {
            tableAlias = null;
        }
        return tableAlias;
    }

    private void appendSetClause(StringBuilder builder, List<ParamWrapper> paramWrapperList
            , final @Nullable String tableAlias, InnerSingleUpdateAble innerAble) {
        TableMeta<?> tableMeta = innerAble.tableMeta();

        List<FieldMeta<?, ?>> fieldMetaList = innerAble.targetFieldList();
        List<Expression<?>> valueExpList = innerAble.valueExpressionList();

        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "updateAble error");
        builder.append("SET ");
        final int size = fieldMetaList.size();
        for (int i = 0; i < size; i++) {
            FieldMeta<?, ?> fieldMeta = fieldMetaList.get(i);
            assertTargetField(fieldMeta, tableMeta);

            if (tableAlias != null) {
                builder.append(tableAlias)
                        .append(".");
            }
            builder.append(quoteIfNeed(fieldMeta.fieldName()))
                    .append(" = ");
            Expression<?> valueExp = valueExpList.get(i);
            // assert not null
            assertFieldAndValueExpressionMatch(fieldMeta, valueExp);
            // append expression
            valueExp.appendSQL(this.sql, builder, paramWrapperList);
            if (i < size - 1) {
                builder.append(",");
            }

        }
        // append update_time and version field
        appendFieldsManagedByArmy(tableMeta, builder, paramWrapperList, tableAlias);
    }

    private void appendFieldsManagedByArmy(TableMeta<?> tableMeta, StringBuilder builder
            , List<ParamWrapper> paramWrapperList, final @Nullable String tableAlias) {
        //1. version field
        FieldMeta<?, ?> fieldMeta = tableMeta.getField(TableMeta.VERSION);

        builder.append(",");
        if (tableAlias != null) {
            builder.append(tableAlias)
                    .append(".");
        }
        builder.append(quoteIfNeed(fieldMeta.fieldName()))
                .append(" = ")
        ;
        // version = version + 1
        fieldMeta.add(1).appendSQL(this.sql, builder, paramWrapperList);
        //2. updateTime field
        fieldMeta = tableMeta.getField(TableMeta.UPDATE_TIME);
        builder.append(",");
        if (tableAlias != null) {
            builder.append(tableAlias)
                    .append(".");
        }
        builder.append(quoteIfNeed(fieldMeta.fieldName()))
                .append(" = ");

        if (fieldMeta.javaType() == LocalDateTime.class) {
            SQLS.param(LocalDateTime.now()).appendSQL(this.sql, builder, paramWrapperList);
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            SQLS.param(ZonedDateTime.now(this.zoneId())).appendSQL(this.sql, builder, paramWrapperList);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private boolean appendWhereClause(StringBuilder builder, List<ParamWrapper> paramWrapperList
            , final @Nullable String tableAlias, InnerSingleUpdateAble innerAble, Visible visible) {

        final List<Predicate> predicateList = innerAble.predicateList();
        Assert.notEmpty(predicateList, "where clause must be not empty");

        builder.append("WHERE ");

        Predicate predicate;
        final TableMeta<?> tableMeta = innerAble.tableMeta();

        final FieldMeta<?, ?> versionField = tableMeta.getField(TableMeta.VERSION);

        boolean hasVersion = false;
        for (Iterator<Predicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            predicate = iterator.next();
            predicate.appendSQL(this.sql, builder, paramWrapperList);
            if (iterator.hasNext()) {
                builder.append("AND ");
            }
            if (predicate instanceof DualPredicate) {
                DualPredicate dualPredicate = (DualPredicate) predicate;
                // version = expresion
                if (versionField.equals(dualPredicate.leftExpression())
                        && dualPredicate.dualOperator() == DualOperator.EQ) {
                    hasVersion = true;
                }
            }
        }
        // append visible predicate
        appendVisiblePredicate(tableMeta, builder, paramWrapperList, tableAlias, visible);
        return hasVersion;
    }

    private void appendVisiblePredicate(TableMeta<?> tableMeta, StringBuilder builder
            , List<ParamWrapper> paramWrapperList, final @Nullable String tableAlias, Visible visible) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        Assert.state(visibleField.javaType() == Boolean.class, "visible prop class type only is Boolean");
        // append visible field
        Boolean visibleValue;
        switch (visible) {
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
                throw new IllegalArgumentException(String.format("unknown visible[%s]", visible));
        }
        if (visibleValue != null) {
            builder.append("AND ");

            if (tableAlias != null) {
                builder.append(tableAlias)
                        .append(".");
            }
            visibleField.appendSQL(this.sql, builder, paramWrapperList);
            builder.append("= ? ");
            paramWrapperList.add(ParamWrapper.build(visibleField.mappingType(),visibleValue));
        }

    }

    private void appendOrderByClause(StringBuilder builder, List<ParamWrapper> paramWrapperList
            , InnerSingleUpdateAble innerAble) {
        builder.append("ORDER BY ");

        List<Expression<?>> orderExpList = innerAble.orderExpList();
        List<Boolean> ascExpList = innerAble.ascExpList();
        Assert.isTrue(orderExpList.size() == ascExpList.size(), "orderExpList size and ascExpList size not match.");

        Expression<?> orderExp;
        Boolean ascExp;
        final int size = orderExpList.size();
        for (int i = 0; i < size; i++) {
            orderExp = orderExpList.get(i);
            orderExp.appendSQL(this.sql, builder, paramWrapperList);
            ascExp = ascExpList.get(i);
            if (Boolean.TRUE.equals(ascExp)) {
                builder.append("ASC ");
            } else if (Boolean.FALSE.equals(ascExp)) {
                builder.append("DESC ");
            }
            if (i < size - 1) {
                builder.append(",");
            }
        }
    }

    private void appendLimitClause(StringBuilder builder, List<ParamWrapper> paramWrapperList, int rowCount) {
        if (rowCount > 0) {
            builder.append("LIMIT ?");
            paramWrapperList.add(ParamWrapper.build(MappingFactory.getDefaultMapping(Integer.class), rowCount));
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
