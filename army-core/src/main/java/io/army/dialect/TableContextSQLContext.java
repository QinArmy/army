package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

public interface TableContextSQLContext extends SQLContext {

    Visible visible();

    Dialect dialect();

    TableContext tableContext();

    void appendParentOf(ChildTableMeta<?> tableMeta);

    List<ParamWrapper> paramList();

    SimpleSQLWrapper build();

    @Nullable
    TableContextSQLContext parentContext();

}
