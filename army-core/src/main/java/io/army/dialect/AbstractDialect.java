package io.army.dialect;


import io.army.SessionFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
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
    private final Set<String> keywords ;

    protected final SessionFactory sessionFactory;

    private final TableDDL tableDDL;

    private final DML DML;

    private final DQL DQL;



    public AbstractDialect(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");

        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.tableDDL = createTableDDL();
        this.DML = createTableDML();
        this.DQL = createTableDQL();
    }

    @Override
    public final String quoteIfNeed(String identifier) {
        String newIdentifier = identifier;
        if(isKeyWord(identifier)){
            newIdentifier = doQuote(identifier);
        }
        return newIdentifier;
    }

    @Override
    public final boolean isKeyWord(String text) {
        if(!StringUtils.hasText(text)){
            return false;
        }
        return keywords.contains(text.toUpperCase());
    }


    @Override
    public final ZoneId zoneId() {
        return sessionFactory.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public boolean supportZoneId() {
        return false;
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


    /*################################## blow TableDML method ##################################*/

    @Override
    public final List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        return DML.insert(tableMeta, entityWrapper);
    }

    @Override
    public final List<SQLWrapper> update(Update update, Visible visible) {
        return DML.update(update, visible);
    }

    @Override
    public List<SQLWrapper> delete(Delete.DeleteAble deleteAble, Visible visible) {
        return DML.delete(deleteAble, visible);
    }

    @Override
    public  SQLDialect sqlDialect() {
        throw new UnsupportedOperationException();
    }

    /*####################################### below protected  method #################################*/


    /*####################################### below protected template method #################################*/
    /**
     * @return must a modifiable Set,then every element is uppercase
     */
    protected abstract Set<String> createKeywordsSet();

    protected abstract String doQuote(String identifier);

   protected abstract TableDDL createTableDDL() ;

    protected abstract DML createTableDML();

    protected abstract DQL createTableDQL();

    /*############################### sub class override method ####################################*/

    @Override
    public String toString() {
        return String.valueOf(sqlDialect());
    }
}
