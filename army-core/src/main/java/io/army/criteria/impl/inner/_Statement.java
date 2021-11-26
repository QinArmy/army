package io.army.criteria.impl.inner;

import io.army.criteria.Visible;

public interface _Statement {

    SessionMode sessionMode();

    void clear();

    Visible visible();

    enum SessionMode {

        WRITE,
        WRITE_TRANSACTION,
        READ
    }


}
