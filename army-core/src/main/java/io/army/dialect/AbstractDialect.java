package io.army.dialect;


import io.army.GenericRmSessionFactory;
import io.army.UnKnownTypeException;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

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

    private final DDL ddl;

    private final DML dml;

    private final DQL dql;

    private final MappingContext mappingContext;


    protected AbstractDialect(GenericRmSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        Assert.isTrue(sessionFactory.actualDatabase() != this.database()
                , () -> String.format("session actual Database[%s] and dialect Database[%s] not match."
                        , sessionFactory.actualDatabase(), this.database()));

        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.ddl = createTableDDL();
        this.dml = createDML();
        this.dql = createDQL();

        this.mappingContext = new MappingContextImpl(this.sessionFactory.zoneId(), this.database());
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
        if (!StringUtils.hasText(text)) {
            return false;
        }
        return this.keywords.contains(text.toUpperCase());
    }

    @Override
    public final String showSQL(SQLWrapper sqlWrapper) {
        String sql;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            sql = ((SimpleSQLWrapper) sqlWrapper).sql();
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            sql = childSQLWrapper.parentWrapper().sql();
            sql += "\n";
            sql += childSQLWrapper.childWrapper().sql();
        } else {
            throw new UnKnownTypeException(sqlWrapper);
        }
        return sql;
    }

    @Override
    public final ZoneId zoneId() {
        return sessionFactory.zoneId();
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

    /*################################## blow DQL method ##################################*/

    @Override
    public final SimpleSQLWrapper select(Select select, Visible visible) {
        return this.dql.select(select, visible);
    }

    @Override
    public final void select(Select select, SQLContext original) {
        this.dql.select(select, original);
    }


    @Override
    public final void subQuery(SubQuery subQuery, SQLContext original) {
        this.dql.subQuery(subQuery, original);
    }

    /*################################## blow DML method ##################################*/

    @Override
    public final List<SQLWrapper> valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible) {
        return this.dml.valueInsert(insert, domainIndexSet, visible);
    }

    @Override
    public final SQLWrapper returningInsert(Insert insert, Visible visible) {
        return this.dml.returningInsert(insert, visible);
    }

    @Override
    public final SQLWrapper subQueryInsert(Insert insert, Visible visible) {
        return this.dml.subQueryInsert(insert, visible);
    }

    @Override
    public final SQLWrapper update(Update update, Visible visible) {
        return this.dml.update(update, visible);
    }

    @Override
    public final SQLWrapper delete(Delete delete, Visible visible) {
        return this.dml.delete(delete, visible);
    }

    @Override
    public Database database() {
        throw new UnsupportedOperationException();
    }



    /*####################################### below DQL  method #################################*/


    /*####################################### below protected template method #################################*/

    /**
     * @return must a modifiable Set,then every element is uppercase
     */
    protected abstract Set<String> createKeywordsSet();

    protected abstract String doQuote(String identifier);

    protected abstract DDL createTableDDL();

    protected abstract DML createDML();

    protected abstract DQL createDQL();

    /*############################### sub class override method ####################################*/

    @Override
    public String toString() {
        return String.valueOf(database());
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
        public Database sqlDialect() {
            return sqlDialect;
        }
    }
}
