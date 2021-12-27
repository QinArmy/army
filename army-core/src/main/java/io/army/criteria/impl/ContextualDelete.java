package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.Dml;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to crate dynamic delete and sub query
 */
final class ContextualDelete<C> extends AbstractSQLDebug implements Delete
        , Dml.DeleteWhereSpec<C>, Dml.WhereAndSpec<C, Delete>, Dml.DmlSpec<Delete>, _SingleDelete {

    static DomainDeleteSpec<Void> create() {
        return new DomainDeleteSpecImpl<>(null);
    }

    static <C> DomainDeleteSpec<C> create(final C criteria) {
        Objects.requireNonNull(criteria);
        return new DomainDeleteSpecImpl<>(criteria);
    }

    private final TableMeta<?> table;

    private final String tableAlias;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private List<_Predicate> predicateList = new ArrayList<>();

    private boolean prepared;

    private ContextualDelete(TableMeta<?> table, String tableAlias, @Nullable C criteria) {
        this.table = table;
        this.tableAlias = tableAlias;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextStack.setContextStack(criteriaContext);
    }

    /*################################## blow SingleDeleteSpec method ##################################*/



    /*################################## blow SingleWhereSpec method ##################################*/

    @Override
    public DmlSpec<Delete> where(List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public DmlSpec<Delete> where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public DmlSpec<Delete> where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public WhereAndSpec<C, Delete> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow private method ##################################*/

    @Override
    public WhereAndSpec<C, Delete> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public WhereAndSpec<C, Delete> and(Function<C, IPredicate> function) {
        return this.and(Objects.requireNonNull(function.apply(this.criteria)));
    }

    @Override
    public WhereAndSpec<C, Delete> and(Supplier<IPredicate> supplier) {
        return this.and(Objects.requireNonNull(supplier.get()));
    }

    @Override
    public WhereAndSpec<C, Delete> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public WhereAndSpec<C, Delete> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public WhereAndSpec<C, Delete> ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }

    /*################################## blow DeleteSpec method ##################################*/

    @Override
    public Delete asDml() {
        _Assert.nonPrepared(this.prepared);

        CriteriaContextStack.clearContextStack(this.criteriaContext);

        _Assert.hasTable(this.table);
        _Assert.identifierHasText(this.tableAlias);
        this.predicateList = CriteriaUtils.predicateList(this.predicateList);

        this.prepared = true;
        return this;
    }

    @Override
    public void clear() {
        this.predicateList = null;
        this.prepared = false;
    }

    /*################################## blow InnerStandardSingleDelete method ##################################*/

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
        _Assert.prepared(this.prepared);
        return this.predicateList;
    }


    /*################################## blow static inner class ##################################*/


    private static final class DomainDeleteSpecImpl<C> implements DomainDeleteSpec<C> {

        private final C criteria;

        private DomainDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public DeleteWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
            return new ContextualDelete<>(table, tableAlias, this.criteria);
        }

    }


}
