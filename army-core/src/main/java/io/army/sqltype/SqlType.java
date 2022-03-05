package io.army.sqltype;

import io.army.Database;

public interface SqlType {

    Database database();

    /**
     * @see Enum#name()
     */
    String name();

}
