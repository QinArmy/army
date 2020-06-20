package io.army.dialect;

import io.army.wrapper.SQLWrapper;

/**
 * A common interface to all dialect of dialect.
 * created  on 2019-02-22.
 */
public interface Dialect extends DDL, DML, DQL {

    String showSQL(SQLWrapper sqlWrapper);

    /**
     * @return always same a instance.
     */
    MappingContext mappingContext();

}
