package io.army;

import io.army.dialect.Database;

public enum DialectMode {

    MySQL57(Database.MySQL),
    MYSQL80(Database.MySQL);

    public final Database database;

    DialectMode(Database database) {
        this.database = database;
    }


}
