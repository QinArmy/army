package io.army.dialect.mysql;

import io.army.dialect.AbstractTableDML;

class MySQLTableDML extends AbstractTableDML {

    private final MySQLDialect mySQLDialect;

    MySQLTableDML(MySQLDialect mySQLDialect) {
        this.mySQLDialect = mySQLDialect;
    }


}
