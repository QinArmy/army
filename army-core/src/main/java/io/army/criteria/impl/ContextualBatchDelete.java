package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._StandardBatchDelete;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * This class representing standard batch domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to create dynamic delete and sub query
 */
final class ContextualBatchDelete<C> implements Delete,
        Delete.BatchDomainDeleteSpec<C>, Delete.BatchWhereSpec<C>
        , Delete.BatchWhereAndSpec<C>, Delete.BatchParamSpec<C>, Delete.DeleteSpec, _StandardBatchDelete {

    static ContextualBatchDelete<Void> create() {
        return new ContextualBatchDelete<>(null);
    }

    static <C> ContextualBatchDelete<C> create(C criteria) {
        return new ContextualBatchDelete<>(Objects.requireNonNull(criteria));
    }

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> table;

    private String tableAlias;

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<ReadonlyWrapper> paramList = new ArrayList<>();

    private boolean prepared;

    private ContextualBatchDelete(@Nullable C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchSingleDeleteSpec method ##################################*/

    @Override
    public BatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
        this.table = table;
        this.tableAlias = tableAlias;
        return this;
    }


    /*################################## blow BatchWhereSpec method ##################################*/

    @Override
    public BatchParamSpec<C> where(List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
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
    public DeleteSpec paramMaps(List<Map<String, Object>> mapList) {
        final List<ReadonlyWrapper> paramList = this.paramList;
        for (Map<String, Object> map : mapList) {
            paramList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public DeleteSpec paramBeans(List<Object> beanList) {
        final List<ReadonlyWrapper> paramList = this.paramList;
        for (Object bean : beanList) {
            paramList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public DeleteSpec paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }

    /*################################## blow DeleteSpec method ##################################*/

    @Override
    public Delete asDelete() {
        Assert.nonPrepared(this.prepared);
        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.hasTable(this.table);
        Assert.identifierHasText(this.tableAlias);

        this.predicateList = CriteriaUtils.predicateList(this.predicateList);
        this.paramList = CriteriaUtils.namedParamList(this.paramList);
        this.prepared = true;
        return this;
    }

    /*################################## blow Delete method ##################################*/

    @Override
    public void prepared() {
        Assert.prepared(this.prepared);
    }

    /*################################## blow InnerStandardBatchDelete method ##################################*/

    @Override
    public List<ReadonlyWrapper> wrapperList() {
        Assert.prepared(this.prepared);
        return this.paramList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        Assert.prepared(this.prepared);
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
        this.table = null;
        this.tableAlias = null;
        this.predicateList = null;
        this.paramList = null;

        this.prepared = false;
    }


}
