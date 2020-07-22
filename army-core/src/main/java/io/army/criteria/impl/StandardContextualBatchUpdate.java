package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

final class StandardContextualBatchUpdate<T extends IDomain, C> extends AbstractSQLDebug
        implements Update, Update.BatchUpdateAble<T, C>, Update.BatchSetAble<T, C>
        , Update.BatchWhereAble<T, C>, Update.BatchWhereAndAble<T, C>, Update.BatchNamedParamAble<C>
        , Update.UpdateAble, InnerBatchUpdate, InnerStandardUpdate {

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

    private List<Object> namedParamList = new ArrayList<>();

    private boolean prepared;

    private StandardContextualBatchUpdate(TableMeta<T> tableMeta, C criteria) {
        this.tableMeta = tableMeta;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchUpdateAble method ##################################*/

    @Override
    public BatchSetAble<T, C> update(TableMeta<T> tableMeta, String tableAlias) {
        Assert.isTrue(tableMeta == this.tableMeta, "tableMeta not match");
        this.tableAlias = tableAlias;
        Assert.hasText(this.tableAlias, "tableMeta required");
        return this;
    }

    /*################################## blow BatchSetAble method ##################################*/

    @Override
    public <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.param(value, target));
        return this;
    }

    @Override
    public <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, Function<C, Expression<F>> function) {
        this.targetFieldList.add(target);
        this.valueExpList.add(function.apply(this.criteria));
        return this;
    }

    @Override
    public <F> BatchWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value) {
        if (predicate.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public <F> BatchWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
            , Expression<F> valueExp) {
        if (predicate.test(this.criteria)) {
            set(target, valueExp);
        }
        return this;
    }

    @Override
    public <F> BatchWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
            , Function<C, Expression<F>> valueExpFunction) {
        if (predicate.test(this.criteria)) {
            set(target, valueExpFunction);
        }
        return this;
    }

    /*################################## blow BatchWhereAble method ##################################*/


    @Override
    public BatchNamedParamAble<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public BatchWhereAndAble<T, C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow BatchWhereAndAble method ##################################*/

    @Override
    public BatchWhereAndAble<T, C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public BatchWhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow BatchNamedParamAble method ##################################*/

    @Override
    public UpdateAble namedParamMaps(Collection<Map<String, Object>> mapCollection) {
        this.namedParamList.addAll(mapCollection);
        return this;
    }

    @Override
    public UpdateAble namedParamMaps(Function<C, Collection<Map<String, Object>>> function) {
        this.namedParamList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public UpdateAble namedParamBeans(Collection<Object> beanCollection) {
        this.namedParamList.addAll(beanCollection);
        return this;
    }

    @Override
    public UpdateAble namedParamBeans(Function<C, Collection<Object>> function) {
        this.namedParamList.addAll(function.apply(this.criteria));
        return this;
    }

    /*################################## blow update method ##################################*/

    @Override
    public boolean prepared() {
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
    public Collection<Object> namedParamList() {
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
