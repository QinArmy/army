package io.army.dialect;

import io.army.lang.Nullable;
import io.army.tx.Isolation;

public interface TransactionOption {

    @Nullable
    String name();

    boolean readOnly();

    Isolation isolation();

    int timeout();


}
