package io.army.dialect.mysql;

import io.army.GenericRmSessionFactory;
import io.army.dialect.DDL;
import io.army.dialect.DmlDialect;
import io.army.dialect.Database;
import io.army.dialect.TCL;

import java.util.Set;

class MySQL80Dialect extends MySQL57Dialect {


    MySQL80Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    /*################################## blow interfaces method ##################################*/


    @Override
    public Database database() {
        return Database.MySQL80;
    }

    /*################################## blow AbstractDialect method ##################################*/

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLDialectUtils.create80KeywordsSet();
    }

    @Override
    protected DDL createDDL() {
        return new MySQL80DDL(this);
    }

    @Override
    protected DmlDialect createDML() {
        return new MySQL80DmlDialect(this);
    }


    @Override
    protected TCL createTCL() {
        return new MySQL80TCL(this);
    }
}
