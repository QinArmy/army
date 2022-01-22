package io.army.dialect.mysql;


import io.army.dialect._Dialect;
import io.army.dialect._DialectEnvironment;

import java.util.Set;

/**
 * this class is a  {@link _Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 */
abstract class MySQL57 extends MySQLDialect {

    static MySQL57 create(_DialectEnvironment environment) {
        return new MySQL57StandardDialect(environment);
    }


    MySQL57(_DialectEnvironment environment) {
        super(environment);

    }

    /*################################## blow interfaces method ##################################*/


    @Override
    public final boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public final boolean hasRowKeywords() {
        return true;
    }

    /*####################################### below AbstractDialect template  method #################################*/

    @Override
    protected final Set<String> createKeyWordSet() {
        return MySQLDialectUtils.create57KeywordsSet();
    }




    private static final class MySQL57StandardDialect extends MySQL57 {

        private MySQL57StandardDialect(_DialectEnvironment environment) {
            super(environment);
        }

    }


}
