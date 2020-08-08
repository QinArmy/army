package io.army.dialect.postgre;

import io.army.GenericRmSessionFactory;
import io.army.dialect.AbstractDialect;
import io.army.dialect.DDL;
import io.army.dialect.DML;
import io.army.dialect.DQL;
import io.army.meta.mapping.MappingMeta;

import java.util.Set;

class Postgre11Dialect extends AbstractDialect {

    Postgre11Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    protected Set<String> createKeywordsSet() {
        return Postgre11DialectUtils.create11KeywordsSet();
    }

    @Override
    protected String doQuote(String identifier) {
        return null;
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
    public boolean supportZone() {
        return false;
    }

    @Override
    public String mapping(MappingMeta mappingType) {
        return null;
    }

    @Override
    public boolean tableAliasAfterAs() {
        return false;
    }

    @Override
    public boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public boolean hasRowKeywords() {
        return false;
    }
}
