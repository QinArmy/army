package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

public interface TableContextSQLContext extends SQLContext {

    /**
     * <p>
     * the key of sharding  {@link TableMeta} in same database.
     * </p>
     *
     * @param tableMeta {@link TableMeta} that will be append table name .
     */
    void appendTable(TableMeta<?> tableMeta,@Nullable  String tableAlias);

    Visible visible();

    Dialect dialect();

    TableContext tableContext();

    TableContext primaryTableContext();

    @Nullable
    TableContext parentTableContext();

    String primaryRouteSuffix();

    void appendParentOf(ChildTableMeta<?> tableMeta);

    List<ParamWrapper> paramList();

    SimpleSQLWrapper build();

    @Nullable
    TableContextSQLContext parentContext();





}
