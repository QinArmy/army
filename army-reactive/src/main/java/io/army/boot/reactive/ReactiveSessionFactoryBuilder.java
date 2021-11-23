package io.army.boot.reactive;

import io.army.SessionFactoryException;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.FactoryMode;


public interface ReactiveSessionFactoryBuilder
        extends GenericReactiveSessionFactoryBuilder<ReactiveSessionFactoryBuilder> {


    /**
     * @param databaseSessionFactory {@code io.jdbd.DatabaseSessionFactory}
     */
    ReactiveSessionFactoryBuilder datasource(Object databaseSessionFactory);

    /**
     * possible values below:
     * <ul>
     *     <li>{@link FactoryMode#NO_SHARDING }</li>
     *     <li>{@link FactoryMode#TABLE_SHARDING}</li>
     * </ul>
     * Default  is {@link FactoryMode#NO_SHARDING}
     */
    ReactiveSessionFactoryBuilder shardingMode(FactoryMode factoryMode);

    ReactiveSessionFactory build() throws SessionFactoryException;

    static ReactiveSessionFactoryBuilder builder() {
        return builder(false);
    }

    static ReactiveSessionFactoryBuilder builder(boolean springApplication) {
        return ReactiveSessionFactoryBuilderImpl.build(springApplication);
    }

}
