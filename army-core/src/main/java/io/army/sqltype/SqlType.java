package io.army.sqltype;

import io.army.session.Database;

public interface SqlType {

    Database database();

    /**
     * @see Enum#name()
     */
    String name();

}
