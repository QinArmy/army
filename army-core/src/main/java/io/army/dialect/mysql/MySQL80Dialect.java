package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.session.GenericRmSessionFactory;

import java.util.Set;

class MySQL80Dialect extends MySQL57Dialect {


    MySQL80Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    /*################################## blow interfaces method ##################################*/


    @Override
    public Database database() {
        return Database.MySQL;
    }

    /*################################## blow AbstractDialect method ##################################*/

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLDialectUtils.create80KeywordsSet();
    }

    @Override
    protected DdlDialect createDDL() {
        return new MySQL80DDL(this);
    }

    @Override
    protected DmlDialect createDML() {
        return new MySQL80DmlDialect(this);
    }


    @Override
    protected TclDialect createTCL() {
        return new MySQL80TCL(this);
    }
}
