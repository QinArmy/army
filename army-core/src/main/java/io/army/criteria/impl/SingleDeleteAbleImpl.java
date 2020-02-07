package io.army.criteria.impl;

import io.army.SessionFactory;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSingleDeleteAble;
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

final class SingleDeleteAbleImpl<T extends IDomain, C> extends AbstractSQLAble
        implements SingleDelete.WhereAbleOfSingleDelete<T, C>
        , SingleDelete.WhereAndOfSingleDelete<T, C>, SingleDelete.OrderAbleOfSingleDelete<T, C>
        , SingleDelete.OrderItemOfSingleDelete<T, C>, SingleDelete.LimitAbleOfSingleDelete<T, C>
        , SingleDelete.OptionalOrderOfSingleDelete<T, C>
        , InnerSingleDeleteAble {

    private final TableMeta<T> tableMeta;

    private final C criteria;

    private List<IPredicate> predicateList;

    private List<Expression<?>> orderExpList;

    private List<Boolean> ascList;

    private int rowCount = -1;

    SingleDeleteAbleImpl(TableMeta<T> tableMeta, C criteria) {
        this.tableMeta = tableMeta;
        this.criteria = criteria;
    }

    /*################################## blow WhereAbleOfSingleDelete method ##################################*/

    @Override
    public OrderAbleOfSingleDelete<T, C> where(List<IPredicate> predicates) {
        Assert.state(this.predicateList == null, "where clause ended.");
        Assert.notEmpty(predicates, "delete sql no where clause forbidden by army");
        this.predicateList = predicates;
        return this;
    }

    @Override
    public OrderAbleOfSingleDelete<T, C> where(Function<C, List<IPredicate>> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public WhereAndOfSingleDelete<T, C> where(IPredicate predicate) {
        Assert.state(this.predicateList == null, "where clause ended.");
        Assert.notNull(predicate, "delete sql no where clause forbidden by army");
        this.predicateList = new ArrayList<>();
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public WhereAndOfSingleDelete<T, C> where(Function<C, IPredicate> function, boolean one) {
        return where(function.apply(this.criteria));
    }

    /*################################## blow WhereAndOfSingleDelete method ##################################*/

    @Override
    public WhereAndOfSingleDelete<T, C> and(IPredicate predicate) {
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "no where clause");
        Assert.notNull(predicate, "predicate required");
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public WhereAndOfSingleDelete<T, C> and(Function<C, IPredicate> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public WhereAndOfSingleDelete<T, C> and(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            where(predicate);
        }
        return this;
    }

    @Override
    public WhereAndOfSingleDelete<T, C> and(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            where(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow OrderAbleOfSingleDelete method ##################################*/

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp) {
        return orderBy(orderExp, (Boolean) null);
    }

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp, @Nullable Boolean asc) {
        Assert.state(this.orderExpList == null, "order by clause ended");
        Assert.notNull(orderExp, "orderExp required");

        this.orderExpList = new ArrayList<>(4);
        this.orderExpList.add(orderExp);

        this.ascList = new ArrayList<>(this.orderExpList.size());
        this.ascList.add(asc);
        return this;
    }

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction) {
        return orderBy(orderExpFunction.apply(this.criteria));
    }

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction, @Nullable Boolean asc) {
        return orderBy(orderExpFunction.apply(this.criteria), asc);
    }

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction
            , Function<C, Boolean> ascFunction) {
        return orderBy(
                orderExpFunction.apply(this.criteria)
                , ascFunction.apply(this.criteria)
        );
    }

    @Override
    public OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp, Function<C, Boolean> ascFunction) {
        return orderBy(orderExp, ascFunction.apply(this.criteria));
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate, Expression<?> orderExp) {
        if (testPredicate.test(this.criteria)) {
            orderBy(orderExp, (Boolean) null);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate, Expression<?> orderExp
            , @Nullable Boolean asc) {
        if (testPredicate.test(this.criteria)) {
            orderBy(orderExp, asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate, Function<C, Expression<?>> function
            , @Nullable Boolean asc) {
        if (testPredicate.test(this.criteria)) {
            orderBy(function.apply(this.criteria), asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate, Function<C, Expression<?>> function
            , Function<C, Boolean> ascFunction) {
        if (testPredicate.test(this.criteria)) {
            orderBy(function.apply(this.criteria), ascFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate, Expression<?> orderExp
            , Function<C, Boolean> ascFunction) {
        if (testPredicate.test(this.criteria)) {
            orderBy(orderExp, ascFunction.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow OptionalOrderOfSingleDelete method ##################################*/

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Expression<?> orderExp) {
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            then(orderExp);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Expression<?> orderExp, @Nullable Boolean asc) {
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            then(orderExp, asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Function<C, Expression<?>> function, @Nullable Boolean asc) {
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            then(function.apply(this.criteria), asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Function<C, Expression<?>> function, Function<C, Boolean> ascFunction) {
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            then(function.apply(this.criteria), ascFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Expression<?> orderExp, Function<C, Boolean> ascFunction) {
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            then(orderExp, ascFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Predicate<C> testPredicate, Expression<?> orderExp
            , @Nullable Boolean asc) {
        if (!CollectionUtils.isEmpty(this.orderExpList)
                && testPredicate.test(this.criteria)) {
            then(orderExp, asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Predicate<C> testPredicate, Expression<?> orderExp
            , Function<C, Boolean> ascFunction) {
        if (!CollectionUtils.isEmpty(this.orderExpList)
                && testPredicate.test(this.criteria)) {
            then(orderExp, ascFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Predicate<C> testPredicate, Function<C, Expression<?>> function
            , @Nullable Boolean asc) {
        if (!CollectionUtils.isEmpty(this.orderExpList)
                && testPredicate.test(this.criteria)) {
            then(function.apply(this.criteria), asc);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Predicate<C> testPredicate, Expression<?> orderExp) {
        if (!CollectionUtils.isEmpty(this.orderExpList)
                && testPredicate.test(this.criteria)) {
            then(orderExp, (Boolean) null);
        }
        return this;
    }

    @Override
    public OptionalOrderOfSingleDelete<T, C> maybeThen(Predicate<C> testPredicate, Function<C, Expression<?>> function,
                                                       Function<C, Boolean> ascFunction) {
        if (!CollectionUtils.isEmpty(this.orderExpList)
                && testPredicate.test(this.criteria)) {
            then(function.apply(this.criteria), ascFunction.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow OrderItemOfSingleDelete method ##################################*/

    @Override
    public OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp) {
        return then(orderExp, (Boolean) null);
    }

    @Override
    public OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp, @Nullable Boolean asc) {
        Assert.state(!CollectionUtils.isEmpty(this.orderExpList), "NO order by clause");
        Assert.notNull(orderExp, "orderExp required");

        this.orderExpList.add(orderExp);
        this.ascList.add(asc);
        return this;
    }

    @Override
    public OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp, Function<C, Boolean> ascFunction) {
        return then(orderExp, ascFunction.apply(this.criteria));
    }

    @Override
    public OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Expression<?> orderExp
            , @Nullable Boolean asc) {
        if (testPredicate.test(this.criteria)) {
            then(orderExp, asc);
        }
        return this;
    }

    @Override
    public OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Expression<?> orderExp
            , Function<C, Boolean> ascFunction) {
        if (testPredicate.test(this.criteria)) {
            then(orderExp, ascFunction.apply(this.criteria));
        }
        return this;
    }

    @Override
    public OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Function<C, Expression<?>> function
            , @Nullable Boolean asc) {
        if (testPredicate.test(this.criteria)) {
            then(function.apply(this.criteria), asc);
        }
        return this;
    }

    /*################################## blow LimitAbleOfSingleDelete method ##################################*/

    @Override
    public SingleDeleteAble limit(int rowCount) {
        Assert.state(this.rowCount < 0, "limit clause ended");
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public SingleDeleteAble limit(Function<C, Integer> function) {
        return limit(function.apply(this.criteria));
    }

    @Override
    public SingleDeleteAble limit(Predicate<C> testPredicate, int rowCount) {
        if (testPredicate.test(this.criteria)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public SingleDeleteAble limit(Predicate<C> testPredicate, Function<C, Integer> function) {
        if (testPredicate.test(this.criteria)) {
            limit(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow InnerSingleDeleteAble method ##################################*/

    @Override
    public TableMeta<?> tableMeta() {
        return tableMeta;
    }

    @Override
    public List<IPredicate> predicateList() {
        if (CollectionUtils.isEmpty(this.predicateList)) {
            throw new IllegalStateException("single delete sql invoke error,no where clause.");
        }
        return predicateList;
    }

    @Override
    public List<Expression<?>> orderExpList() {
        if (this.orderExpList == null) {
            return Collections.emptyList();
        }
        return orderExpList;
    }

    @Override
    public List<Boolean> ascList() {
        if (this.ascList == null) {
            return Collections.emptyList();
        }
        return ascList;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }


    /*################################## blow AbstractSQLAble method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
        SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().delete(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());
    }


}
