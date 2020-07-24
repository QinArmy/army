package io.army.boot;


import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.boot.sync.AbstractSyncSessionFactory;
import io.army.boot.sync.SyncSessionFactoryUtils;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.sync.SessionFactory;
import io.army.util.Assert;
import io.army.util.Pair;

import javax.sql.DataSource;


/**
 * this class is a implementation of {@link SessionFactory}
 * Resource Manager (RM) {@link SessionFactory}.
 * <p>
 * this class run only below {@link io.army.ShardingMode}:
 *     <ul>
 *         <li>{@link io.army.ShardingMode#SHARDING}</li>
 *     </ul>
 * </p>
 */
final class RmSessionFactoryImpl extends AbstractSyncSessionFactory {

    private final DataSource dataSource;

    private final Dialect dialect;

    private final SQLDialect actualSQLDialect;

    private boolean closed;

    RmSessionFactoryImpl(RmSessionFactoryParams params, TmSessionFactoryImpl sessionFactory) {
        super(sessionFactory);
        Assert.state(this.shardingMode == ShardingMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", RmSessionFactoryImpl.class.getName()));
        this.dataSource = params.getDataSource();
        Assert.notNull(this.dataSource, "dataSource required");

        Pair<Dialect, SQLDialect> pair = SyncSessionFactoryUtils.createDialect(dataSource, this);
        this.dialect = pair.getFirst();
        this.actualSQLDialect = pair.getSecond();
    }

    @Override
    public boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public SQLDialect actualSQLDialect() {
        return this.actualSQLDialect;
    }

    @Override
    public void close() throws SessionFactoryException {
        this.closed = true;
    }

    @Override
    public SessionBuilder builder() {
        return null;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }


}
