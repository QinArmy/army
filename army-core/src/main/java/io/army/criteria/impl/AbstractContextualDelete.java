package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerDelete;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractContextualDelete<C> extends AbstractSQL
        implements InnerDelete, Delete.DeleteAble, Delete.WhereAble<C>, Delete.WhereAndAble<C> {

    static final String NOT_PREPARED_MSG = "singleDelete criteria don't haven invoke asDelete() method.";

    final C criteria;

    private final CriteriaContext criteriaContext;

    private List<IPredicate> predicateList = new ArrayList<>();

    private boolean prepared;

    AbstractContextualDelete(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;

        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public DeleteAble where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public DeleteAble where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public Delete.WhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public WhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public WhereAndAble<C> and(Function<C, IPredicate> function) {
        this.predicateList.add(function.apply(this.criteria));
        return this;
    }

    @Override
    public WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow package  method ##################################*/

    @Override
    public final Delete asDelete() {
        if (this.prepared) {
            return this;
        }
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        super.asSQL();
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        doAsDelete();

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerDelete method ##################################*/

    @Override
    public List<SQLModifier> modifierList() {
        return Collections.emptyList();
    }

    @Override
    public final List<IPredicate> predicateList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.predicateList;
    }

    @Override
    public final void clear() {
        this.predicateList = null;
        doClear();
    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean prepared() {
        return this.prepared;
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
        afterOnAddSubQuery(subQuery, subQueryAlias);
    }


    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

    void doAsDelete() {

    }

    void doClear() {

    }

    void afterOnAddSubQuery(SubQuery subQuery, String subQueryAlias) {

    }

    /*################################## blow package template method ##################################*/


}
