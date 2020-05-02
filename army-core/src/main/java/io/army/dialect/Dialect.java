package io.army.dialect;

import io.army.wrapper.BatchSQLWrapper;
import io.army.wrapper.DomainBatchSQLWrapper;
import io.army.wrapper.SQLWrapper;

/**
 * A common interface to all dialect of dialect.
 * created  on 2019-02-22.
 */
public interface Dialect extends DDL, DML, DQL {

    String showSQL(SQLWrapper sqlWrapper);

    String showSQL(BatchSQLWrapper sqlWrapper);

    String showSQL(DomainBatchSQLWrapper sqlWrapper);

}
