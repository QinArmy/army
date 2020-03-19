package io.army.criteria.impl;

import io.army.SessionFactory;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.UpdateAble;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerUpdateAble;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class UpdateAbleImpl<T extends IDomain, C> extends AbstractSQLDebug implements UpdateAble.AliasAble<T, C>
        , UpdateAble.SetAble<T, C>, UpdateAble.WhereAble<T, C>, UpdateAble.WhereAndAble<T, C>
        , InnerUpdateAble, UpdateAble {


    protected final TableMeta<T> tableMeta;

    protected final C criteria;

    protected String tableAlias;

    protected List<FieldMeta<?, ?>> fieldList = new ArrayList<>();

    protected List<Expression<?>> valueExpList = new ArrayList<>();

    protected List<IPredicate> predicateList;

    UpdateAbleImpl(TableMeta<T> tableMeta, C criteria) {
        Assert.notNull(tableMeta, "tableMeta required");
        this.tableMeta = tableMeta;
        this.criteria = criteria;
    }

    /*################################## blow AliasAble method ##################################*/

    @Override
    public UpdateAble.SetAble<T, C> as(String tableAlias) {
        Assert.state(!StringUtils.hasText(this.tableAlias), "as clause ended");
        Assert.hasText(tableAlias, "tableAlias required");
        this.tableAlias = tableAlias;
        return this;
    }

    /*################################## blow SetAble method ##################################*/

    @Override
    public <F> UpdateAble.WhereAble<T, C> set(FieldMeta<? super T, F> target, F value) {
        return set(target, SQLS.param(value));
    }

    @Override
    public <F> UpdateAble.WhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<?> valueExp) {
        fieldList.add(target);
        valueExpList.add(valueExp);
        return this;
    }

    @Override
    public <F> UpdateAble.WhereAble<T, C> set(FieldMeta<? super T, F> target, Function<C, Expression<?>> valueExpFunction) {
        return set(target, valueExpFunction.apply(this.criteria));
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public <F> UpdateAble.WhereAble<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target, F value) {
        if (test.test(this.criteria)) {
            set(target, SQLS.param(value, target.mappingType()));
        }
        return this;
    }

    @Override
    public <F> UpdateAble.WhereAble<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target, Expression<?> valueExp) {
        if (test.test(this.criteria)) {
            set(target, valueExp);
        }
        return this;
    }

    @Override
    public <F> UpdateAble.WhereAble<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target
            , Function<C, Expression<?>> valueExpFunction) {
        if (test.test(this.criteria)) {
            set(target, valueExpFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public UpdateAble where(List<IPredicate> predicateList) {
        Assert.state(this.predicateList == null, "where clause ended");
        Assert.notEmpty(predicateList, "no where clause forbidden by army");
        this.predicateList = predicateList;
        return this;
    }

    @Override
    public UpdateAble where(Function<C, List<IPredicate>> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public UpdateAble.WhereAndAble<T, C> where(IPredicate predicate) {
        Assert.state(this.predicateList == null, "where clause ended");
        Assert.notNull(predicate, "no where clause forbidden by army");

        this.predicateList = new ArrayList<>();
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public UpdateAble.WhereAndAble<T, C> and(IPredicate predicate) {
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "no where clause forbidden by army");
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public UpdateAble.WhereAndAble<T, C> ifAnd(Predicate<C> test, IPredicate predicate) {
        if (test.test(this.criteria)) {
            and(predicate);
        }
        return this;
    }

    @Override
    public UpdateAble.WhereAndAble<T, C> ifAnd(Predicate<C> test, Function<C, IPredicate> function) {
        if (test.test(this.criteria)) {
            and(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow AbstractSQLAble method ##################################*/

    @Override
    public final String debugSQL(SQLDialect sqlDialect, Visible visible) {
        SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());
    }


    /*################################## blow InnerUpdateAble method ##################################*/

    @Override
    public void prepare() {
        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "no target field");
        Assert.state(!CollectionUtils.isEmpty(this.valueExpList), "no value expression");
        Assert.state(this.fieldList.size() == this.valueExpList.size(), "fields ifAnd value exp size not match");
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "no where clause forbidden by army");

        if (!StringUtils.hasText(this.tableAlias)) {
            this.tableAlias = "";
        }
        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.valueExpList = Collections.unmodifiableList(this.valueExpList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final List<FieldMeta<?, ?>> targetFieldList() {
        return this.fieldList;
    }

    @Override
    public final List<Expression<?>> valueExpressionList() {
        return this.valueExpList;
    }

    @Override
    public final List<IPredicate> predicateList() {
        return this.predicateList;
    }

}
