package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.BiFunction;

final class ColumnDDLContext extends AbstractDDLContext {

    ColumnDDLContext(Dialect dialect, TableMeta<?> tableMeta, @Nullable String tableSuffix, Map<Class<?>
            , BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap) {
        super(dialect, tableMeta, tableSuffix, defaultFunctionMap);
    }

    @Override
    protected int sqlListSize(TableMeta<?> tableMeta) {
        return 5;
    }

    @Override
    protected void handleSQLList() {

    }
}
