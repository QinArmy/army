package io.army.dialect;

import io.army.Dialect;
import io.army.criteria.*;
import io.army.meta.ParamMeta;
import io.army.session.DialectSessionFactory;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

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


//    List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix);
//
//    List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
//            , Collection<FieldMeta<?, ?>> addFieldMetas);
//
//    List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
//            , Collection<FieldMeta<?, ?>> changeFieldMetas);
//
//    List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
//            , Collection<IndexMeta<?>> indexMetas);
//
//    List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
//            , Collection<String> indexNames);
//
//    List<String> modifyTableComment(TableMeta<?> tableMeta, @Nullable String tableSuffix);
//
//    List<String> modifyColumnComment(FieldMeta<?, ?> fieldMeta, @Nullable String tableSuffix);

    /**
     * performance after {@link DialectSessionFactory}  initializing .
     */
    void clearForDDL();

    boolean supportInsertReturning();


    default String safeTableName(String tableName) {
        throw new UnsupportedOperationException();
    }

    /**
     * design for standard statement.
     */
    default String safeColumnName(String columnName) {
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


    String showSQL(Stmt stmt);

    boolean supportSavePoint();

    boolean setClauseTableAlias();

    Dialect mode();

    String defaultFuncName();

    boolean multiTableUpdateChild();

}
