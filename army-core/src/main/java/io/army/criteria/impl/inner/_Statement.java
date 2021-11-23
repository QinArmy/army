package io.army.criteria.impl.inner;

public interface _Statement {

    SessionMode sessionMode();

    void clear();

    enum SessionMode {

        WRITE,
        WRITE_TRANSACTION,
        READ
    }


}
