package io.army.tx;

import io.army.lang.Nullable;

public interface TransactionOption {

    @Nullable
    String name();

    boolean readOnly();

    Isolation isolation();

    int timeout();


}
