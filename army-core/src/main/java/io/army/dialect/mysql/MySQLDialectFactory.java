package io.army.dialect.mysql;

import io.army.ArmyKeys;
import io.army.DialectMode;
import io.army.dialect.Dialect;
import io.army.session.GenericRmSessionFactory;
import io.army.util._Exceptions;

public abstract class MySQLDialectFactory {

    public static Dialect createDialect(GenericRmSessionFactory sessionFactory) {
        final DialectMode dialectMode;
        dialectMode = sessionFactory.environment().getOrDefault(ArmyKeys.dialectMode, DialectMode.class);
        final Dialect dialect;
        switch (dialectMode) {
            case MySQL57:
                dialect = new MySQL57Dialect(sessionFactory);
                break;
            case MYSQL80:
                dialect = new MySQL80Dialect(sessionFactory);
                break;
            default:
                throw _Exceptions.notSupportDialectMode(dialectMode, sessionFactory.serverMeta());
        }
        return dialect;
    }



}
