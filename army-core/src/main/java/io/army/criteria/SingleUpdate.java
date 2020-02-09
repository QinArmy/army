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
     * @see UpdateAble
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
     * @see UpdateAble
     */
    interface WhereAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SetAbleOfSingleUpdate<T, C1, C2> {

        OrderAbleOfUpdate<T, C1, C2> where(List<IPredicate> predicateList);

        OrderAbleOfUpdate<T, C1, C2> where(Function<C1, List<IPredicate>> function);

        OrderAbleOfUpdate<T, C1, C2> where(BiFunction<C1, C2, List<IPredicate>> biFunction);

        WhereAndAbleOfUpdate<T, C1, C2> where(IPredicate predicate);

        OrderAbleOfUpdate<T, C1, C2> where(Function<C1, IPredicate> function, boolean one);

        OrderAbleOfUpdate<T, C1, C2> where(BiFunction<C1, C2, IPredicate> biFunction, boolean one);

    }

    interface WhereAndAbleOfUpdate<T extends IDomain, C1, C2> extends OrderAbleOfUpdate<T, C1, C2> {

        WhereAndAbleOfUpdate<T, C1, C2> and(IPredicate predicate);

        WhereAndAbleOfUpdate<T, C1, C2> and(Function<C1, IPredicate> function);

        WhereAndAbleOfUpdate<T, C1, C2> and(BiFunction<C1, C2, IPredicate> biFunction);

        WhereAndAbleOfUpdate<T, C1, C2> and(Predicate<C1> testPredicate, IPredicate predicate);

        WhereAndAbleOfUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, IPredicate predicate);

        WhereAndAbleOfUpdate<T, C1, C2> and(Predicate<C1> testPredicate, Function<C1, IPredicate> function);

        WhereAndAbleOfUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, IPredicate> biFunction);
    }

    interface LimitAbleOfUpdate<T extends IDomain, C1, C2> extends UpdateAble,SQLBuilder {

        UpdateAble limit(int rowCount);

        UpdateAble limit(Function<C1, Integer> function);

        UpdateAble limit(BiFunction<C1, C2, Integer> function);

        UpdateAble limit(Predicate<C1> predicate, int rowCount);

        UpdateAble limit(BiPredicate<C1, C2> predicate, int rowCount);

        UpdateAble limit(Predicate<C1> predicate, Function<C1, Integer> function);

        UpdateAble limit(BiPredicate<C1, C2> predicate, BiFunction<C1, C2, Integer> biFunction);
    }
}
