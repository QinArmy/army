package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._StandardDelete;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to crate dynamic delete and sub query
 */
final class ContextualDelete<C> extends AbstractSQLDebug implements Delete
        , Delete.DomainDeleteSpec<C>, Delete.WhereSpec<C>, Delete.WhereAndSpec<C>
        , _StandardDelete {

    static DomainDeleteSpec<Void> create() {
        return new ContextualDelete<>(null);
    }

    static <C> DomainDeleteSpec<C> create(C criteria) {
        return new ContextualDelete<>(Objects.requireNonNull(criteria));
    }


    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> tableMeta;

    private String tableAlias;

    private List<_Predicate> predicateList = new ArrayList<>();

    private boolean prepared;

    private ContextualDelete(@Nullable C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(criteriaContext);
    }

    /*################################## blow SingleDeleteSpec method ##################################*/


    @Override
    public WhereSpec<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, final String tableAlias) {
        Assert.identifierHasText(tableAlias);
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        return this;
    }


    /*################################## blow SingleWhereSpec method ##################################*/

    @Override
    public DeleteSpec where(List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public DeleteSpec where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public WhereAndSpec<C> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow private method ##################################*/

    @Override
    public WhereAndSpec<C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public WhereAndSpec<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public WhereAndSpec<C> ifAnd(Function<C, IPredicate> function) {
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

    /*################################## blow DeleteSpec method ##################################*/

    @Override
    public Delete asDelete() {
        Assert.nonPrepared(this.prepared);

        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.hasTable(this.tableMeta);
        Assert.identifierHasText(this.tableAlias);
        this.predicateList = CriteriaUtils.predicateList(this.predicateList);

        this.prepared = true;
        return this;
    }

    @Override
    public void clear() {
        this.tableMeta = null;
        this.tableAlias = null;
        this.predicateList = null;
        this.prepared = false;
    }

    /*################################## blow InnerStandardSingleDelete method ##################################*/

    @Override
    public TableMeta<?> tableMeta() {
        Assert.prepared(this.prepared);
        return this.tableMeta;
    }

    @Override
    public List<_Predicate> predicateList() {
        Assert.prepared(this.prepared);
        return this.predicateList;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }


    /*################################## blow static inner class ##################################*/


}
