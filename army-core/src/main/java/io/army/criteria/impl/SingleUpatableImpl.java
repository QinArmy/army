package io.army.criteria.impl;

import io.army.boot.SessionFactoryBuilder;
import io.army.criteria.*;
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

final class SingleUpatableImpl<T extends IDomain> implements SingleSetAble<T>, WhereAbleOfSingleUpdate<T>
        , OrderAbleOfSingleUpdate<T>, OrderAscAbleOfSingleUpdate<T>, LimitAbleOfSingleUpdate, InnerSingleSetAble<T> {

    private final TableMeta<T> tableMeta;

    private final List<FieldMeta<T, ?>> targetFieldList = new ArrayList<>();

    private final List<Expression<?>> valueExpressionList = new ArrayList<>();

    private List<Predicate> predicateList;

    private FieldMeta<T, ?> orderField;

    private Boolean asc;

    private int rowCount = -1;


    SingleUpatableImpl(TableMeta<T> tableMeta) {
        this.tableMeta = tableMeta;
    }

    /*################################## blow SingleSetAble method ##################################*/

    @Override
    public <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T, F> targetField, Expression<F> expression) {
        Assert.state(this.predicateList == null, "set clause ended.");

        targetFieldList.add(targetField);
        valueExpressionList.add(expression);
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T, F> targetField, @Nullable F newValue) {
        Assert.state(this.predicateList == null, "set clause ended.");
        targetFieldList.add(targetField);
        valueExpressionList.add(SQLS.constant(newValue));
        return this;
    }

    /*################################## blow WhereAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderAbleOfSingleUpdate<T> where(List<Predicate> predicateList) {
        Assert.state(this.predicateList == null, "where clause ended.");
        Assert.notEmpty(predicateList, "no where forbade by army ");
        this.predicateList = Collections.unmodifiableList(predicateList);
        return this;
    }

    /*################################## blow OrderAbleOfSingleUpdate method ##################################*/

    @Override
    public <F> OrderAscAbleOfSingleUpdate<T> orderBy(FieldMeta<T, F> orderField) {
        Assert.state(this.orderField == null, "order by clause ended.");
        this.orderField = orderField;
        return this;
    }

    @Override
    public LimitAbleOfSingleUpdate asc() {
        Assert.state(this.asc == null, "asc clause ended.");
        this.asc = Boolean.TRUE;
        return this;
    }

    @Override
    public LimitAbleOfSingleUpdate desc() {
        Assert.state(this.asc == null, "desc clause ended.");
        this.asc = Boolean.FALSE;
        return this;
    }


    /*################################## blow LimitAbleOfSingleUpdate method ##################################*/

    @Override
    public Updatable limit(int rowCount) {
        Assert.state(this.rowCount < 0, "order by clause ended.");
        this.rowCount = rowCount;
        return this;
    }

    /*################################## blow io.army.criteria.impl.InnerSingleSetAble method ##################################*/

    @Override
    public TableMeta<T> tableMeta() {
        assertUpdateStatement();
        return tableMeta;
    }

    @Override
    public List<FieldMeta<T, ?>> targetFieldList() {
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
    public FieldMeta<T, ?> orderField() {
        return orderField;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    /*################################## blow private method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect) {
        List<SQLWrapper> sqlWrapperList = SessionFactoryBuilder.mockBuilder()
                .catalog(tableMeta.schema().catalog())
                .schema(tableMeta.schema().schema())
                .sqlDialect(sqlDialect)
                .build()
                .dialect()
                .update(this);

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
