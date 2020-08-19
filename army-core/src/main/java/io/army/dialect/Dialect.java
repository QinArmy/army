package io.army.dialect;

import io.army.wrapper.SQLWrapper;

/**
 * A common interface to all dialect of dialect.
 */
public interface Dialect extends DDL, DML, DQL, TCL {

    String showSQL(SQLWrapper sqlWrapper);

    boolean supportSavePoint();

    /**
     * @return always same a instance.
     */
    MappingContext mappingContext();

}
