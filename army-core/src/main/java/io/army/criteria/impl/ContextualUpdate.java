package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._StandardUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>
 * This class representing standard single domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class ContextualUpdate<T extends IDomain, C> extends AbstractSQLDebug implements
        Update, Update.UpdateSpec, Update.WhereSpec<T, C>, Update.SetSpec<T, C>
        , Update.WhereAndSpec<T, C>, _StandardUpdate {

    static DomainUpdateSpec<Void> create() {
        return new DomainUpdateSpecImpl<>(null);
    }

    static <C> DomainUpdateSpec<C> create(C criteria) {
        return new DomainUpdateSpecImpl<>(Objects.requireNonNull(criteria));
    }

    private final TableMeta<T> table;

    private final String tableAlias;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private List<FieldMeta<?, ?>> fieldList = new ArrayList<>();

    private List<_Expression<?>> valueExpList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private boolean prepared;

    private ContextualUpdate(TableMeta<T> table, String tableAlias, @Nullable C criteria) {
        Assert.hasText(tableAlias, "tableAlias required");
        this.table = table;
        this.criteria = criteria;
        this.tableAlias = tableAlias;
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }


    /*################################## blow DomainSetAble method ##################################*/

    @Override
    public <F> WhereSpec<T, C> set(FieldMeta<? super T, F> target, @Nullable F value) {
        return this.set(target, SQLs.param(target, value));
    }

    @Override
    public <F> WhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp) {
        this.fieldList.add(target);
        this.valueExpList.add((_Expression<?>) valueExp);
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> setDefault(FieldMeta<? super T, F> target) {
        return this.set(target, SQLs.defaultKeyWord());
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, @Nullable F value) {
        if (value != null) {
            this.set(target, SQLs.param(target, value));
        }
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value) {
        if (predicate.test(this.criteria)) {
            this.set(target, SQLs.param(target, value));
        }
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> target
            , Function<C, Expression<F>> function) {
        final Expression<F> expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(target, expression);
        }
        return this;
    }

    /*################################## blow DomainWhereAble method ##################################*/


    @Override
    public UpdateSpec where(List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public UpdateSpec where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public WhereAndSpec<T, C> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow WhereAndSpec method ##################################*/

    @Override
    public WhereAndSpec<T, C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public WhereAndSpec<T, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public WhereAndSpec<T, C> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

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
        final List<_Predicate> predicateList = this.predicateList;
        switch (predicateList.size()) {
            case 0:
                throw new IllegalStateException("update no where clause.");
            case 1:
                this.predicateList = Collections.singletonList(predicateList.get(0));
                break;
            default:
                this.predicateList = Collections.unmodifiableList(predicateList);
        }
        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardDomainUpdate method ##################################*/

    @Override
    public TableMeta<?> tableMeta() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public List<_Predicate> predicateList() {
        Assert.prepared(this.prepared);
        return this.predicateList;
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
    public void clear() {
        this.fieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.prepared = false;
    }


    private static final class DomainUpdateSpecImpl<C> implements DomainUpdateSpec<C> {

        private final C criteria;

        private DomainUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T extends IDomain> SetSpec<T, C> update(TableMeta<T> table, String tableAlias) {
            return new ContextualUpdate<>(table, tableAlias, this.criteria);
        }

    }


}

