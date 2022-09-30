package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PairConsumer<T> {


    PairConsumer<T> accept(FieldMeta<T> field, Expression value);

    PairConsumer<T> accept(FieldMeta<T> field, Supplier<? extends Expression> supplier);

    PairConsumer<T> accept(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function);

    <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value);

    <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier);

    PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

}
