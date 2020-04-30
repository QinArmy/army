package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the abstract of debugSQL tableMeta ,that is a part of DDL.
 */
public interface DDL extends SQL {

    List<String> createTable(TableMeta<?> tableMeta);

    List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);

    List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas);

    List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas);

    List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames);

}
