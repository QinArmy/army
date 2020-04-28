package io.army.dialect;


import io.army.GenericSessionFactory;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * this class is abstract implementation of {@link Dialect} .
 * created  on 2018/10/21.
 */
public abstract class AbstractDialect implements Dialect {

    /**
     * a unmodifiable Set, every element is uppercase .
     */
    private final Set<String> keywords;

    protected final GenericSessionFactory sessionFactory;

    private final TableDDL tableDDL;

    private final DML dml;

    private final DQL dql;


    public AbstractDialect(GenericSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");

        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.tableDDL = createTableDDL();
        this.dml = createDML();
        this.dql = createDQL();
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
        return keywords.contains(text.toUpperCase());
    }


    @Override
    public final ZoneId zoneId() {
        return sessionFactory.zoneId();
    }

    @Override
    public final GenericSessionFactory sessionFactory() {
        return sessionFactory;
    }


    /*################################## blow TableDDL method ##################################*/

    @Override
    public final List<String> tableDefinition(TableMeta<?> tableMeta) {
        return tableDDL.tableDefinition(tableMeta);
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return tableDDL.addColumn(tableMeta, addFieldMetas);
    }

    @Override
    public final List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas) {
        return tableDDL.changeColumn(tableMeta, changeFieldMetas);
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas) {
        return tableDDL.addIndex(tableMeta, indexMetas);
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        return tableDDL.dropIndex(tableMeta, indexNames);
    }

    /*################################## blow DQL method ##################################*/

    @Override
    public final List<SQLWrapper> select(Select select, Visible visible) {
        return this.dql.select(select, visible);
    }

    @Override
    public final void select(Select select, SQLContext originalContext) {
        this.dql.select(select, originalContext);
    }

    @Override
    public final void partSelect(PartQuery select, SQLContext originalContext) {
        this.dql.partSelect(select, originalContext);
    }

    @Override
    public final void partSubQuery(PartQuery subQuery, SQLContext originalContext) {
        this.dql.partSubQuery(subQuery, originalContext);
    }

    @Override
    public final void subQuery(SubQuery subQuery, SQLContext originalContext) {
        this.dql.subQuery(subQuery, originalContext);
    }

    /*################################## blow DML method ##################################*/

    @Override
    public final List<SQLWrapper> insert(IDomain domain) {
        return this.dml.insert(domain);
    }

    @Override
    public final List<SQLWrapper> insert(Insert insert, Visible visible) {
        return this.dml.insert(insert, visible);
    }

    @Override
    public final List<BatchSQLWrapper> batchInsert(Insert insert, Visible visible) {
        return this.dml.batchInsert(insert, visible);
    }

    @Override
    public final List<SQLWrapper> update(Update update, Visible visible) {
        return this.dml.update(update, visible);
    }

    @Override
    public final List<SQLWrapper> delete(Delete delete, Visible visible) {
        return this.dml.delete(delete, visible);
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

    protected abstract TableDDL createTableDDL();

    protected abstract DML createDML();

    protected abstract DQL createDQL();

    /*############################### sub class override method ####################################*/

    @Override
    public String toString() {
        return String.valueOf(sqlDialect());
    }
}
