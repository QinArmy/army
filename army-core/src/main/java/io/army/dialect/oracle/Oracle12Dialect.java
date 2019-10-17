package io.army.dialect.oracle;

import io.army.dialect.*;
import io.army.dialect.tcl.DialectTCL;

/**
 * this class is a  {@link Dialect} implementation and represent Oracle 12g  Dialect
 * created  on 2018/10/21.
 */
public class Oracle12Dialect extends AbstractDialect {

    public static final Oracle12Dialect INSTANCE = new Oracle12Dialect();

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

    @Override
    protected DialectTCL dialectTcl() {
        return null;
    }
}
