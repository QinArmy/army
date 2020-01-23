package io.army.dialect.mysql;


import io.army.SessionFactory;
import io.army.dialect.*;
import io.army.dialect.TableDDL;
import io.army.dialect.func.SQLFuncDescribe;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL 5.7 Dialect
 * created  on 2018/10/21.
 */
class MySQL57Dialect extends AbstractDialect  {

    private final TableDDL tableDDL;

    private final TableDML tableDML;

    private final TableDQL tableDQL;

    MySQL57Dialect(SessionFactory sessionFactory) {
        super(sessionFactory);

        this.tableDDL = new MySQL57TableDDL(this);
        this.tableDML = null;
        this.tableDQL = null;

    }

    /*################################## blow interfaces method ##################################*/

    @Override
    public List<String> tableDefinition(TableMeta<?> tableMeta) {
        return tableDDL.tableDefinition(tableMeta);
    }

    @Override
    public List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return tableDDL.addColumn(tableMeta,addFieldMetas);
    }

    @Override
    public List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas) {
        return tableDDL.changeColumn(tableMeta,changeFieldMetas);
    }

    @Override
    public List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas) {
        return tableDDL.addIndex(tableMeta,indexMetas);
    }

    @Override
    public List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        return tableDDL.dropIndex(tableMeta,indexNames);
    }

    @Override
    public DataBase database() {
        return DataBase.MySQL57;
    }


    /*####################################### below protected  method #################################*/

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLUtils.create57KeywordsSet();
    }

    @Override
    protected Map<String, SQLFuncDescribe> createSqlFuncMap() {
        return null;
    }

}
