package io.army.dialect;


import io.army.SessionFactory;
import io.army.meta.mapping.MappingType;

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
    public final SessionFactory sessionFactory() {
        return this.dialect.sessionFactory();
    }

    @Override
    public final String mapping(MappingType mappingType) {
        return this.dialect.mapping(mappingType);
    }


}