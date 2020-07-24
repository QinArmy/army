package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.InnerStandardBatchUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

final class StandardContextualBatchUpdate<T extends IDomain, C> extends AbstractSQLDebug
        implements Update, Update.BatchUpdateAble<T, C>, Update.BatchSetAble<T, C>
        , Update.BatchWhereAble<T, C>, Update.BatchTableRouteAble<T, C>, Update.BatchWhereAndAble<T, C>
        , Update.BatchNamedParamAble<C>, Update.UpdateAble, InnerStandardBatchUpdate {

    static <T extends IDomain, C> StandardContextualBatchUpdate<T, C> build(TableMeta<T> tableMeta, C criteria) {
        Assert.isTrue(!tableMeta.immutable(), () -> String.format("TableMeta[%s] immutable", tableMeta));
        return new StandardContextualBatchUpdate<>(tableMeta, criteria);
    }

    private final TableMeta<T> tableMeta;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private String tableAlias;

    private List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private List<Expression<?>> valueExpList = new ArrayList<>();

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<ReadonlyWrapper> namedParamList = new ArrayList<>();

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualBatchUpdate(TableMeta<T> tableMeta, C criteria) {
        this.tableMeta = tableMeta;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchUpdateAble method ##################################*/

    @Override
    public final BatchTableRouteAble<T, C> update(TableMeta<T> tableMeta, String tableAlias) {
        Assert.isTrue(tableMeta == this.tableMeta, "tableMeta not match");
        this.tableAlias = tableAlias;
        Assert.hasText(this.tableAlias, "tableMeta required");
        return this;
    }

    @Override
    public final BatchSetAble<T, C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = databaseIndex;
        this.tableIndex = tableIndex;
        return this;
    }

    @Override
    public final BatchSetAble<T, C> route(int tableIndex) {
        this.tableIndex = tableIndex;
        return this;
    }

    /*################################## blow BatchSetAble method ##################################*/

    @Override
    public final <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.param(value, target));
        return this;
    }

    @Override
    public final <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public final <F> BatchWhereAble<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target, F value) {
        if (test.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public final <F> BatchWhereAble<T, C> ifSet(FieldMeta<? super T, F> target
            , Function<C, Expression<F>> function) {
        Expression<F> expression = function.apply(this.criteria);
        if (expression != null) {
            set(target, expression);
        }
        return this;
    }

    /*################################## blow BatchWhereAble method ##################################*/


    @Override
    public final BatchNamedParamAble<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final BatchWhereAndAble<T, C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow BatchWhereAndAble method ##################################*/

    @Override
    public final BatchWhereAndAble<T, C> and(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final BatchWhereAndAble<T, C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    /*################################## blow BatchNamedParamAble method ##################################*/

    @Override
    public final UpdateAble namedParamMaps(List<Map<String, Object>> mapList) {
        List<ReadonlyWrapper> namedParamList = this.namedParamList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public final UpdateAble namedParamMaps(Function<C, List<Map<String, Object>>> function) {
        return namedParamMaps(function.apply(this.criteria));
    }

    @Override
    public final UpdateAble namedParamBeans(List<Object> beanList) {
        List<ReadonlyWrapper> namedParamList = this.namedParamList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public final UpdateAble namedParamBeans(Function<C, List<Object>> function) {
        return namedParamBeans(function.apply(this.criteria));
    }

    /*################################## blow update method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }

    /*################################## blow UpdateAble method ##################################*/

    @Override
    public final Update asUpdate() {
        if (this.prepared) {
            return this;
        }
        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.state(!this.targetFieldList.isEmpty(), "update no set clause.");
        Assert.state(this.valueExpList.size() == this.targetFieldList.size()
                , "update target field and value exp size not match");
        Assert.state(!this.predicateList.isEmpty(), "update no where clause.");
        Assert.state(this.tableAlias != null, "no tableAlias");

        Assert.state(!this.namedParamList.isEmpty(), "batch update no named params");

        this.targetFieldList = Collections.unmodifiableList(this.targetFieldList);
        this.valueExpList = Collections.unmodifiableList(this.valueExpList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);
        this.namedParamList = Collections.unmodifiableList(this.namedParamList);

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardBatchUpdate method ##################################*/

    @Override
    public final List<ReadonlyWrapper> namedParamList() {
        return this.namedParamList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final int databaseIndex() {
        return this.databaseIndex;
    }

    @Override
    public final int tableIndex() {
        return this.tableIndex;
    }

    @Override
    public List<FieldMeta<?, ?>> targetFieldList() {
        return this.targetFieldList;
    }

    @Override
    public List<Expression<?>> valueExpList() {
        return this.valueExpList;
    }

    @Override
    public List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.targetFieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.namedParamList = null;
        this.prepared = false;
    }
}
