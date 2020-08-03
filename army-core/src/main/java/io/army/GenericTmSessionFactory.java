package io.army;

import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;

import java.util.List;

/**
 * This interface is base interface of below:
 * <ul>
 *     <li>{@code io.army.sync.TmSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveTmSessionFactory}</li>
 * </ul>
 */
public interface GenericTmSessionFactory extends GenericSessionFactory {

    boolean supportZone();

    /**
     * @return a unmodifiable list
     */
    List<Database> actualDatabaseList();

    /**
     * @return a integer than great than 0 .
     */
    int tableCountPerDatabase();

    DatabaseRoute dataSourceRoute(TableMeta<?> tableMeta) throws NotFoundRouteException;
}
