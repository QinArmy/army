package io.army;

import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;

import java.util.Map;

public interface GenericTmSessionFactory extends GenericSessionFactory {

    boolean supportZone();

    Map<String, SQLDialect> actualSQLDialectMap();

    DatabaseRoute dataSourceRoute(TableMeta<?> tableMeta);
}
