package io.army.sync;

import io.army.GenericRmSessionFactory;
import io.army.advice.GenericSessionFactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.ArmyEnvironment;

import java.util.Collection;

final class MockSessionFactoryBuilder extends AbstractSyncSessionFactoryBuilder {

    MockSessionFactoryBuilder(boolean springApplication) {
        super(springApplication);
    }

    @Override
    public GenericFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        return null;
    }

    @Override
    public GenericFactoryBuilder name(String sessionFactoryName) {
        return null;
    }

    @Override
    public GenericFactoryBuilder environment(ArmyEnvironment environment) {
        return null;
    }

    @Override
    public SyncSessionFactoryBuilder factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices) {
        return null;
    }

    @Override
    public SyncSessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase) {
        return null;
    }

    @Override
    public SyncSessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
        return null;
    }

    private static Dialect createDialect(GenericRmSessionFactory sessionFactory, Database sqlDialect) {
        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(sqlDialect, sessionFactory);
                break;
            case Postgre:
            case Oracle:
            default:
                throw new IllegalArgumentException();
        }
        return dialect;
    }
}
