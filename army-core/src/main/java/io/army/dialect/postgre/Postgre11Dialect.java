package io.army.dialect.postgre;


import io.army.dialect.*;
import io.army.dialect.func.SQLFunc;
import io.army.dialect.func.SQLFuncDescribe;

import java.util.Map;
import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation and represent Postgre 11.x  Dialect
 * created  on 2018/10/21.
 */
public class Postgre11Dialect extends PostgreDialectImpl {

    public static final Postgre11Dialect INSTANCE = new Postgre11Dialect();

    @Override
    protected Set<String> createKeywordsSet() {
        return null;
    }

    @Override
    protected Map<String, SQLFuncDescribe<?>> createSqlFuncMap() {
        return null;
    }

    @Override
    public DataBase database() {
        return null;
    }

    @Override
    protected TableDML tableDML() {
        return null;
    }

    @Override
    protected TableDQL tableDQL() {
        return null;
    }



    @Override
    public Func func() {
        return null;
    }


}
