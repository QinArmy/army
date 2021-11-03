package io.army.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.advice.GenericSessionFactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.function.Function;

final class SessionFactoryBuilderImpl extends AbstractSyncSessionFactoryBuilder implements SessionFactoryBuilder {

    static SessionFactoryBuilderImpl buildInstance(boolean springApplication) {

        return new SessionFactoryBuilderImpl(springApplication);
    }

    private DataSource dataSource;

    public SessionFactoryBuilderImpl(boolean springApplication, DataSource dataSource) {
        super(springApplication);
        this.dataSource = dataSource;
    }


    private SessionFactoryBuilderImpl(boolean springApplication) {
        super(springApplication);
    }


    @Override
    public SessionFactoryBuilder exceptionFunction(Function<RuntimeException, RuntimeException> exceptionFunction) {
        return null;
    }


    @Override
    public final SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        //  this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public final SessionFactoryBuilder name(String sessionFactoryName) {
        // this.name = sessionFactoryName;
        return this;
    }

    @Override
    public final SessionFactoryBuilder environment(ArmyEnvironment environment) {
        //this.environment = environment;
        return this;
    }

    @Override
    public final SessionFactoryBuilder factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices) {
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



    @Override
    public final SessionFactory build() throws SessionFactoryException {
//        Assert.notNull(this.name, "name required");
//        Assert.notNull(this.dataSource, "dataSource required");
//        Assert.notNull(this.environment, "environment required");
//        Assert.notNull(this.shardingMode, "shardingMode required");

        // final GenericSessionFactoryAdvice factoryAdvice = getFactoryAdviceComposite();
        try {
            //1. beforeInstance
            //  composite.beforeInstance(this.environment);
            //2. create SessionFactory instance
            SessionFactoryImpl sessionFactory = createSessionFactory();
            //3. beforeInitialize
            //  composite.beforeInitialize(sessionFactory);
            //4. init session factory
            if (initializeSessionFactory(sessionFactory)) {
                //5. afterInitialize
                // composite.afterInitialize(sessionFactory);
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

    private SessionFactoryImpl createSessionFactory() {

        return new SessionFactoryImpl(this);
    }

    boolean initializeSessionFactory(SessionFactoryImpl sessionFactory) {
        // init session factory
        return sessionFactory.initializeSessionFactory();
    }

    /*################################## blow package static class ##################################*/


}
