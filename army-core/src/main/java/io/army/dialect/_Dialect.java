package io.army.dialect;

import io.army.criteria.*;
import io.army.meta.ParamMeta;
import io.army.schema._SchemaResult;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.tx.Isolation;

import java.util.List;

/**
 * A common interface to all dialect of dialect.
 */
public interface _Dialect {


    /**
     * @return possibly below type:
     * <ul>
     *     <li>{@link SimpleStmt}</li>
     *     <li>{@link io.army.stmt.GeneratedKeyStmt}</li>
     *     <li>{@link io.army.stmt.PairStmt}</li>
     * </ul>
     */
    Stmt insert(Insert insert, Visible visible);

    Stmt update(Update update, Visible visible);

    Stmt delete(Delete delete, Visible visible);

    SimpleStmt select(Select select, Visible visible);

    void rowSet(RowSet rowSet, _SqlContext original);

    List<String> startTransaction(Isolation isolation, boolean readonly);


    default List<String> schemaDdl(_SchemaResult schemaResult) {
        throw new UnsupportedOperationException();
    }


    /**
     * <p>
     * Append  literal
     * </p>
     */
    StringBuilder literal(ParamMeta paramMeta, Object nonNull, StringBuilder sqlBuilder);


    default StringBuilder identifier(String identifier, StringBuilder builder) {
        return builder;
    }

    String identifier(String identifier);


    boolean supportSavePoint();


    boolean setClauseTableAlias();


    default boolean setClauseSupportRow() {
        throw new UnsupportedOperationException();
    }

    Dialect dialectMode();


    String printStmt(Stmt stmt, boolean beautify);


}
