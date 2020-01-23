package io.army.dialect.oracle;

import io.army.dialect.*;
import io.army.dialect.func.SQLFunc;
import io.army.dialect.TableDDL;
import io.army.dialect.func.SQLFuncDescribe;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation and represent Oracle 12g  Dialect
 * created  on 2018/10/21.
 */
public class Oracle12Dialect extends AbstractDialect {

    public static final Oracle12Dialect INSTANCE = new Oracle12Dialect();

    @Override
    protected Set<String> createKeywordsSet() {
        return null;
    }

    @Override
    protected Map<String, SQLFuncDescribe> createSqlFuncMap() {
        return null;
    }

    @Override
    public Map<String, List<String>> standardFunc() {
        return null;
    }

    @Override
    public DataBase database() {
        return null;
    }

    @Override
    protected TableDDL tableDDL() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean supportZoneId() {
        return false;
    }

    @Override
    public Func func() {
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

}
