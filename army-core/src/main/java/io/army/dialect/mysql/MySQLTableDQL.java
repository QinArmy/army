package io.army.dialect.mysql;

import io.army.dialect.AbstractTableDQL;

class MySQLTableDQL extends AbstractTableDQL {


    private final MySQLDialect mySQLDialect;

    public MySQLTableDQL(MySQLDialect mySQLDialect) {
        this.mySQLDialect = mySQLDialect;
    }
}
