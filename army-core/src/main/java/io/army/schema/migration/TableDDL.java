package io.army.schema.migration;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * This interface represents the abstract of create table ,that is a part of DDL.
 */
public interface TableDDL {

    String tableDefinition(TableMeta<?> tableMeta);

    String addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);

    String modifyColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);

    String addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas);

    String modifyIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas);

    String dropIndex(TableMeta<?> tableMeta,Collection<String> indexNames);

}
