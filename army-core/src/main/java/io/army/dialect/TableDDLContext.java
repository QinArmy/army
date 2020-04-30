package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.BiFunction;

final class TableDDLContext extends AbstractDDLContext {

    TableDDLContext(Dialect dialect, TableMeta<?> tableMeta
            , Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap) {
        super(dialect, tableMeta, defaultFunctionMap);
        // place holder for table definition
        sqlList.add("");
    }

    @Override
    protected int sqlListSize(TableMeta<?> tableMeta) {
        return tableMeta.indexCollection().size() + 1;
    }

    @Override
    protected void handleSQLList() {
        this.sqlList.set(0, sqlBuilder.toString());
    }

}
