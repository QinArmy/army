package io.army.dialect.mysql;

import io.army.SessionFactory;
import io.army.dialect.SQLDialect;
import io.army.dialect.TableDDL;
import io.army.dialect.TableDML;
import io.army.dialect.TableDQL;

import java.util.Set;

class MySQL80Dialect extends MySQL57Dialect {


    MySQL80Dialect(SessionFactory sessionFactory) {
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
    protected TableDDL createTableDDL() {
        return new MySQL80TableDDL(this);
    }

    @Override
    protected TableDML createTableDML() {
        return super.createTableDML();
    }

    @Override
    protected TableDQL createTableDQL() {
        return super.createTableDQL();
    }
}
