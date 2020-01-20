package io.army.dialect;

import io.army.dialect.tcl.DialectTCL;

import javax.annotation.Nonnull;

/**
 * A common interface to all dialect of database.
 * created  on 2019-02-22.
 */
public interface Dialect extends  TableDML, TableDQL, DialectTCL, Func {

    @Nonnull
    String name();


    boolean supportZoneId();



}
