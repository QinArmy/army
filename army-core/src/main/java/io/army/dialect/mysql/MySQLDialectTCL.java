package io.army.dialect.mysql;

import io.army.dialect.tcl.AbstractDialectTCL;

public class MySQLDialectTCL extends AbstractDialectTCL {
    private final MySQLDialect mySQLDialect;

    public MySQLDialectTCL(MySQLDialect mySQLDialect) {
        this.mySQLDialect = mySQLDialect;
    }
}
