package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.Delete;
import io.army.criteria.Dml;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._BatchDelete;
import io.army.criteria.impl.inner._Predicate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard batch domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to create dynamic delete and sub query
 */
final class ContextualBatchDelete<C> implements Delete, Dml.BatchDeleteWhereSpec<C>
        , Dml.BatchWhereAndSpec<C, Delete>, Dml.BatchParamSpec<C, Delete>, Dml.DmlSpec<Delete>, _BatchDelete {

    static Delete.BatchDomainDeleteSpec<Void> create() {
        return new BatchDomainDeleteSpecImpl<>(null);
    }

    static <C> Delete.BatchDomainDeleteSpec<C> create(C criteria) {
        Objects.requireNonNull(criteria);
        return new BatchDomainDeleteSpecImpl<>(criteria);
    }

    private final TableMeta<?> table;

    private final String tableAlias;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<ReadWrapper> paramList = new ArrayList<>();

    private boolean prepared;

    private ContextualBatchDelete(TableMeta<?> table, String tableAlias, @Nullable C criteria) {
        this.table = table;
        this.tableAlias = tableAlias;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    /*################################## blow BatchSingleDeleteSpec method ##################################*/


    /*################################## blow BatchWhereSpec method ##################################*/

    @Override
    public BatchParamSpec<C, Delete> where(List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchParamSpec<C, Delete> where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public BatchParamSpec<C, Delete> where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public BatchWhereAndSpec<C, Delete> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow BatchWhereAndSpec method ##################################*/

    @Override
    public BatchWhereAndSpec<C, Delete> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public BatchWhereAndSpec<C, Delete> and(Function<C, IPredicate> function) {
        return this.and(Objects.requireNonNull(function.apply(this.criteria)));
    }

    @Override
    public BatchWhereAndSpec<C, Delete> and(Supplier<IPredicate> supplier) {
        return this.and(Objects.requireNonNull(supplier.get()));
    }

    @Override
    public BatchWhereAndSpec<C, Delete> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndSpec<C, Delete> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndSpec<C, Delete> ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow BatchNamedParamSpec method ##################################*/

    @Override
    public DmlSpec<Delete> paramMaps(List<Map<String, Object>> mapList) {
        final List<ReadWrapper> paramList = this.paramList;
        for (Map<String, Object> map : mapList) {
            paramList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public DmlSpec<Delete> paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public DmlSpec<Delete> paramMaps(Supplier<List<Map<String, Object>>> supplier) {
        return this.paramMaps(supplier.get());
    }

    @Override
    public DmlSpec<Delete> paramBeans(List<Object> beanList) {
        final List<ReadWrapper> paramList = this.paramList;
        for (Object bean : beanList) {
            paramList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public DmlSpec<Delete> paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }

    @Override
    public DmlSpec<Delete> paramBeans(Supplier<List<Object>> supplier) {
        return this.paramBeans(supplier.get());
    }

    /*################################## blow DeleteSpec method ##################################*/

    @Override
    public Delete asDml() {
        _Assert.nonPrepared(this.prepared);
        CriteriaContextStack.clearContextStack(this.criteriaContext);

        _Assert.hasTable(this.table);
        _Assert.identifierHasText(this.tableAlias);

        this.predicateList = CriteriaUtils.predicateList(this.predicateList);
        this.paramList = CriteriaUtils.namedParamList(this.paramList);
        this.prepared = true;
        return this;
    }

    /*################################## blow Delete method ##################################*/

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }

    /*################################## blow InnerStandardBatchDelete method ##################################*/

    @Override
    public List<ReadWrapper> wrapperList() {
        _Assert.prepared(this.prepared);
        return this.paramList;
    }

    @Override
    public TableMeta<?> table() {
        _Assert.prepared(this.prepared);
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public List<_Predicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.predicateList = null;
        this.paramList = null;

        this.prepared = false;
    }


    private static final class BatchDomainDeleteSpecImpl<C> implements BatchDomainDeleteSpec<C> {

        private final C criteria;

        private BatchDomainDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public BatchDeleteWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
            return new ContextualBatchDelete<>(table, tableAlias, this.criteria);
        }

    }


}
