package io.army.dialect;


import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.session.GenericRmSessionFactory;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * this class is abstract implementation of {@link Dialect} .
 */
public abstract class AbstractDialect implements InnerDialect {

    /**
     * a unmodifiable Set, every element is uppercase .
     */
    private final Set<String> keywords;

    protected final GenericRmSessionFactory sessionFactory;

    private final DdlDialect ddl;

    private final DmlDialect dml;

    private final DqlDialect dql;

    private final TclDialect tcl;

    private final MappingContext mappingContext;


    protected AbstractDialect(GenericRmSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.ddl = createDDL();
        this.dml = createDML();
        this.dql = createDQL();
        this.tcl = createTCL();
        assertDatabaseMatch();
        this.mappingContext = new MappingContextImpl(this.sessionFactory.zoneOffset(), this.database());
    }

    @Override
    public final String quoteIfNeed(String identifier) {
        String newIdentifier = identifier;
        if (isKeyWord(identifier)) {
            newIdentifier = doQuote(identifier);
        }
        return newIdentifier;
    }

    @Override
    public final boolean isKeyWord(String text) {
        return StringUtils.hasText(text) && this.keywords.contains(text.toUpperCase());
    }

    @Override
    public final String showSQL(Stmt stmt) {
        String sql;
        if (stmt instanceof SimpleStmt) {
            sql = ((SimpleStmt) stmt).sql();
        } else if (stmt instanceof PairStmt) {
            PairStmt childSQLWrapper = (PairStmt) stmt;
            sql = childSQLWrapper.parentStmt().sql();
            sql += "\n";
            sql += childSQLWrapper.childStmt().sql();
        } else {
            throw new UnKnownTypeException(stmt);
        }
        return sql;
    }

    @Override
    public final ZoneId zoneId() {
        return sessionFactory.zoneOffset();
    }

    @Override
    public final GenericRmSessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public final MappingContext mappingContext() {
        return this.mappingContext;
    }

    /*################################## blow DDL method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        return ddl.createTable(tableMeta, tableSuffix);
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> addFieldMetas) {
        return ddl.addColumn(tableMeta, tableSuffix, addFieldMetas);
    }

    @Override
    public final List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> changeFieldMetas) {
        return ddl.changeColumn(tableMeta, tableSuffix, changeFieldMetas);
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<IndexMeta<?>> indexMetas) {
        return ddl.addIndex(tableMeta, tableSuffix, indexMetas);
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<String> indexNames) {
        return ddl.dropIndex(tableMeta, tableSuffix, indexNames);
    }

    @Override
    public final List<String> modifyTableComment(TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        return this.ddl.modifyTableComment(tableMeta, tableSuffix);
    }

    @Override
    public final List<String> modifyColumnComment(FieldMeta<?, ?> fieldMeta, @Nullable String tableSuffix) {
        return this.ddl.modifyColumnComment(fieldMeta, tableSuffix);
    }

    @Override
    public final void clearForDDL() {
        this.ddl.clearForDDL();
    }

    /*################################## blow DQL method ##################################*/

    @Override
    public final SimpleStmt select(Select select, Visible visible) {
        return this.dql.select(select, visible);
    }

    @Override
    public final void select(Select select, _SqlContext original) {
        this.dql.select(select, original);
    }


    @Override
    public final void subQuery(SubQuery subQuery, _SqlContext original) {
        this.dql.subQuery(subQuery, original);
    }

    /*################################## blow DML method ##################################*/

    @Override
    public final Stmt valueInsert(Insert insert, Visible visible) {
        return this.dml.insert(insert, visible);
    }

    @Override
    public final Stmt returningInsert(Insert insert, Visible visible) {
        return this.dml.returningInsert(insert, visible);
    }


    @Override
    public final Stmt update(Update update, Visible visible) {
        return this.dml.update(update, visible);
    }

    @Override
    public final Stmt delete(Delete delete, Visible visible) {
        return this.dml.delete(delete, visible);
    }



    /*####################################### below protected template method #################################*/

    /**
     * @return must a modifiable Set,then every element is uppercase
     */
    protected abstract Set<String> createKeywordsSet();

    protected abstract String doQuote(String identifier);

    protected abstract DdlDialect createDDL();

    protected abstract DmlDialect createDML();

    protected abstract DqlDialect createDQL();

    protected abstract TclDialect createTCL();

    /*############################### sub class override method ####################################*/

    @Override
    public final String toString() {
        return database().name();
    }

    /*################################## blow private method ##################################*/

    private void assertDatabaseMatch() {
        Database database = this.ddl.database();
        if (this.dml.database() != database || this.dql.database() != database || this.tcl.database() != database) {
            throw new IllegalStateException(
                    String.format("ddl database[%s] , dml database[%s] ,dql database[%s] , tcl database[%s] not match."
                            , this.ddl.database(), this.dml.database(), this.dql.database(), this.tcl.database()));
        }
    }


    private static final class MappingContextImpl implements MappingContext {

        private final ZoneId zoneId;
        private final Database sqlDialect;

        private MappingContextImpl(ZoneId zoneId, Database sqlDialect) {
            this.zoneId = zoneId;
            this.sqlDialect = sqlDialect;
        }

        @Override
        public ZoneId zoneId() {
            return this.zoneId;
        }

        @Override
        public Database database() {
            return sqlDialect;
        }
    }
}
