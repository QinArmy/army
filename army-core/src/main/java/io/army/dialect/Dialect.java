package io.army.dialect;

public interface Dialect {

    String name();

    Database database();

    @Deprecated
    int version();

    @Override
    String toString();


}
