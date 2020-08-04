package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

abstract class AbstractDDLContext implements DDLContext {

    protected final Dialect dialect;

    protected StringBuilder sqlBuilder = new StringBuilder();

    protected final TableMeta<?> tableMeta;

    private final String tableSuffix;

    protected final List<String> sqlList;

    protected final Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap;

    private boolean prepared;

    AbstractDDLContext(Dialect dialect, TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap) {
        this.dialect = dialect;
        this.tableMeta = tableMeta;
        this.tableSuffix = tableSuffix;
        int size = sqlListSize(tableMeta);
        this.sqlList = size < 1 ? Collections.emptyList() : new ArrayList<>(size);

        this.defaultFunctionMap = defaultFunctionMap;


    }

    @Override
    public String defaultValue(FieldMeta<?, ?> fieldMeta) {
        Class<?> javaType = fieldMeta.javaType();
        if (CodeEnum.class.isAssignableFrom(fieldMeta.javaType())) {
            javaType = Integer.class;
        }
        BiFunction<FieldMeta<?, ?>, ZoneId, String> function = defaultFunctionMap.get(javaType);
        Assert.notNull(function, () -> String.format("not found default value function for %s ."
                , fieldMeta.javaType().getName()));
        return function.apply(fieldMeta, dialect.zoneId());
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final void appendTable() {
        this.sqlBuilder.append(this.dialect.quoteIfNeed(this.tableMeta.tableName()));
        if (this.tableSuffix != null) {
            this.sqlBuilder.append(this.tableSuffix);
        }
    }

    @Override
    public final void append(String sql) {
        this.sqlList.add(sql);
    }

    @Override
    public final void resetBuilder() {
        this.sqlBuilder = new StringBuilder();
    }

    protected abstract int sqlListSize(TableMeta<?> tableMeta);

    protected abstract void handleSQLList();

    @Override
    public final List<String> build() {
        Assert.state(!this.prepared, "");
        this.prepared = true;
        handleSQLList();
        return Collections.unmodifiableList(this.sqlList);
    }
}
