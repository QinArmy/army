package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnvironment;

class MySQL80Dialect extends MySQL57Dialect {

    static MySQL80Dialect create(DialectEnvironment environment) {
        return new MySQL80Dialect(environment);
    }


    MySQL80Dialect(DialectEnvironment environment) {
        super(environment);
    }


    /*################################## blow interfaces method ##################################*/


    @Override
    public Database database() {
        return Database.MySQL;
    }

    /*################################## blow AbstractDialect method ##################################*/


}
