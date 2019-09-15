package io.army.dialect;


import io.army.dialect.ddl.TableDDL;
import io.army.dialect.dml.TableDML;
import io.army.dialect.dql.TableDQL;
import io.army.dialect.tcl.DialectTCL;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * this class is abstract implementation of {@link Dialect} .
 * created  on 2018/10/21.
 */
public abstract class AbstractDialect implements Dialect {


    @Nonnull
    @Override
    public final String tableDefinition(TableMeta<?> tableMeta) {
        return tableDDL().tableDefinition(tableMeta);
    }

    @Nonnull
    @Override
    public final String addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return tableDDL().addColumn(tableMeta, addFieldMetas);
    }

    @Nonnull
    @Override
    public final String modifyColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return tableDDL().modifyColumn(tableMeta, addFieldMetas);
    }

    @Nonnull
    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean supportZoneId() {
        return false;
    }

    @Override
    public String now() {
        return null;
    }

    @Override
    public String now(int precision) {
        return null;
    }

    @Override
    public String currentDate() {
        return null;
    }

    @Override
    public String currentTime() {
        return null;
    }

    @Override
    public String currentTime(int precision) {
        return null;
    }




    /*####################################### below protected template method #################################*/


    protected abstract TableDDL tableDDL();

    protected abstract TableDML tableDML();

    protected abstract TableDQL tableDQL();

    protected abstract DialectTCL dialectTcl();

    protected abstract Func func();



    /*############################### sub class override method ####################################*/


}
