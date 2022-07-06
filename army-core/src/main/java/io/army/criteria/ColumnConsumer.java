package io.army.criteria;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface ColumnConsumer {


    ColumnConsumer comma(@Nullable Object value);

    ColumnConsumer commaLiteral(@Nullable Object value);

    ColumnConsumer commaExp(Supplier<? extends Expression> supplier);

}
