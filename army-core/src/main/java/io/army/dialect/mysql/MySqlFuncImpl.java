package io.army.dialect.mysql;

import io.army.dialect.AbstractFunc;

class MySqlFuncImpl extends AbstractFunc implements MySQLFunc {

    private final MySQLDialect mySQLDialect;

    public MySqlFuncImpl(MySQLDialect mySQLDialect) {
        this.mySQLDialect = mySQLDialect;
    }

    @Override
    public String now() {
        return null;
    }

    @Override
    public String now(int precision) {
        return null;
    }

    @Override
    public String currentDate() {
        return null;
    }

    @Override
    public String currentTime() {
        return null;
    }

    @Override
    public String currentTime(int precision) {
        return null;
    }
}
