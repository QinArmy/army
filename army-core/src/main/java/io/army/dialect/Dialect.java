package io.army.dialect;

public interface Dialect {

    String name();

    Database database();

    int version();

    @Override
    String toString();


}
