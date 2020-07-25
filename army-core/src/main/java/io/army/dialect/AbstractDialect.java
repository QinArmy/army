package io.army.dialect;


import io.army.GenericRmSessionFactory;
import io.army.GenericSessionFactory;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
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
        Assert.isTrue(sessionFactory.actualSQLDialect() != this.sqlDialect()
                , () -> String.format("session actual SQLDialect[%s] and dialect SQLDialect[%s] not match."
                        , sessionFactory.actualSQLDialect(), this.sqlDialect()));

        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.ddl = createTableDDL();
        this.dml = createDML();
        this.dql = createDQL();

        this.mappingContext = new MappingContextImpl(this.sessionFactory.zoneId(), this.sqlDialect());
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
    public final ZoneId zoneId() {
        return sessionFactory.zoneId();
    }

    @Override
    public final GenericSessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public final MappingContext mappingContext() {
        return this.mappingContext;
    }

    /*################################## blow DDL method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta) {
        return ddl.createTable(tableMeta);
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return ddl.addColumn(tableMeta, addFieldMetas);
    }

    @Override
    public final List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas) {
        return ddl.changeColumn(tableMeta, changeFieldMetas);
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas) {
        return ddl.addIndex(tableMeta, indexMetas);
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        return ddl.dropIndex(tableMeta, indexNames);
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
    public final SQLWrapper subQueryInsert(Insert insert, Visible visible) {
        return this.dml.subQueryInsert(insert, visible);
    }

    @Override
    public final SQLWrapper simpleUpdate(Update update, Visible visible) {
        return this.dml.simpleUpdate(update, visible);
    }

    @Override
    public final List<SQLWrapper> batchUpdate(Update update, @Nullable Set<Integer> namedParamIexSet, Visible visible) {
        return this.dml.batchUpdate(update, namedParamIexSet, visible);
    }

    @Override
    public final SQLWrapper simpleDelete(Delete delete, Visible visible) {
        return this.dml.simpleDelete(delete, visible);
    }

    @Override
    public final List<SQLWrapper> batchDelete(Delete delete, @Nullable Set<Integer> namedParamIexSet, Visible visible) {
        return this.dml.batchDelete(delete, namedParamIexSet, visible);
    }

    @Override
    public SQLDialect sqlDialect() {
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
        return String.valueOf(sqlDialect());
    }


    private static final class MappingContextImpl implements MappingContext {

        private final ZoneId zoneId;
        private final SQLDialect sqlDialect;

        private MappingContextImpl(ZoneId zoneId, SQLDialect sqlDialect) {
            this.zoneId = zoneId;
            this.sqlDialect = sqlDialect;
        }

        @Override
        public ZoneId zoneId() {
            return this.zoneId;
        }

        @Override
        public SQLDialect sqlDialect() {
            return sqlDialect;
        }
    }
}
