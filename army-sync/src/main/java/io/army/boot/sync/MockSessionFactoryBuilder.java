package io.army.boot.sync;

import io.army.GenericSessionFactory;
import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.Environment;
import io.army.sync.SessionFactoryAdvice;

import java.util.Collection;
import java.util.List;

final class MockSessionFactoryBuilder extends AbstractSyncSessionFactoryBuilder {


    @Override
    public GenericFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        return null;
    }

    @Override
    public GenericFactoryBuilder name(String sessionFactoryName) {
        return null;
    }

    @Override
    public GenericFactoryBuilder environment(Environment environment) {
        return null;
    }

    @Override
    public GenericFactoryBuilder factoryAdvice(List<SessionFactoryAdvice> factoryAdviceList) {
        return null;
    }

    private static Dialect createDialect(GenericSessionFactory sessionFactory, Database sqlDialect) {
        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(sqlDialect, sessionFactory);
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException();
        }
        return dialect;
    }
}
