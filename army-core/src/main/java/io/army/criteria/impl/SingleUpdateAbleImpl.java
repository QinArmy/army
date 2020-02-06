package io.army.criteria.impl;

import io.army.boot.SessionFactoryBuilder;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSingleUpdateAble;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SingleUpdateAbleImpl<T extends IDomain> implements SetAbleOfSingleUpdate<T>, WhereAbleOfSingleUpdate<T>
        , OrderAbleOfSingleUpdate<T>, OrderItemAbleOfSingleUpdate<T>, LimitAbleOfSingleUpdate, InnerSingleUpdateAble {

    private final TableMeta<T> tableMeta;

    private final List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private final List<Expression<?>> valueExpressionList = new ArrayList<>();

    private List<Predicate> predicateList;

    private List<Expression<?>> orderExpList = new ArrayList<>(4);

    private List<Boolean> ascExpList = new ArrayList<>(orderExpList.size());

    private int rowCount = -1;


    SingleUpdateAbleImpl(TableMeta<T> tableMeta) {
        this.tableMeta = tableMeta;
    }

    /*################################## blow SingleSetAble method ##################################*/

    @Override
    public <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T, F> targetField, Expression<F> expression) {
        Assert.state(CollectionUtils.isEmpty(predicateList), "set clause ended.");

        targetFieldList.add(targetField);
        valueExpressionList.add(expression);
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T, F> targetField, @Nullable F newValue) {
        if (newValue == null) {
            set(targetField, SQLS.asNull(targetField.mappingType()));
        } else {
            set(targetField, SQLS.param(newValue, targetField.mappingType()));
        }
        return this;
    }

    /*################################## blow WhereAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderAbleOfSingleUpdate<T> where(List<Predicate> predicateList) {
        Assert.state(CollectionUtils.isEmpty(this.predicateList), "where clause ended.");
        Assert.notEmpty(predicateList, "no where clause forbade by army ");
        this.predicateList = Collections.unmodifiableList(predicateList);
        return this;
    }

    /*################################## blow OrderAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderItemAbleOfSingleUpdate<T> orderBy(Expression<?> orderExp) {
        return orderBy(orderExp, null);
    }

    /*################################## blow OrderAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderItemAbleOfSingleUpdate<T> orderBy(Expression<?> orderExp,@Nullable Boolean asc) {
        Assert.state(CollectionUtils.isEmpty(orderExpList), "order by clause ended.");
        Assert.state(CollectionUtils.isEmpty(ascExpList), "order by clause ended.");

        orderExpList.add(orderExp);
        ascExpList.add(asc);
        return this;
    }

    /*################################## blow OrderItemAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderItemAbleOfSingleUpdate<T> then(Expression<?> orderExp) {
        return then(orderExp,null);
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T> then(Expression<?> orderExp,@Nullable Boolean asc) {
        Assert.state(!CollectionUtils.isEmpty(orderExpList), "no order by clause.");
        Assert.state(!CollectionUtils.isEmpty(ascExpList), "order by clause ended.");

        orderExpList.add(orderExp);
        ascExpList.add(asc);
        return this;
    }

    /*################################## blow LimitAbleOfSingleUpdate method ##################################*/

    @Override
    public SingleUpdateAble limit(int rowCount) {
        Assert.state(this.rowCount < 0, "order by clause ended.");
        this.rowCount = rowCount;
        return this;
    }

    /*################################## blow io.army.criteria.impl.inner.InnerSingleSetAble method ##################################*/

    @Override
    public TableMeta<?> tableMeta() {
        assertUpdateStatement();
        return tableMeta;
    }

    @Override
    public List<FieldMeta<?, ?>> targetFieldList() {
        assertUpdateStatement();
        return targetFieldList;
    }

    @Override
    public List<Expression<?>> valueExpressionList() {
        return valueExpressionList;
    }

    @Override
    public List<Predicate> predicateList() {
        return predicateList;
    }

    @Override
    public List<Expression<?>> orderExpList() {
        return orderExpList;
    }

    @Override
    public List<Boolean> ascExpList() {
        return ascExpList;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    /*################################## blow io.army.criteria.SQLBuilder method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect) {
        return debugSQL(sqlDialect, Visible.ONLY_VISIBLE);
    }

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
        List<SQLWrapper> sqlWrapperList = SessionFactoryBuilder.mockBuilder()
                .catalog(tableMeta.schema().catalog())
                .schema(tableMeta.schema().schema())
                .sqlDialect(sqlDialect)
                .build()
                .dialect()
                .update(this, visible);

        StringBuilder builder = new StringBuilder();
        for (SQLWrapper wrapper : sqlWrapperList) {
            builder.append(wrapper);
        }
        return builder.toString();
    }

    /*################################## blow private method ##################################*/

    private void assertUpdateStatement() {
        if (!CollectionUtils.isEmpty(targetFieldList)
                && !CollectionUtils.isEmpty(predicateList)) {
            throw new IllegalStateException("no targetField or no predicate");
        }
    }
}
