package io.army.criteria;

import java.util.function.Supplier;

public interface ColumnConsumer {


    ColumnConsumer accept(Object value);

    ColumnConsumer acceptLiteral(Object value);

    ColumnConsumer acceptExp(Supplier<? extends Expression> supplier);

}
