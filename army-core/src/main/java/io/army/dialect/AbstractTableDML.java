package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerObjectUpdateAble;
import io.army.criteria.impl.inner.InnerDeleteAble;
import io.army.criteria.impl.inner.InnerUpdateAble;
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
    public final List<SQLWrapper> update(UpdateAble updateAble, Visible visible) {
        Assert.isInstanceOf(InnerUpdateAble.class, updateAble, "");
        InnerUpdateAble innerAble = (InnerUpdateAble) updateAble;

        List<SQLWrapper> wrapperList;
        if (updateAble instanceof InnerObjectUpdateAble) {
            // create update dml for child mapping mode
            wrapperList = createObjectUpdate((InnerObjectUpdateAble) innerAble, visible);
        } else {
            wrapperList = createUpdateForSimple(innerAble, visible);
        }
        return wrapperList;
    }

    @Override
    public List<SQLWrapper> delete(DeleteAble.SingleDeleteAble singleDeleteAble, Visible visible) {
        InnerDeleteAble deleteAble = (InnerDeleteAble) singleDeleteAble;
        TableMeta<?> tableMeta = deleteAble.tableMeta();
        List<SQLWrapper> list;
        if (tableMeta.parentMeta() == null) {
            list = createDeleteForSimple(deleteAble, visible);
        } else {
            list = createDeleteForChild(deleteAble, visible);
        }
        return list;
    }

    /*################################## blow protected template method ##################################*/

    /*################################## blow protected method ##################################*/

    /*################################## blow private method ##################################*/

    private List<SQLWrapper> createDeleteForChild(InnerDeleteAble deleteAble, Visible visible) {
        return Collections.emptyList();
    }


    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createDeleteForSimple(InnerDeleteAble deleteAble, Visible visible) {

        final SQLContext context = new DefaultSQLContext(this, SQLStatement.DELETE);
        TableMeta<?> tableMeta = deleteAble.tableMeta();

        final String tableName = this.sql.quoteIfNeed(tableMeta.tableName());
        // 1. delete clause
        appendDeleteClause(context, tableName, deleteAble);
        // 2. where clause
        appendDeleteWhereClause(context, deleteAble, visible);
        return Collections.singletonList(
                SQLWrapper.build(context.stringBuilder().toString(), context.paramWrapper())
        );
    }

    private void appendDeleteClause(SQLContext context, String tableName, InnerDeleteAble deleteAble) {
        context.stringBuilder()
                .append("DELETE FROM ")
                .append(tableName);

    }

    private void appendDeleteWhereClause(SQLContext context, InnerDeleteAble deleteAble, Visible visible) {

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

    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createObjectUpdate(InnerObjectUpdateAble updateAble, Visible visible) {
        TableMeta<?> childMeta = updateAble.tableMeta(), parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        ObjectUpdateContextImpl context = new ObjectUpdateContextImpl(this, childMeta, updateAble.tableAlias());
        // 1. update clause
        appendObjectUpdateClause(context);
        // 2. set clause
        appendSetClause(context, updateAble.targetFieldList(), updateAble.valueExpressionList());
        // 3. where clause
        appendWhereClause(context, updateAble.predicateList());
        //4. append child visible
        appendVisiblePredicate(parentMeta, context, context.safeParentAlias(), visible);

        return Collections.singletonList(
                SQLWrapper.build(context.builder.toString(), context.paramWrapperList)
        );
    }

    private void appendObjectUpdateClause(ObjectUpdateContextImpl context) {
        TableMeta<?> childMeta = context.tableMeta(), parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        StringBuilder builder = context.stringBuilder()
                .append("UPDATE ")
                .append(this.sql.quoteIfNeed(childMeta.tableName()));

        if (StringUtils.hasText(context.safeAlias())) {
            builder.append(" AS ")
                    .append(context.safeAlias());
        }
        builder.append(" JOIN ")
                .append(this.sql.quoteIfNeed(parentMeta.tableName()))
                .append(" AS ")
                .append(context.safeParentAlias())
                .append(" ON ")
                .append(context.safeAlias())
                .append(".")
                .append(TableMeta.ID)
                .append(" = ")
                .append(context.safeParentAlias())
                .append(".")
                .append(TableMeta.ID)
        ;
    }

    private void appendSetClause(UpdateSQLContext context, List<FieldMeta<?, ?>> fieldMetaList
            , List<Expression<?>> valueExpList) {

        Assert.notEmpty(fieldMetaList, "set clause must not empty");
        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "field list ifAnd value exp list size not match");

        final int size = fieldMetaList.size();
        FieldMeta<?, ?> fieldMeta;
        Expression<?> valueExp;

        StringBuilder builder = context.stringBuilder()
                .append(" SET");
        for (int i = 0; i < size; i++) {
            fieldMeta = fieldMetaList.get(i);
            valueExp = valueExpList.get(i);

            context.assertField(fieldMeta);

            // fieldMeta self-describe
            fieldMeta.appendSQL(context);
            builder.append(" =");
            // expression self-describe
            valueExp.appendSQL(context);
            if (i < size - 1) {
                builder.append(",");
            }
        }
        // append version ifAnd updateTime
        appendFieldsManagedByArmy(context);

    }


    private void appendWhereClause(SQLContext context, List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "where clause must be not empty");

        StringBuilder builder = context.stringBuilder()
                .append(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            // predicate self-describe
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
        }
    }


    private List<SQLWrapper> createUpdateForSimple(InnerUpdateAble innerAble, Visible visible) {

        // build dml context
        final UpdateSQLContextImpl context = new UpdateSQLContextImpl(this, SQLStatement.UPDATE
                , innerAble.tableMeta(), innerAble.tableAlias());

        //1. update clause
        appendUpdateClause(context);
        //2. set clause
        appendSetClause(context, innerAble.targetFieldList(), innerAble.valueExpressionList());
        //3. where clause
        appendWhereClause(context, innerAble.predicateList());
        //4. append visible
        appendVisiblePredicate(context.updateTable, context, context.safeAlias(), visible);

        return Collections.singletonList(
                SQLWrapper.build(context.builder.toString(), context.paramWrapperList)
        );
    }

    private void appendUpdateClause(UpdateSQLContext context) {
        context.stringBuilder().append("UPDATE ")
                .append(this.sql.quoteIfNeed(context.tableMeta().tableName()));

        if (StringUtils.hasText(context.safeAlias())) {
            context.stringBuilder()
                    .append(" AS ")
                    .append(context.safeAlias());
        }
    }

    private void appendFieldsManagedByArmy(UpdateSQLContext context) {
        //1. version field
        FieldMeta<?, ?> fieldMeta = context.versionField();
        StringBuilder builder = context.stringBuilder();

        builder.append(",");
        fieldMeta.appendSQL(context);

        builder.append(" = ");
        fieldMeta.add(SQLS.constant(1)).appendSQL(context);

        //2. updateTime field
        fieldMeta = context.updateTimeField();
        builder.append(",");
        // updateTime field self-describe
        fieldMeta.appendSQL(context);
        builder.append(" = ");

        if (fieldMeta.javaType() == LocalDateTime.class) {
            SQLS.param(LocalDateTime.now()).appendSQL(context);
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            SQLS.param(ZonedDateTime.now(this.zoneId())).appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }


    private void appendVisiblePredicate(TableMeta<?> tableMetaWithVisible, SQLContext context
            , final String safeTableAlias, Visible visible) {
        final FieldMeta<?, ?> visibleField = tableMetaWithVisible.getField(TableMeta.VISIBLE);

        Assert.state(visibleField.javaType() == Boolean.class, "visible prop class type only is Boolean");
        // append visible field
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            StringBuilder builder = context.stringBuilder().append(" AND ");
            if (StringUtils.hasText(safeTableAlias)) {
                builder.append(safeTableAlias)
                        .append(".");
            }
            builder.append(this.sql.quoteIfNeed(visibleField.fieldName()))
                    .append(" = ? ");
            context.appendParam(ParamWrapper.build(visibleField.mappingType(), visibleValue));
        }

    }

    private void appendOrderByClause(SQLContext context, List<Expression<?>> orderExpList, List<Boolean> ascExpList) {
        if (CollectionUtils.isEmpty(orderExpList)) {
            return;
        }
        Assert.isTrue(orderExpList.size() == ascExpList.size(), "orderExpList size havingAnd ascExpList size not match.");

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
                "field[%s] don'field belong to tableMeta[%s]", fieldMeta, tableMeta));

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
