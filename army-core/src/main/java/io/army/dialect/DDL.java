package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the abstract of debugSQL tableMeta ,that is a part of DDL.
 */
public interface DDL extends SQL {

    List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix);

    List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix, Collection<FieldMeta<?, ?>> addFieldMetas);

    List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix, Collection<FieldMeta<?, ?>> changeFieldMetas);

    List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix, Collection<IndexMeta<?>> indexMetas);

    List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix, Collection<String> indexNames);

}
