package io.army.dialect;

import io.army.dialect.ddl.TableDDL;
import io.army.dialect.dml.TableDML;
import io.army.dialect.dql.TableDQL;
import io.army.dialect.tcl.DialectTCL;

import javax.annotation.Nonnull;

/**
 * A common interface to all dialect of database.
 * created  on 2019-02-22.
 */
public interface Dialect extends TableDDL, TableDML, TableDQL, DialectTCL, Func {

    @Nonnull
    String name();


    boolean supportZoneId();



}
