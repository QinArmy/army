package io.army.dialect;


import io.army.SessionFactory;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.time.ZoneId;
import java.util.*;


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

    private final TableDML tableDML;

    private final TableDQL tableDQL;



    public AbstractDialect(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory,"sessionFactory required");

        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;

        this.tableDDL = createTableDDL();
        this.tableDML = createTableDML();
        this.tableDQL = createTableDQL();
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
        return sessionFactory.options().zoneId();
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




    /*####################################### below protected  method #################################*/


    /*####################################### below protected template method #################################*/
    /**
     * @return must a modifiable Set,and every element is uppercase
     */
    protected abstract Set<String> createKeywordsSet();

    protected abstract String doQuote(String identifier);

   protected abstract TableDDL createTableDDL() ;

    protected abstract TableDML createTableDML() ;

    protected abstract TableDQL createTableDQL() ;

    /*############################### sub class override method ####################################*/


}
