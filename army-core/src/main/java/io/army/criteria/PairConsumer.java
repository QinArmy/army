package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.function.Supplier;

public interface PairConsumer<T> {


    PairConsumer<T> accept(FieldMeta<T> field, @Nullable Object value);

    PairConsumer<T> acceptLiteral(FieldMeta<T> field, @Nullable Object value);

    PairConsumer<T> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);


}
