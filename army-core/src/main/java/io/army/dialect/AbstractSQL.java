package io.army.dialect;


import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;

import java.time.ZoneId;

public abstract class AbstractSQL implements SqlDialect {

    protected final Dialect dialect;

    protected final boolean sharding;

    protected AbstractSQL(Dialect dialect) {
        this.dialect = dialect;
        this.sharding = this.dialect.sessionFactory().factoryMode() != FactoryMode.NO_SHARDING;
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
    public final boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public final GenericRmSessionFactory sessionFactory() {
        return this.dialect.sessionFactory();
    }

    @Override
    public final Database database() {
        return this.dialect.database();
    }

    @Override
    public final boolean tableAliasAfterAs() {
        return this.dialect.tableAliasAfterAs();
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        return this.dialect.singleDeleteHasTableAlias();
    }

    @Override
    public final boolean hasRowKeywords() {
        return this.dialect.hasRowKeywords();
    }


}
