package io.army.dialect.mysql;

import io.army.dialect.DialectEnvironment;

import java.util.Set;

class MySQL80Dialect extends MySQLDialect {

    static MySQL80Dialect create(DialectEnvironment environment) {
        return new MySQL80Dialect(environment);
    }


    MySQL80Dialect(DialectEnvironment environment) {
        super(environment);
    }


    /*################################## blow interfaces method ##################################*/

    @Override
    protected final Set<String> createKeyWordSet() {
        return MySQLDialectUtils.create80KeywordsSet();
    }

    /*################################## blow AbstractDialect method ##################################*/


}
