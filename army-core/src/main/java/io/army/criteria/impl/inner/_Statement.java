package io.army.criteria.impl.inner;

public interface _Statement {

    default SessionMode sessionMode() {
        throw new UnsupportedOperationException();
    }

    void clear();

    enum SessionMode {

        WRITE,
        WRITE_TRANSACTION,
        READ
    }


}
