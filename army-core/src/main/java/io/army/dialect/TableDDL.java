package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This interface represents the abstract of debugSQL table ,that is a part of DDL.
 */
public interface TableDDL extends SQL {

    List<String> tableDefinition(TableMeta<?> tableMeta);

    List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);

    List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas);

    List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas);

    List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames);

}
