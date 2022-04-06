package io.army.dialect;

import io.army.criteria.*;
import io.army.meta.ParamMeta;
import io.army.schema._SchemaResult;
import io.army.session.Database;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.tx.Isolation;

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

    List<String> startTransaction(Isolation isolation, boolean readonly);


    default List<String> schemaDdl(_SchemaResult schemaResult) {
        throw new UnsupportedOperationException();
    }


    boolean supportInsertReturning();


    default StringBuilder safeObjectName(String tableName, StringBuilder builder) {
        return builder;
    }
//
//    default String safeObjectName(String objectName){
//        throw new UnsupportedOperationException();
//    }


    boolean supportZone();

    boolean supportOnlyDefault();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();

    default boolean supportRowLeftItem() {
        return false;
    }


    String literal(ParamMeta paramMeta, Object nonNull);


    String quoteIfNeed(String identifier);

    default StringBuilder quoteIfNeed(String identifier, StringBuilder builder) {
        return builder;
    }


    boolean supportSavePoint();

    boolean setClauseTableAlias();

    Dialect dialect();

    String defaultFuncName();

    boolean multiTableUpdateChild();

}
