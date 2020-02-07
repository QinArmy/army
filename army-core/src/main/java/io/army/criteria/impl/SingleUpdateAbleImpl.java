package io.army.criteria.impl;

import io.army.SessionFactory;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSingleUpdateAble;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

final class SingleUpdateAbleImpl<T extends IDomain, C1, C2> extends AbstractSQLAble implements
        SetAbleOfSingleUpdate<T, C1, C2>, WhereAbleOfSingleUpdate<T, C1, C2>
        , WhereAndAbleOfSingleUpdate<T, C1, C2>, OrderItemAbleOfSingleUpdate<T, C1, C2>, InnerSingleUpdateAble
        , AliasAbleOfSingleUpdate<T, C1, C2> {

    private final TableMeta<T> tableMeta;

    private final C1 criteria1;

    private final C2 criteria2;

    private final List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private final List<Expression<?>> valueExpressionList = new ArrayList<>();

    private String tableAlias = "";

    private List<IPredicate> predicateList;

    private List<Expression<?>> orderExpList = new ArrayList<>(4);

    private List<Boolean> ascExpList = new ArrayList<>(orderExpList.size());

    private int rowCount = -1;

    SingleUpdateAbleImpl(TableMeta<T> tableMeta, C1 criteria1, C2 criteria2) {
        this.tableMeta = tableMeta;
        this.criteria1 = criteria1;
        this.criteria2 = criteria2;
    }


    /*################################## blow AliasAbleOfSingleUpdate method ##################################*/

    @Override
    public SetAbleOfSingleUpdate<T, C1, C2> as(String tableAlias) {
        Assert.state(!StringUtils.hasText(this.tableAlias), " as clause ended.");
        Assert.hasText(tableAlias, "tableAlias required");
        this.tableAlias = tableAlias;
        return this;
    }


    /*################################## blow SingleSetAble method ##################################*/
    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Expression<F> expression) {
        Assert.state(CollectionUtils.isEmpty(this.predicateList), "set clause ended.");

        this.targetFieldList.add(targetField);
        this.valueExpressionList.add(expression);
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Function<C1, Expression<F>> function) {
        return set(targetField, function.apply(this.criteria1));
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField
            , BiFunction<C1, C2, Expression<F>> function) {
        return set(targetField, function.apply(this.criteria1, this.criteria2));
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, @Nullable F newValue) {
        if (newValue == null) {
            set(targetField, SQLS.asNull(targetField.mappingType()));
        } else {
            set(targetField, SQLS.param(newValue, targetField.mappingType()));
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField
            , @Nullable F newValue) {
        if (predicate.test(this.criteria1)) {
            set(targetField, newValue);
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
            , @Nullable F newValue) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            set(targetField, newValue);
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField
            , Expression<F> expression) {
        if (predicate.test(this.criteria1)) {
            set(targetField, expression);
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
            , Expression<F> expression) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            set(targetField, expression);
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField
            , Function<C1, F> fFunction) {
        if (predicate.test(this.criteria1)) {
            set(targetField, fFunction.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
            , BiFunction<C1, C2, F> biFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            set(targetField, biFunction.apply(this.criteria1, this.criteria2));
        }
        return this;
    }

    /*################################## blow WhereAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderAbleOfSingleUpdate<T, C1, C2> where(List<IPredicate> IPredicateList) {
        Assert.state(CollectionUtils.isEmpty(this.predicateList), "where clause ended.");
        Assert.notEmpty(IPredicateList, "no where clause forbade by army ");
        this.predicateList = Collections.unmodifiableList(IPredicateList);
        return this;
    }

    @Override
    public OrderAbleOfSingleUpdate<T, C1, C2> where(Function<C1, List<IPredicate>> function) {
        return where(function.apply(this.criteria1));
    }

    @Override
    public OrderAbleOfSingleUpdate<T, C1, C2> where(BiFunction<C1, C2, List<IPredicate>> biFunction) {
        return where(biFunction.apply(this.criteria1, this.criteria2));
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> where(IPredicate IPredicate) {
        Assert.state(CollectionUtils.isEmpty(this.predicateList), "where clause ended.");
        Assert.notNull(IPredicate, "predicate required");
        this.predicateList = new ArrayList<>();
        this.predicateList.add(IPredicate);
        return this;
    }

    @Override
    public OrderAbleOfSingleUpdate<T, C1, C2> where(Function<C1, IPredicate> function, boolean one) {
        return where(function.apply(this.criteria1));
    }

    @Override
    public OrderAbleOfSingleUpdate<T, C1, C2> where(BiFunction<C1, C2, IPredicate> biFunction, boolean one) {
        return where(biFunction.apply(this.criteria1, this.criteria2));
    }

    /*################################## blow WhereAndAbleOfSingleUpdate method ##################################*/

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(IPredicate IPredicate) {
        this.predicateList.add(IPredicate);
        return this;
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(Function<C1, IPredicate> function) {
        return and(function.apply(this.criteria1));
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiFunction<C1, C2, IPredicate> biFunction) {
        return and(biFunction.apply(this.criteria1, this.criteria2));
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria1)) {
            where(predicate);
        }
        return this;
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, IPredicate predicate) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            where(predicate);
        }
        return this;
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, Function<C1, IPredicate> function) {
        if (testPredicate.test(this.criteria1)) {
            where(function.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2
            , IPredicate> biFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            where(biFunction.apply(this.criteria1, this.criteria2));
        }
        return this;
    }

    /*################################## blow OrderAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Expression<?> orderExp) {
        return orderBy(orderExp, null);
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Function<C1, Expression<?>> function) {
        return orderBy(function.apply(this.criteria1));
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp) {
        if (testPredicate.test(this.criteria1)) {
            orderBy(orderExp);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            orderBy(orderExp);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate
            , Function<C1, Expression<?>> function) {
        if (testPredicate.test(this.criteria1)) {
            orderBy(function.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate
            , BiFunction<C1, C2, Expression<?>> biFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            orderBy(biFunction.apply(this.criteria1, this.criteria2));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate
            , Function<C1, Expression<?>> function, @Nullable Boolean asc) {

        if (testPredicate.test(this.criteria1)) {
            orderBy(function.apply(this.criteria1), asc);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate
            , BiFunction<C1, C2, Expression<?>> function, BiFunction<C1, C2, Boolean> ascFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            orderBy(
                    function.apply(this.criteria1, this.criteria2)
                    , ascFunction.apply(this.criteria1, this.criteria2)
            );
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp
            , Function<C1, Boolean> ascFunction) {
        if (testPredicate.test(this.criteria1)) {
            orderBy(orderExp, ascFunction.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp
            , BiFunction<C1, C2, Boolean> ascFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            orderBy(orderExp, ascFunction.apply(this.criteria1, this.criteria2));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Expression<?> orderExp, @Nullable Boolean asc) {
        Assert.state(CollectionUtils.isEmpty(orderExpList), "order by clause ended.");
        Assert.state(CollectionUtils.isEmpty(ascExpList), "order by clause ended.");

        orderExpList.add(orderExp);
        ascExpList.add(asc);
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiFunction<C1, C2, Expression<?>> function) {
        return orderBy(function.apply(this.criteria1, this.criteria2));
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Function<C1, Expression<?>> function, @Nullable Boolean asc) {
        return orderBy(function.apply(this.criteria1), asc);
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiFunction<C1, C2, Expression<?>> function
            , @Nullable Boolean asc) {
        return orderBy(function.apply(this.criteria1, this.criteria2), asc);
    }

    /*################################## blow OrderItemAbleOfSingleUpdate method ##################################*/

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Expression<?> orderExp) {
        return then(orderExp, null);
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Expression<?> orderExp, @Nullable Boolean asc) {
        Assert.state(!CollectionUtils.isEmpty(orderExpList), "no order by clause.");
        Assert.state(!CollectionUtils.isEmpty(ascExpList), "order by clause ended.");

        orderExpList.add(orderExp);
        ascExpList.add(asc);
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Function<C1, Expression<?>> function) {
        return then(function.apply(this.criteria1));
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(BiFunction<C1, C2, Expression<?>> biFunction) {
        return then(biFunction.apply(this.criteria1, this.criteria2));
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Predicate<C1> testPredicate, Expression<?> orderExp) {
        if (testPredicate.test(this.criteria1)) {
            then(orderExp);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            then(orderExp);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Predicate<C1> testPredicate
            , Function<C1, Expression<?>> function) {
        if (testPredicate.test(this.criteria1)) {
            then(function.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(BiPredicate<C1, C2> biPredicate
            , BiFunction<C1, C2, Expression<?>> biFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            then((biFunction.apply(this.criteria1, this.criteria2)));
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Predicate<C1> testPredicate, Function<C1, Expression<?>> function
            , @Nullable Boolean asc) {
        if (testPredicate.test(this.criteria1)) {
            then(function.apply(this.criteria1), asc);
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(BiPredicate<C1, C2> biPredicate
            , BiFunction<C1, C2, Expression<?>> function, BiFunction<C1, C2, Boolean> ascFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            then(
                    function.apply(this.criteria1, this.criteria2)
                    , ascFunction.apply(this.criteria1, this.criteria2)
            );
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(Predicate<C1> testPredicate, Expression<?> orderExp
            , Function<C1, Boolean> ascFunction) {
        if (testPredicate.test(this.criteria1)) {
            then(
                    orderExp
                    , ascFunction.apply(this.criteria1)
            );
        }
        return this;
    }

    @Override
    public OrderItemAbleOfSingleUpdate<T, C1, C2> then(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp
            , BiFunction<C1, C2, Boolean> ascFunction) {
        if (biPredicate.test(this.criteria1, this.criteria2)) {
            then(
                    orderExp
                    , ascFunction.apply(this.criteria1, this.criteria2)
            );
        }
        return this;
    }

    /*################################## blow LimitAbleOfSingleUpdate method ##################################*/

    @Override
    public SingleUpdateAble limit(int rowCount) {
        Assert.state(this.rowCount < 0, "order by clause ended.");
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public SingleUpdateAble limit(Function<C1, Integer> function) {
        return limit(function.apply(this.criteria1));
    }

    @Override
    public SingleUpdateAble limit(BiFunction<C1, C2, Integer> function) {
        return limit(function.apply(this.criteria1, this.criteria2));
    }

    @Override
    public SingleUpdateAble limit(Predicate<C1> predicate, int rowCount) {
        if (predicate.test(this.criteria1)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public SingleUpdateAble limit(BiPredicate<C1, C2> predicate, int rowCount) {
        if (predicate.test(this.criteria1, this.criteria2)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public SingleUpdateAble limit(Predicate<C1> predicate, Function<C1, Integer> function) {
        if (predicate.test(this.criteria1)) {
            limit(function.apply(this.criteria1));
        }
        return this;
    }

    @Override
    public SingleUpdateAble limit(BiPredicate<C1, C2> predicate, BiFunction<C1, C2, Integer> biFunction) {
        if (predicate.test(this.criteria1, this.criteria2)) {
            limit(biFunction.apply(this.criteria1, this.criteria2));
        }
        return this;
    }

    /*################################## blow io.army.criteria.impl.inner.InnerSingleSetAble method ##################################*/

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public TableMeta<?> tableMeta() {
        assertUpdateStatement();
        return tableMeta;
    }

    @Override
    public List<FieldMeta<?, ?>> targetFieldList() {
        assertUpdateStatement();
        return targetFieldList;
    }

    @Override
    public List<Expression<?>> valueExpressionList() {
        return valueExpressionList;
    }

    @Override
    public List<IPredicate> predicateList() {
        return predicateList;
    }

    @Override
    public List<Expression<?>> orderExpList() {
        return orderExpList;
    }

    @Override
    public List<Boolean> ascExpList() {
        return ascExpList;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    /*################################## blow io.army.criteria.SQLBuilder method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
        SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());
    }


    /*################################## blow private method ##################################*/

    private void assertUpdateStatement() {
        if (CollectionUtils.isEmpty(targetFieldList)
                || CollectionUtils.isEmpty(predicateList)) {
            throw new IllegalStateException("no targetField or no predicate");
        }
    }
}
