package io.army.dialect.mysql;


import io.army.SessionFactory;
import io.army.dialect.*;

import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 * created  on 2018/10/21.
 */
class MySQL57Dialect extends AbstractDialect {


    MySQL57Dialect(SessionFactory sessionFactory) {
        super(sessionFactory);

    }

    /*################################## blow interfaces method ##################################*/


    @Override
    public SQLDialect sqlDialect() {
        return SQLDialect.MySQL57;
    }

    @Override
    public final boolean supportZoneId() {
        return false;
    }

    /*####################################### below AbstractDialect template  method #################################*/

    @Override
    protected final String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLUtils.create57KeywordsSet();
    }

    @Override
    protected TableDDL createTableDDL() {
        return new MySQL57TableDDL(this);
    }

    @Override
    protected DML createTableDML() {
        return new MySQL57TableDML(this);
    }

    @Override
    protected DQL createTableDQL() {
        return null;
    }
}
