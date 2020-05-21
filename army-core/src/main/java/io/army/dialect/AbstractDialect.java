package io.army.dialect;


import io.army.GenericSessionFactory;
import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.wrapper.BatchSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SelectSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * this class is abstract implementation of {@link Dialect} .
 * created  on 2018/10/21.
 */
public abstract class AbstractDialect implements InnerDialect {

    /**
     * a unmodifiable Set, every element is uppercase .
     */
    private final Set<String> keywords;

    protected final GenericSessionFactory sessionFactory;

    private final DDL tableDDL;

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

    /*################################## blow DDL method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta) {
        return tableDDL.createTable(tableMeta);
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
    public final List<SelectSQLWrapper> select(Select select, Visible visible) {
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
    public final List<SQLWrapper> insert(Insert insert, Visible visible) {
        return this.dml.insert(insert, visible);
    }

    @Override
    public final List<BatchSQLWrapper> batchInsert(Insert insert, Visible visible) {
        return this.dml.batchInsert(insert, visible);
    }

    @Override
    public final SQLWrapper update(Update update, Visible visible) {
        return this.dml.update(update, visible);
    }

    @Override
    public final List<SimpleSQLWrapper> delete(Delete delete, Visible visible) {
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

    protected abstract DDL createTableDDL();

    protected abstract DML createDML();

    protected abstract DQL createDQL();

    /*############################### sub class override method ####################################*/

    @Override
    public String toString() {
        return String.valueOf(sqlDialect());
    }
}
