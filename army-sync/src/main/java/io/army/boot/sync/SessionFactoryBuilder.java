package io.army.boot.sync;


import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.sync.SessionFactory;

import javax.sql.DataSource;

/**
 * builder for {@link SessionFactory}
 */
public interface SessionFactoryBuilder extends SyncSessionFactoryBuilder<SessionFactoryBuilder> {


    SessionFactoryBuilder datasource(DataSource dataSource);

    /**
     * possible values below:
     * <ul>
     *     <li>{@link ShardingMode#NO_SHARDING }</li>
     *     <li>{@link ShardingMode#SINGLE_DATABASE_SHARDING}</li>
     * </ul>
     * Default value is {@link ShardingMode#NO_SHARDING}
     */
    SessionFactoryBuilder shardingMode(ShardingMode shardingMode);

    SessionFactory build() throws SessionFactoryException;


    static SessionFactoryBuilder builder() {
        return builder(false);
    }

    static SessionFactoryBuilder builder(boolean springApplication) {
        return SessionFactoryBuilderImpl.buildInstance(springApplication);
    }

    static SessionFactoryBuilder mockBuilder() {
        throw new UnsupportedOperationException();
    }


}
