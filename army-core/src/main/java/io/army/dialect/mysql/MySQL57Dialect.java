package io.army.dialect.mysql;


import io.army.GenericRmSessionFactory;
import io.army.dialect.*;
import io.army.meta.mapping.MappingMeta;

import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 */
class MySQL57Dialect extends AbstractDialect {


    MySQL57Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);

    }

    /*################################## blow interfaces method ##################################*/


    @Override
    public Database database() {
        return Database.MySQL57;
    }

    @Override
    public final boolean supportZone() {
        return false;
    }

    @Override
    public String mapping(MappingMeta mappingType) {
        throw new UnsupportedOperationException();
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

    @Override
    protected final String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLUtils.create57KeywordsSet();
    }

    @Override
    protected DDL createTableDDL() {
        return new MySQL57DDL(this);
    }

    @Override
    protected DML createDML() {
        return new MySQL57DML(this);
    }

    @Override
    protected DQL createDQL() {
        return new MySQL57DQL(this);
    }


}
