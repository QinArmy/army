package io.army.dialect.mysql;

import io.army.GenericRmSessionFactory;
import io.army.GenericSessionFactory;
import io.army.dialect.DDL;
import io.army.dialect.DML;
import io.army.dialect.DQL;
import io.army.dialect.SQLDialect;

import java.util.Set;

class MySQL80Dialect extends MySQL57Dialect {


    MySQL80Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    /*################################## blow interfaces method ##################################*/


    @Override
    public SQLDialect sqlDialect() {
        return SQLDialect.MySQL80;
    }

    /*################################## blow AbstractDialect method ##################################*/

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLUtils.create80KeywordsSet();
    }

    @Override
    protected DDL createTableDDL() {
        return new MySQL80TableDDL(this);
    }

    @Override
    protected DML createDML() {
        return super.createDML();
    }

    @Override
    protected DQL createDQL() {
        return super.createDQL();
    }
}
