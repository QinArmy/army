package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <T> entity java class
 * @see SingleUpdateAble
 */
public interface SetAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SingleUpdate {


    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Expression<F> expression);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, Function<C1,Expression<F>> function);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, BiFunction<C1,C2,Expression<F>> function);


    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(FieldMeta<T, F> targetField, @Nullable F newValue);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField, @Nullable F newValue);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
            ,  @Nullable F newValue);


    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> predicate, FieldMeta<T, F> targetField, Expression<F> expression);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField
            , Expression<F> expression);


    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(Predicate<C1> biPredicate, FieldMeta<T, F> targetField, Function<C1, F> fFunction);

    <F> WhereAbleOfSingleUpdate<T, C1, C2> set(BiPredicate<C1, C2> biPredicate, FieldMeta<T, F> targetField, BiFunction<C1, C2, F> biFunction);


}
