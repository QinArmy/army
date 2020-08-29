package io.army.boot.reactive;

import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.reactive.ReactiveSessionFactory;


public interface ReactiveSessionFactoryBuilder
        extends GenericReactiveSessionFactoryBuilder<ReactiveSessionFactoryBuilder> {


    /**
     * @param databaseSessionFactory {@code io.jdbd.DatabaseSessionFactory}
     */
    ReactiveSessionFactoryBuilder datasource(Object databaseSessionFactory);

    /**
     * possible values below:
     * <ul>
     *     <li>{@link ShardingMode#NO_SHARDING }</li>
     *     <li>{@link ShardingMode#SINGLE_DATABASE_SHARDING}</li>
     * </ul>
     * Default  is {@link ShardingMode#NO_SHARDING}
     */
    ReactiveSessionFactoryBuilder shardingMode(ShardingMode shardingMode);

    ReactiveSessionFactory build() throws SessionFactoryException;

    static ReactiveSessionFactoryBuilder builder() {
        return builder(false);
    }

    static ReactiveSessionFactoryBuilder builder(boolean springApplication) {
        return ReactiveSessionFactoryBuilderImpl.build(springApplication);
    }

}
