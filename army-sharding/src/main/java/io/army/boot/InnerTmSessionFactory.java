package io.army.boot;

import io.army.TmSessionFactory;
import io.army.meta.TableMeta;
import io.army.sharding.DataSourceRoute;

interface InnerTmSessionFactory extends TmSessionFactory, InnerGenericSyncSessionFactory {

    DataSourceRoute dataSourceRoute(TableMeta<?> tableMeta);

}
