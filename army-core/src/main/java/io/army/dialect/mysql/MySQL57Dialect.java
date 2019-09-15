package io.army.dialect.mysql;


import io.army.dialect.Dialect;
import io.army.dialect.ddl.TableDDL;
import io.army.dialect.dml.TableDML;
import io.army.dialect.dql.TableDQL;
import io.army.dialect.tcl.DialectTCL;

import javax.annotation.Nonnull;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL 5.7 Dialect
 * created  on 2018/10/21.
 */
public class MySQL57Dialect extends MySQLDialect {

    public static final MySQL57Dialect INSTANCE = new MySQL57Dialect();


    private final TableDDL tableDDL;

    private final TableDML tableDML;

    private final TableDQL tableDQL;

    private final DialectTCL dialectTCL;

    private final MySQLFunc mySQLFunc;

    MySQL57Dialect() {
        this.tableDDL = new MySQL57TableDDL(this);
        this.tableDML = new MySQLTableDML(this);
        this.tableDQL = new MySQLTableDQL(this);

        this.dialectTCL = new MySQLDialectTCL(this);
        this.mySQLFunc = new MySqlFuncImpl(this);
    }

    @Override
    protected TableDDL tableDDL() {
        return this.tableDDL;
    }

    @Override
    protected TableDML tableDML() {
        return this.tableDML;
    }

    @Override
    protected TableDQL tableDQL() {
        return this.tableDQL;
    }

    @Override
    protected DialectTCL dialectTcl() {
        return this.dialectTCL;
    }

    @Override
    protected MySQLFunc func() {
        return this.mySQLFunc;
    }


    @Nonnull
    @Override
    public String name() {
        return "MySQL57";
    }
}
