package io.army.schema.migration;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;
import org.springframework.lang.Nullable;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Meta2Schema {


    Map<TableMeta<?>, List<String>> migrate(Collection<TableMeta<?>> tableMetas, Connection connection
            , Dialect dialect) throws SchemaInfoException, MetaException;


}
