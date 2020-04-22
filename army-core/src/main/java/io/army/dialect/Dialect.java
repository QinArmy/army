package io.army.dialect;

/**
 * A common interface to all dialect of dialect.
 * created  on 2019-02-22.
 */
public interface Dialect extends TableDDL, DML, DQL {

    SQLDialect sqlDialect();

    String format(SQLWrapper sqlWrapper);

    String format(BatchSQLWrapper sqlWrapper);


}
