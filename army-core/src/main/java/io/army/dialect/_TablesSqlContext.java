package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;

import java.util.List;

public interface _TablesSqlContext extends _SqlContext {

    /**
     * <p>
     * the key of sharding  {@link TableMeta} in same database.
     * </p>
     *
     * @param tableMeta {@link TableMeta} that will be append table name .
     */
    void appendTable(TableMeta<?> tableMeta, @Nullable String tableAlias);

    Visible visible();

    Dialect dialect();

    TablesContext tableContext();

    TablesContext primaryTableContext();

    @Nullable
    TablesContext parentTableContext();

    String primaryRouteSuffix();

    void appendParentOf(ChildTableMeta<?> childMeta,String childAlias);

    List<ParamValue> paramList();

    SimpleStmt build();

    @Nullable
    _TablesSqlContext parentContext();





}
