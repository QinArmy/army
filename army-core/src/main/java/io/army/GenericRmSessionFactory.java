package io.army;

import io.army.dialect.SQLDialect;

/**
 * <p>
 * This interface encapsulate rm(Resource Manager) SessionFactory api than can directly access database.
 * </p>
 * <p>
 * this interface have four direct sub interfaces:
 *     <ul>
 *         <li>{@code io.army.sync.SessionFactory}</li>
 *         <li>{@code io.army.boot.sync.RmSessionFactory}</li>
 *         <li>{@code io.army.TmSession}</li>
 *     </ul>
 * </p>
 */
public interface GenericRmSessionFactory extends GenericSessionFactory {

    boolean supportZone();

    SQLDialect actualSQLDialect();

}
