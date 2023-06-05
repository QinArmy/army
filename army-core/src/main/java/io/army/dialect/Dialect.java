package io.army.dialect;

public interface Dialect {

    String name();

    Database database();

    @Deprecated
    default int version() {
        throw new UnsupportedOperationException();
    }

    @Override
    String toString();


}
