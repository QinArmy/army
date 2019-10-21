package io.army.dialect.mysql;

import io.army.dialect.AbstractFunc;

class MySQL5757FuncImpl extends AbstractFunc implements MySQL57Func {

    private final MySQL57Dialect mySQLDialect;

    public MySQL5757FuncImpl(MySQL57Dialect mySQLDialect) {
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
