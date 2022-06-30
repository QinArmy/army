package io.army.criteria;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface AliasColumnConsumer<F extends TableField> extends ColumnConsumer<F> {

    AliasColumnConsumer<F> accept(F field, @Nullable Object value);

    AliasColumnConsumer<F> acceptLiteral(F field, @Nullable Object value);

    AliasColumnConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier);

    AliasColumnConsumer<F> accept(String field, @Nullable Object value);

    AliasColumnConsumer<F> acceptLiteral(String field, @Nullable Object value);

    AliasColumnConsumer<F> acceptExp(String field, Supplier<? extends Expression> supplier);


}
