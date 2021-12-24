package io.army.dialect;

import io.army.DialectMode;
import io.army.stmt.Stmt;

/**
 * A common interface to all dialect of dialect.
 */
public interface Dialect extends DdlDialect, DmlDialect, DqlDialect, TclDialect {


    String quoteIfNeed(String identifier);


    String showSQL(Stmt stmt);

    boolean supportSavePoint();

    boolean setClauseTableAlias();

    /**
     * @return always same a instance.
     */
    MappingContext mappingContext();

    DialectMode mode();

    String defaultFuncName();

    boolean multiTableUpdateChild();

}
