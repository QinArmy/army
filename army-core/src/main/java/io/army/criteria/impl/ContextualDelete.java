package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDelete;
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
        , Delete.DomainDeleteSpec<C>, Delete.DeleteRoute<C>, Delete.WhereAndSpec<C>
        , _SingleDelete {

    static DomainDeleteSpec<Void> create() {
        return new ContextualDelete<>(null);
    }

    static <C> DomainDeleteSpec<C> create(C criteria) {
        return new ContextualDelete<>(Objects.requireNonNull(criteria));
    }


    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> table;

    private String tableAlias;

    private byte databaseIndex = -1;

    private byte tableIndex = -1;

    private List<_Predicate> predicateList = new ArrayList<>();

    private boolean prepared;

    private ContextualDelete(@Nullable C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextStack.setContextStack(criteriaContext);
    }

    /*################################## blow SingleDeleteSpec method ##################################*/


    @Override
    public DeleteRoute<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, final String tableAlias) {
        Assert.identifierHasText(tableAlias);
        this.table = tableMeta;
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public WhereSpec<C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = Assert.databaseRoute(this.table, databaseIndex);
        this.tableIndex = Assert.tableRoute(this.table, tableIndex);
        return this;
    }

    @Override
    public WhereSpec<C> route(int tableIndex) {
        this.tableIndex = Assert.tableRoute(this.table, tableIndex);
        return this;
    }

    @Override
    public WhereSpec<C> routeAll() {
        this.databaseIndex = Byte.MIN_VALUE;
        this.tableIndex = Byte.MIN_VALUE;
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

        CriteriaContextStack.clearContextStack(this.criteriaContext);

        Assert.hasTable(this.table);
        Assert.identifierHasText(this.tableAlias);
        this.predicateList = CriteriaUtils.predicateList(this.predicateList);

        this.prepared = true;
        return this;
    }

    @Override
    public void clear() {
        this.table = null;
        this.tableAlias = null;
        this.predicateList = null;

        this.databaseIndex = -1;
        this.tableIndex = -1;

        this.prepared = false;
    }

    /*################################## blow InnerStandardSingleDelete method ##################################*/

    @Override
    public TableMeta<?> table() {
        Assert.prepared(this.prepared);
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public byte databaseIndex() {
        Assert.prepared(this.prepared);
        return this.databaseIndex;
    }

    @Override
    public byte tableIndex() {
        Assert.prepared(this.prepared);
        return this.tableIndex;
    }

    @Override
    public List<_Predicate> predicateList() {
        Assert.prepared(this.prepared);
        return this.predicateList;
    }




    /*################################## blow static inner class ##################################*/


}
