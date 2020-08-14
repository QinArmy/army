package io.army.boot.sync;


import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactory;
import io.army.sync.SessionFactoryAdvice;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder extends SyncSessionFactoryBuilder {

    @Override
    SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    @Override
    SessionFactoryBuilder name(String sessionFactoryName);

    @Override
    SessionFactoryBuilder environment(ArmyEnvironment environment);

    @Override
    SessionFactoryBuilder factoryAdvice(Collection<SessionFactoryAdvice> factoryAdvices);

    @Override
    SessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase);

    @Override
    SessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors);

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
