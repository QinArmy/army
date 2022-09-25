package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PairConsumer<T> {


    PairConsumer<T> accept(FieldMeta<T> field, Expression value);

    PairConsumer<T> accept(FieldMeta<T> field, Supplier<?> supplier);

    PairConsumer<T> accept(FieldMeta<T> field, Function<String, ?> function, String keyName);

    PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value);

    PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier);

    PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

}
