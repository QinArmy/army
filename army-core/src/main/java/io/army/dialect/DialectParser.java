package io.army.dialect;

import io.army.criteria.*;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
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

    Stmt update(UpdateStatement update, boolean useMultiStmt, Visible visible);

    Stmt delete(DeleteStatement delete, boolean useMultiStmt, Visible visible);

    Stmt select(SelectStatement select, boolean useMultiStmt, Visible visible);

    Stmt values(Values values, Visible visible);


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


    boolean isKeyWords(String words);

    ServerMeta serverMeta();

    StringBuilder identifier(String identifier, StringBuilder builder);


    String identifier(String identifier);

    void typeName(MappingType type, StringBuilder sqlBuilder);


    Dialect dialect();


    String printStmt(Stmt stmt, boolean beautify);

    String sqlElement(SQLElement element);


}
