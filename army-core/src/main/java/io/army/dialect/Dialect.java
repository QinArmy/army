package io.army.dialect;

import io.army.DialectMode;
import io.army.meta.FieldMeta;
import io.army.stmt.Stmt;

/**
 * A common interface to all dialect of dialect.
 */
public interface Dialect extends DdlDialect, DmlDialect, DqlDialect, TclDialect {

    String showSQL(Stmt stmt);

    boolean supportSavePoint();

    /**
     * @return always same a instance.
     */
    MappingContext mappingContext();

    DialectMode mode();

    void defaultFunc(FieldMeta<?, ?> field, StringBuilder builder);

}
