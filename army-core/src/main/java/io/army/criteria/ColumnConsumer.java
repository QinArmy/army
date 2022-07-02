package io.army.criteria;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface ColumnConsumer<F extends TableField> {


    ColumnConsumer<F> accept(F field, @Nullable Object value);

    ColumnConsumer<F> acceptLiteral(F field, @Nullable Object value);

    ColumnConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier);


}