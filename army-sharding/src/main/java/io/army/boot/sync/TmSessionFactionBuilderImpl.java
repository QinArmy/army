package io.army.boot.sync;

import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.sync.SessionFactoryAdvice;
import io.army.sync.TmSessionFactory;
import io.army.util.Assert;

import javax.sql.XADataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class TmSessionFactionBuilderImpl extends AbstractSyncSessionFactoryBuilder implements TmSessionFactionBuilder {

    static TmSessionFactionBuilderImpl buildInstance(boolean springApplication) {

        return new TmSessionFactionBuilderImpl(springApplication);
    }

    private List<XADataSource> dataSourceList;

    private Map<Integer, Database> databaseMap;

    private TmSessionFactionBuilderImpl(boolean springApplication) {
        super(springApplication);
    }


    @Override
    public final TmSessionFactionBuilder dataSourceList(List<XADataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder databaseMap(Map<Integer, Database> databaseMap) {
        this.databaseMap = databaseMap;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder environment(ArmyEnvironment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder factoryAdvice(Collection<SessionFactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder tableCountPerDatabase(int tableCountPerDatabase) {
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    @Nullable
    final List<XADataSource> dataSourceList() {
        return dataSourceList;
    }

    @Nullable
    final Map<Integer, Database> databaseMap() {
        return databaseMap;
    }

    final int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Override
    final ShardingMode shardingMode() {
        return ShardingMode.SHARDING;
    }

    @Override
    public final TmSessionFactory build() throws SessionFactoryException {
        Assert.hasText(this.name, "name required");
        Assert.notEmpty(this.dataSourceList, "dataSource list required");
        Assert.notNull(this.environment, "environment required");

        final CompositeSessionFactoryAdvice composite = getCompositeSessionFactoryAdvice();
        try {
            //1. beforeInstance
            composite.beforeInstance(this.environment);
            //2.  create TmSessionFactory instance
            final TmSessionFactoryImpl sessionFactory = createSessionFactory();
            // 3. beforeInitialize
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
            throw new SessionFactoryException(e, "create TmSessionFactory[%s] error.", this.name);
        }
    }

    /*################################## blow private method ##################################*/

    TmSessionFactoryImpl createSessionFactory() {
        return new TmSessionFactoryImpl(this);
    }

    boolean initializeSessionFactory(TmSessionFactoryImpl sessionFactory) {
        // init session factory
        return sessionFactory.initializeTmSessionFactory();
    }

}
