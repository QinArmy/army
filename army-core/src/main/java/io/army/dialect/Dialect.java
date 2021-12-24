package io.army.dialect;

import io.army.DialectMode;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.session.GenericRmSessionFactory;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

/**
 * A common interface to all dialect of dialect.
 */
public interface Dialect {


    Stmt insert(Insert insert, Visible visible);

    Stmt update(Update update, Visible visible);

    Stmt delete(Delete delete, Visible visible);

    SimpleStmt select(Select select, Visible visible);

    void select(Select select, _SqlContext original);


    void subQuery(SubQuery subQuery, _SqlContext original);

    List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix);

    List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> addFieldMetas);

    List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> changeFieldMetas);

    List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<IndexMeta<?>> indexMetas);

    List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<String> indexNames);

    List<String> modifyTableComment(TableMeta<?> tableMeta, @Nullable String tableSuffix);

    List<String> modifyColumnComment(FieldMeta<?, ?> fieldMeta, @Nullable String tableSuffix);

    /**
     * performance after {@link GenericRmSessionFactory}  initializing .
     */
    void clearForDDL();


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

    @Deprecated
    ZoneId zoneId();

    boolean supportZone();

    boolean supportOnlyDefault();

    GenericRmSessionFactory sessionFactory();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();


    String literal(ParamMeta paramMeta, Object value);


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
