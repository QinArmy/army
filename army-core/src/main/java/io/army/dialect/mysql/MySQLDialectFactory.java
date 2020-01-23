package io.army.dialect.mysql;

import io.army.SessionFactory;
import io.army.dialect.DataBase;

public abstract class MySQLDialectFactory {

    static MySQLDialectFactory decideDialect(DataBase dataBase, SessionFactory sessionFactory) {
       return null;
    }
}
