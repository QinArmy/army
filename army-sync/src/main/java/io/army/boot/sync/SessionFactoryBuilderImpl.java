package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactory;
import io.army.sync.SessionFactoryAdvice;
import io.army.util.Assert;
import io.army.util.BeanUtils;

import javax.sql.DataSource;
import java.util.Collection;

class SessionFactoryBuilderImpl extends AbstractSyncSessionFactoryBuilder implements SessionFactoryBuilder {

    static SessionFactoryBuilderImpl buildInstance(boolean springApplication) {
        SessionFactoryBuilderImpl builder;
        if (springApplication) {
            try {
                builder = (SessionFactoryBuilderImpl) BeanUtils.instantiateClass(
                        Class.forName("io.army.boot.sync.SessionFactoryBuilderForSpring"));
            } catch (ClassNotFoundException e) {
                throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                        , "army-spring module not add classpath.");
            }
        } else {
            builder = new SessionFactoryBuilderImpl();
        }
        return builder;
    }

    private DataSource dataSource;


    SessionFactoryBuilderImpl() {

    }


    @Override
    public final SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public final SessionFactoryBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public final SessionFactoryBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public final SessionFactoryBuilder factoryAdvice(Collection<SessionFactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public final SessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public final SessionFactoryBuilder shardingMode(ShardingMode shardingMode) {
        this.shardingMode = shardingMode;
        return this;
    }

    @Override
    public final SessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase) {
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    @Override
    public final SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public final DataSource dataSource() {
        return this.dataSource;
    }

    final int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Override
    public final SessionFactory build() throws SessionFactoryException {
        Assert.notNull(this.name, "name required");
        Assert.notNull(this.dataSource, "dataSource required");
        Assert.notNull(this.environment, "environment required");

        final SessionFactoryAdvice composite = getCompositeSessionFactoryAdvice();
        try {
            //1. beforeInstance
            composite.beforeInstance(this.environment);
            //2. create SessionFactory instance
            SessionFactoryImpl sessionFactory = createSessionFactory();
            //3. beforeInitialize
            composite.beforeInitialize(sessionFactory);
            //4. init session factory
            if (initializeSessionFactory(sessionFactory)) {
                //5. afterInitialize
                composite.afterInitialize(sessionFactory);
            }
            return sessionFactory;
        } catch (SessionFactoryException e) {
            throw e;
        } catch (Throwable e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "create session factory error.");
        }
    }



    /*################################## blow private method ##################################*/

    SessionFactoryImpl createSessionFactory() {
        return new SessionFactoryImpl(this);
    }

    boolean initializeSessionFactory(SessionFactoryImpl sessionFactory) {
        // init session factory
        return sessionFactory.initializeSessionFactory();
    }

    /*################################## blow package static class ##################################*/


}
