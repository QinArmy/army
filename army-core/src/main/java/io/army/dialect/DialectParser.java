package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.schema._SchemaResult;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.tx.Isolation;

import java.util.List;

/**
 * A common interface to all dialect of dialect.
 */
public interface DialectParser {


    /**
     * @return possibly below type:
     * <ul>
     *     <li>{@link SimpleStmt}</li>
     *     <li>{@link io.army.stmt.GeneratedKeyStmt}</li>
     *     <li>{@link io.army.stmt.PairStmt}</li>
     * </ul>
     */
    Stmt insert(InsertStatement insert, Visible visible);

    Stmt update(UpdateStatement update, Visible visible);

    Stmt delete(DeleteStatement delete, Visible visible);

    SimpleStmt select(Select select, Visible visible);

    SimpleStmt values(Values values, Visible visible);


    void subQuery(SubQuery query, _SqlContext original);


    default Stmt dialectDml(DmlStatement statement, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default Stmt dialectDql(DqlStatement statement, Visible visible) {
        throw new UnsupportedOperationException();
    }




    List<String> startTransaction(Isolation isolation, boolean readonly);


    default List<String> schemaDdl(_SchemaResult schemaResult) {
        throw new UnsupportedOperationException();
    }



    default StringBuilder identifier(String identifier, StringBuilder builder) {
       throw new UnsupportedOperationException();
    }


    String identifier(String identifier);


    Dialect dialect();


    String printStmt(Stmt stmt, boolean beautify);


}
