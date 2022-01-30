package io.army.dialect;

import io.army.Database;
import io.army.criteria.*;
import io.army.meta.ParamMeta;
import io.army.schema._SchemaResult;
import io.army.session.DialectSessionFactory;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.util.List;

/**
 * A common interface to all dialect of dialect.
 */
public interface _Dialect {


    Stmt insert(Insert insert, Visible visible);

    Stmt update(Update update, Visible visible);

    Stmt delete(Delete delete, Visible visible);

    SimpleStmt select(Select select, Visible visible);

    void select(Select select, _SqlContext original);


    void subQuery(SubQuery subQuery, _SqlContext original);


    default List<String> schemaDdl(_SchemaResult schemaResult) {
        throw new UnsupportedOperationException();
    }


    /**
     * performance after {@link DialectSessionFactory}  initializing .
     */
    void clearForDDL();

    boolean supportInsertReturning();


    @Deprecated
    default String safeTableName(String tableName) {
        throw new UnsupportedOperationException();
    }


    default StringBuilder safeObjectName(String tableName, StringBuilder builder) {
        return builder;
    }

    /**
     * design for standard statement.
     */
    @Deprecated
    default String safeObjectName(String columnName) {
        throw new UnsupportedOperationException();
    }


    boolean isKeyWord(String identifier);

    boolean supportZone();

    boolean supportOnlyDefault();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();


    String literal(ParamMeta paramMeta, Object nonNull);


    String quoteIfNeed(String identifier);

    default StringBuilder quoteIfNeed(String identifier, StringBuilder builder) {
        return builder;
    }


    String showSQL(Stmt stmt);

    boolean supportSavePoint();

    boolean setClauseTableAlias();

    Dialect dialect();

    String defaultFuncName();

    boolean multiTableUpdateChild();

}
