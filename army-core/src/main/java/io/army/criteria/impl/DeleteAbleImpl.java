package io.army.criteria.impl;

import io.army.SessionFactory;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerDeleteAble;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

 class DeleteAbleImpl< C> extends AbstractSQLAble
        implements DeleteAble.WhereAble< C>, DeleteAble.WhereAndAble< C>,DeleteAble.FromAble<C>,InnerDeleteAble {

    private final C criteria;

    private  TableMeta<?> tableMeta;

    private List<IPredicate> predicateList;

    DeleteAbleImpl(C criteria) {
        this.criteria = criteria;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public WhereAble<C> from(TableMeta<?> tableMeta) {
        Assert.state(this.tableMeta == null,"from clause ended");
        this.tableMeta = tableMeta;
        return this;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public DeleteAble where(List<IPredicate> predicates) {
        Assert.state(this.predicateList == null, "where clause ended.");
        Assert.notEmpty(predicates, "delete dml no where clause forbidden by army");
        this.predicateList = predicates;
        return this;
    }

    @Override
    public DeleteAble where(Function<C, List<IPredicate>> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public WhereAndAble< C> where(IPredicate predicate) {
        Assert.state(this.predicateList == null, "where clause ended.");
        Assert.notNull(predicate, "delete dml no where clause forbidden by army");
        this.predicateList = new ArrayList<>();
        this.predicateList.add(predicate);
        return this;
    }


    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public WhereAndAble< C> and(IPredicate predicate) {
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "no where clause");
        Assert.notNull(predicate, "predicate required");
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public WhereAndAble<C> and(Function<C, IPredicate> function) {
        return and(function.apply(this.criteria));
    }

    @Override
    public WhereAndAble< C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            and(predicate);
        }
        return this;
    }

    @Override
    public WhereAndAble< C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            and(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow InnerDeleteAble method ##################################*/

    @Override
    public TableMeta<?> tableMeta() {
        Assert.state(this.tableMeta != null,"field meta must not null");
        return tableMeta;
    }

    @Override
    public List<IPredicate> predicateList() {
        if (CollectionUtils.isEmpty(this.predicateList)) {
            throw new IllegalStateException("single delete dml invoke error,no where clause.");
        }
        return predicateList;
    }


    /*################################## blow AbstractSQLAble method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
        SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().delete(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());
    }


}
