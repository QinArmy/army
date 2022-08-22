package io.army.sqltype;

import io.army.dialect.Database;

public interface SqlType {


    /**
     * @see Enum#name()
     */
    String name();


    Database database();

}
