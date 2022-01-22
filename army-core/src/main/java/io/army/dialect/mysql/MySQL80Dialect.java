package io.army.dialect.mysql;

import io.army.dialect._DialectEnvironment;

import java.util.Set;

class MySQL80Dialect extends MySQLDialect {

    static MySQL80Dialect create(_DialectEnvironment environment) {
        return new MySQL80Dialect(environment);
    }


    MySQL80Dialect(_DialectEnvironment environment) {
        super(environment);
    }


    /*################################## blow interfaces method ##################################*/

    @Override
    protected final Set<String> createKeyWordSet() {
        return MySQLDialectUtils.create80KeywordsSet();
    }

    /*################################## blow AbstractDialect method ##################################*/


}
