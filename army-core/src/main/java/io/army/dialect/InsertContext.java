package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

public interface InsertContext extends TableContextSQLContext {

    SQLBuilder fieldsBuilder();

    /**
     * if tableMeta is target table , use fieldsBuilder
     *
     * @param tableMeta {@link TableMeta} that will be append table name .
     */
    @Override
    void appendTable(TableMeta<?> tableMeta,@Nullable String tableAlias);
}
