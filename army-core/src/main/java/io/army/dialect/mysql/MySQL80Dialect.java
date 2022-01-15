package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnvironment;

class MySQL80Dialect extends MySQL57Dialect {


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
