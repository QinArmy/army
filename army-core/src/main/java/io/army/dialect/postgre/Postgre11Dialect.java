package io.army.dialect.postgre;

import io.army.GenericRmSessionFactory;
import io.army.dialect.*;
import io.army.meta.mapping.MappingMeta;

import java.util.Set;

class Postgre11Dialect extends AbstractDialect {

    Postgre11Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public Database database() {
        return Database.Postgre11;
    }

    @Override
    protected Set<String> createKeywordsSet() {
        return Postgre11DialectUtils.create11KeywordsSet();
    }

    @Override
    protected final String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    protected DDL createDDL() {
        return new Postgre11DDL(this);
    }

    @Override
    protected DML createDML() {
        return null;
    }

    @Override
    protected DQL createDQL() {
        return null;
    }

    @Override
    public final boolean supportZone() {
        // always true
        return true;
    }

    @Override
    public String mapping(MappingMeta mappingType) {
        return null;
    }

    @Override
    public final boolean tableAliasAfterAs() {
        // always true
        return true;
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        // always true
        return true;
    }

    @Override
    public final boolean hasRowKeywords() {
        // always true
        return true;
    }
}
