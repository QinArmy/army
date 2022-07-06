package io.army.criteria;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface PairConsumer<F extends TableField> {


    PairConsumer<F> accept(F field, @Nullable Object value);

    PairConsumer<F> acceptLiteral(F field, @Nullable Object value);

    PairConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier);


}
