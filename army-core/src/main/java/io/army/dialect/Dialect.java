package io.army.dialect;

import io.army.stmt.Stmt;

/**
 * A common interface to all dialect of dialect.
 */
public interface Dialect extends DDL, DmlDialect, DqlDialect, TCL {

    String showSQL(Stmt stmt);

    boolean supportSavePoint();

    /**
     * @return always same a instance.
     */
    MappingContext mappingContext();

}
