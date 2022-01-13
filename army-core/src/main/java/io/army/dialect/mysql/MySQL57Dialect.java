package io.army.dialect.mysql;


import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.session.DialectSessionFactory;

/**
 * this class is a  {@link Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 */
class MySQL57Dialect extends MySQLDialect {


    MySQL57Dialect(DialectSessionFactory sessionFactory) {
        super(sessionFactory);

    }

    /*################################## blow interfaces method ##################################*/


    @Override
    public Database database() {
        return Database.MySQL;
    }

    @Override
    public final boolean supportSavePoint() {
        // always true
        return true;
    }

    @Override
    public final boolean supportZone() {
        return false;
    }

    @Override
    public final boolean tableAliasAfterAs() {
        return true;
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public final boolean hasRowKeywords() {
        return true;
    }

    /*####################################### below AbstractDialect template  method #################################*/

}
