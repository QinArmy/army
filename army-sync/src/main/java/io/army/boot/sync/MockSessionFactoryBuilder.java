package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.Environment;

import java.util.Collection;

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
