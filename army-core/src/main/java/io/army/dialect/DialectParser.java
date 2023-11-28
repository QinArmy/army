package io.army.dialect;

import io.army.criteria.*;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.schema._SchemaResult;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.Consumer;

/**
 * A common interface to all dialect of dialect.
 */
public interface DialectParser {


    /**
     * @return one of :
     * <ul>
     *     <li>{@link SimpleStmt}</li>
     *     <li>{@link io.army.stmt.GeneratedKeyStmt}</li>
     *     <li>{@link io.army.stmt.PairStmt}</li>
     * </ul>
     */
    Stmt insert(InsertStatement insert, Visible visible);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt update(UpdateStatement update, boolean useMultiStmt, Visible visible);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt delete(DeleteStatement delete, boolean useMultiStmt, Visible visible);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt select(SelectStatement select, boolean useMultiStmt, Visible visible);

    Stmt values(Values values, Visible visible);


    default Stmt dialectDml(DmlStatement statement, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default Stmt dialectDql(DqlStatement statement, Visible visible) {
        throw new UnsupportedOperationException();
    }



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

    void printStmt(Stmt stmt, boolean beautify, Consumer<String> appender);

    String sqlElement(SQLElement element);


}
