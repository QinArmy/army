package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public interface SchemaMigrator {

    /**
     *
     * @return a unmodifiable List
     */
    List<Migration> migrate(Collection<TableMeta<?>> tableMetas,Connection connection, SQLDialect sqlDialect)
            throws SchemaInfoException, MetaException;




}
