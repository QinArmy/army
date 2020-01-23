package io.army.dialect;


import io.army.SessionFactory;
import io.army.dialect.func.SQLFunc;
import io.army.dialect.func.SQLFuncDescribe;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.StringUtils;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.*;


/**
 * this class is abstract implementation of {@link Dialect} .
 * created  on 2018/10/21.
 */
public abstract class AbstractDialect implements Dialect {

    private final Map<String, SQLFuncDescribe> sqlFunctions ;

    /**
     * a unmodifiable Set, every element is uppercase .
     */
    private final Set<String> keywords ;

    private final SessionFactory sessionFactory;

    public AbstractDialect(SessionFactory sessionFactory) {
        this.sqlFunctions = Collections.unmodifiableMap(createSqlFuncMap());
        this.keywords = Collections.unmodifiableSet(createKeywordsSet());
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final String quoteIfNeed(String text) {
        String newText = text;
        if(isKeyWord(text)){
            newText = StringUtils.quote(newText);
        }
        return newText;
    }

    @Override
    public final boolean isKeyWord(String text) {
        return keywords.contains(text);
    }


    @Override
    public final Map<String, List<String>> standardFunc() {
        return Collections.emptyMap();
    }

    @Override
    public final ZoneId zoneId() {
        return sessionFactory.options().getZoneId();
    }


    @Override
    public boolean supportZoneId() {
        return false;
    }






    /*####################################### below protected  method #################################*/


    /*####################################### below protected template method #################################*/
    /**
     * @return must a modifiable Set,and every element is uppercase
     */
    protected abstract Set<String> createKeywordsSet();
    /**
     * @return must a modifiable map,and every key is uppercase
     */
    protected abstract Map<String,SQLFuncDescribe> createSqlFuncMap();




    /*############################### sub class override method ####################################*/


}
