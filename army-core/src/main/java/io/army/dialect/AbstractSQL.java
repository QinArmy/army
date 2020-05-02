package io.army.dialect;


import io.army.GenericSessionFactory;
import io.army.meta.mapping.MappingMeta;

import java.time.ZoneId;

public abstract class AbstractSQL implements SQL {

    protected final Dialect dialect;

    protected AbstractSQL(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public final String quoteIfNeed(String identifier) {
        return this.dialect.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return this.dialect.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return this.dialect.zoneId();
    }

    @Override
    public final boolean supportZoneId() {
        return this.dialect.supportZoneId();
    }

    @Override
    public final GenericSessionFactory sessionFactory() {
        return this.dialect.sessionFactory();
    }

    @Override
    public final String mapping(MappingMeta mappingType) {
        return this.dialect.mapping(mappingType);
    }

    @Override
    public final SQLDialect sqlDialect() {
        return this.dialect.sqlDialect();
    }
}
