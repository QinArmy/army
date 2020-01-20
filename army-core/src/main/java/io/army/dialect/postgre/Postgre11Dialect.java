package io.army.dialect.postgre;


import io.army.dialect.Dialect;
import io.army.dialect.Func;
import io.army.dialect.TableDML;
import io.army.dialect.TableDQL;
import io.army.dialect.tcl.DialectTCL;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * this class is a  {@link Dialect} implementation and represent Postgre 11.x  Dialect
 * created  on 2018/10/21.
 */
public class Postgre11Dialect extends PostgreDialect {

    public static final Postgre11Dialect INSTANCE = new Postgre11Dialect();


    @Override
    protected TableDML tableDML() {
        return null;
    }

    @Override
    protected TableDQL tableDQL() {
        return null;
    }

    @Override
    protected DialectTCL dialectTcl() {
        return null;
    }

    @Override
    public Func func() {
        return null;
    }


}
