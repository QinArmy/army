package io.army.boot.sync;

import io.army.dialect.Database;

import javax.sql.XADataSource;
import java.util.List;
import java.util.Map;

class TmSessionFactionBuilderImpl {

    static TmSessionFactionBuilderImpl buildInstance(boolean springApplication) {

        return new TmSessionFactionBuilderImpl(springApplication);
    }

    private List<XADataSource> dataSourceList;

    private Map<Integer, Database> databaseMap;

    private TmSessionFactionBuilderImpl(boolean springApplication) {
        //  super(springApplication);
    }
//
//
//    @Override
//    TmSessionFactionBuilder dataSourceList(List<XADataSource> dataSourceList) {
//        this.dataSourceList = dataSourceList;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder databaseMap(Map<Integer, Database> databaseMap) {
//        this.databaseMap = databaseMap;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
//        this.fieldCodecs = fieldCodecs;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder name(String sessionFactoryName) {
//        this.name = sessionFactoryName;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder environment(ArmyEnvironment environment) {
//        this.environment = environment;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices) {
//        this.factoryAdvices = factoryAdvices;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
//        this.domainInterceptors = domainInterceptors;
//        return this;
//    }
//
//    @Override
//    TmSessionFactionBuilder tableCountPerDatabase(int tableCountPerDatabase) {
//        this.tableCountPerDatabase = tableCountPerDatabase;
//        return this;
//    }
//
//    @Nullable
//    final List<XADataSource> dataSourceList() {
//        return dataSourceList;
//    }
//
//    @Nullable
//    final Map<Integer, Database> databaseMap() {
//        return databaseMap;
//    }
//
//    final int tableCountPerDatabase() {
//        return this.tableCountPerDatabase;
//    }
//
//    @Override
//    final ShardingMode shardingMode() {
//        return ShardingMode.SHARDING;
//    }
//
//    @Override
//    TmSessionFactory build() throws SessionFactoryException {
//        Assert.hasText(this.name, "name required");
//        Assert.notEmpty(this.dataSourceList, "dataSource list required");
//        Assert.notNull(this.environment, "environment required");
//
//        final CompositeSessionFactoryAdvice composite = getCompositeSessionFactoryAdvice();
//        try {
//            //1. beforeInstance
//            composite.beforeInstance(this.environment);
//            //2.  create TmSessionFactory instance
//            final TmSessionFactoryImpl sessionFactory = createSessionFactory();
//            // 3. beforeInitialize
//            composite.beforeInitialize(sessionFactory);
//            //4. init session factory
//            if (initializeSessionFactory(sessionFactory)) {
//                //5. afterInitialize
//                composite.afterInitialize(sessionFactory);
//            }
//            return sessionFactory;
//        } catch (SessionFactoryException e) {
//            throw e;
//        } catch (Throwable e) {
//            throw new SessionFactoryException(e, "create TmSessionFactory[%s] error.", this.name);
//        }
//    }

    /*################################## blow private method ##################################*/

    TmSessionFactoryImpl createSessionFactory() {
        return new TmSessionFactoryImpl(this);
    }

    boolean initializeSessionFactory(TmSessionFactoryImpl sessionFactory) {
        // init session factory
        return sessionFactory.initializeTmSessionFactory();
    }

}
