package io.army.sync;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.session.GenericRmSessionFactory;

final class MockSessionFactoryBuilder extends AbstractSyncSessionFactoryBuilder {

    public MockSessionFactoryBuilder() {
        super(false);
    }

    MockSessionFactoryBuilder(boolean springApplication) {
        super(springApplication);
    }
//
//    @Override
//    public GenericFactoryBuilder<> exceptionFunction(Function function) {
//        return null;
//    }
//
//    @Override
//    public GenericFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
//        return null;
//    }
//
//    @Override
//    public GenericFactoryBuilder name(String sessionFactoryName) {
//        return null;
//    }
//
//    @Override
//    public GenericFactoryBuilder environment(ArmyEnvironment environment) {
//        return null;
//    }
//
//    @Override
//    public SyncSessionFactoryBuilder factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices) {
//        return null;
//    }
//
//    @Override
//    public SyncSessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase) {
//        return null;
//    }
//
//    @Override
//    public SyncSessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
//        return null;
//    }

    private static Dialect createDialect(GenericRmSessionFactory sessionFactory, Database sqlDialect) {
        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
                dialect = MySQLDialectFactory.createMySQLDialect(sqlDialect, sessionFactory);
                break;
            case PostgreSQL:
            case Oracle:
            default:
                throw new IllegalArgumentException();
        }
        return dialect;
    }
}
