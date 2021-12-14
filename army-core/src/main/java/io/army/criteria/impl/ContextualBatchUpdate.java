package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._StandardBatchUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>
 * This class representing standard batch domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class ContextualBatchUpdate<T extends IDomain, C> extends AbstractSQLDebug
        implements Update, Update.BatchSetSpec<T, C>, Update.BatchWhereSpec<T, C>
        , Update.BatchWhereAndSpec<C>, Update.BatchParamSpec<C>, Update.UpdateSpec
        , _StandardBatchUpdate {

    static BatchUpdateSpec<Void> create() {
        return new BatchUpdateSpecImpl<>(null);
    }

    static <C> BatchUpdateSpec<C> create(C criteria) {
        return new BatchUpdateSpecImpl<>(Objects.requireNonNull(criteria));
    }

    private final TableMeta<T> tableMeta;

    private final String tableAlias;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private List<FieldMeta<?, ?>> fieldList = new ArrayList<>();

    private List<_Expression<?>> valueExpList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<ReadonlyWrapper> paramList = new ArrayList<>();

    private boolean prepared;

    private ContextualBatchUpdate(TableMeta<T> tableMeta, String tableAlias, @Nullable C criteria) {
        Assert.hasText(tableAlias, "tableAlias required");
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchUpdateSpec method ##################################*/

    /*################################## blow BatchSetSpec method ##################################*/

    @Override
    public <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp) {
        if (field.updateMode() == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        }
        this.fieldList.add(field);
        this.valueExpList.add((_Expression<?>) valueExp);
        return this;
    }

    @Override
    public <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field) {
        return this.set(field, SQLs.namedParam(field));
    }

    @Override
    public <F> BatchWhereSpec<T, C> setDefault(FieldMeta<? super T, F> field) {
        return this.set(field, SQLs.defaultValue());
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setPlus(FieldMeta<? super T, F> field) {
        return this.set(field, field.plus(SQLs.namedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMinus(FieldMeta<? super T, F> field) {
        return this.set(field, field.minus(SQLs.namedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field) {
        return this.set(field, field.multiply(SQLs.namedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setDivide(FieldMeta<? super T, F> field) {
        return this.set(field, field.divide(SQLs.namedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMod(FieldMeta<? super T, F> field) {
        return this.set(field, field.mod(SQLs.namedParam(field)));
    }

    @Override
    public <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field) {
        if (test.test(this.criteria)) {
            this.set(field, SQLs.namedParam(field));
        }
        return this;
    }

    @Override
    public <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function) {
        final Expression<F> expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(filed, expression);
        }
        return this;
    }

    /*################################## blow BatchWhereSpec method ##################################*/


    @Override
    public BatchParamSpec<C> where(List<IPredicate> predicates) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicates) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchParamSpec<C> where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public BatchWhereAndSpec<C> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow BatchWhereAndSpec method ##################################*/

    @Override
    public BatchWhereAndSpec<C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow BatchNamedParamSpec method ##################################*/

    @Override
    public UpdateSpec paramMaps(final List<Map<String, Object>> mapList) {
        final List<ReadonlyWrapper> namedParamList = this.paramList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public UpdateSpec paramBeans(final List<Object> beanList) {
        List<ReadonlyWrapper> namedParamList = this.paramList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public UpdateSpec paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }

    /*################################## blow update method ##################################*/

    @Override
    public void prepared() {
        Assert.prepared(this.prepared);
    }

    /*################################## blow UpdateSpec method ##################################*/

    @Override
    public Update asUpdate() {
        Assert.nonPrepared(this.prepared);
        CriteriaContextHolder.clearContext(this.criteriaContext);

        final List<FieldMeta<?, ?>> fieldList = this.fieldList;
        final List<_Expression<?>> valueExpList = this.valueExpList;

        if (fieldList.size() != valueExpList.size()) {
            throw _Exceptions.updateFieldExpNotMatch();
        }
        switch (fieldList.size()) {
            case 0:
                throw _Exceptions.noUpdateField(this);
            case 1: {
                this.fieldList = Collections.singletonList(fieldList.get(0));
                this.valueExpList = Collections.singletonList(valueExpList.get(0));
            }
            break;
            default: {
                this.fieldList = Collections.unmodifiableList(fieldList);
                this.valueExpList = Collections.unmodifiableList(valueExpList);
            }
        }

        this.predicateList = CriteriaUtils.predicateList(this.predicateList);
        this.paramList = CriteriaUtils.namedParamList(this.paramList);

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardBatchUpdate method ##################################*/

    @Override
    public List<ReadonlyWrapper> wrapperList() {
        Assert.prepared(this.prepared);
        return this.paramList;
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
        return this.fieldList;
    }

    @Override
    public List<_Expression<?>> valueExpList() {
        return this.valueExpList;
    }

    @Override
    public List<_Predicate> predicateList() {
        Assert.prepared(this.prepared);
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.paramList = null;
        this.prepared = false;
    }


    private static final class BatchUpdateSpecImpl<C> implements BatchUpdateSpec<C> {

        private final C criteria;

        private BatchUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T extends IDomain> BatchSetSpec<T, C> update(TableMeta<T> table, String tableAlias) {
            return new ContextualBatchUpdate<>(table, tableAlias, this.criteria);
        }

    }


}
