package io.army.dialect;

/**
 * A common interface to all dialect of dialect.
 * created  on 2019-02-22.
 */
public interface Dialect extends TableDDL, DML, DQL {

    SQLDialect sqlDialect();


    boolean supportZoneId();

}
