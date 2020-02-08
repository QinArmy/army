package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SingleUpdate extends SQLAble{

    interface AliasAbleOfSingleUpdate<T extends IDomain,C1,C2> extends SetAbleOfSingleUpdate<T,C1,C2> {

        SetAbleOfSingleUpdate<T,C1,C2>  as(String tableAlias);


    }

    /**
     * @param <T> entity java class
     * @see SingleUpdateAble
     */
    interface SetAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SingleUpdate {


        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Expression<F> expression);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Function<C1, Expression<F>> function);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, BiFunction<C1, C2, Expression<F>> function);


        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, @Nullable F newValue);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField, @Nullable F newValue);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
                , @Nullable F newValue);


        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField, Expression<F> expression);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
                , Expression<F> expression);


        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> biPredicate, FieldMeta<T, F> targetField, Function<C1, F> fFunction);

        <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField, BiFunction<C1, C2, F> biFunction);


    }

    /**
     * @param <T> domain java class
     * @see SingleUpdateAble
     */
    interface WhereAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SetAbleOfSingleUpdate<T, C1, C2> {

        OrderAbleOfSingleUpdate<T, C1, C2> where(List<IPredicate> predicateList);

        OrderAbleOfSingleUpdate<T, C1, C2> where(Function<C1, List<IPredicate>> function);

        OrderAbleOfSingleUpdate<T, C1, C2> where(BiFunction<C1, C2, List<IPredicate>> biFunction);

        WhereAndAbleOfSingleUpdate<T, C1, C2> where(IPredicate predicate);

        OrderAbleOfSingleUpdate<T, C1, C2> where(Function<C1, IPredicate> function, boolean one);

        OrderAbleOfSingleUpdate<T, C1, C2> where(BiFunction<C1, C2, IPredicate> biFunction, boolean one);

    }

    interface WhereAndAbleOfSingleUpdate<T extends IDomain, C1, C2> extends OrderAbleOfSingleUpdate<T, C1, C2> {

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(IPredicate predicate);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(Function<C1, IPredicate> function);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiFunction<C1, C2, IPredicate> biFunction);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, IPredicate predicate);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, IPredicate predicate);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, Function<C1, IPredicate> function);

        WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, IPredicate> biFunction);
    }

    interface LimitAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SingleUpdateAble,SQLBuilder {

        SingleUpdateAble limit(int rowCount);

        SingleUpdateAble limit(Function<C1, Integer> function);

        SingleUpdateAble limit(BiFunction<C1, C2, Integer> function);

        SingleUpdateAble limit(Predicate<C1> predicate, int rowCount);

        SingleUpdateAble limit(BiPredicate<C1, C2> predicate, int rowCount);

        SingleUpdateAble limit(Predicate<C1> predicate, Function<C1, Integer> function);

        SingleUpdateAble limit(BiPredicate<C1, C2> predicate, BiFunction<C1, C2, Integer> biFunction);
    }
}
