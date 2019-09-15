package io.army.dialect.ddl;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * This interface represents the abstract of create table ,that is a part of DDL.
 */
public interface TableDDL {

    @Nonnull
    String tableDefinition(TableMeta<?> tableMeta);

    @Nonnull
    String addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);

    @Nonnull
    String modifyColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas);
}
