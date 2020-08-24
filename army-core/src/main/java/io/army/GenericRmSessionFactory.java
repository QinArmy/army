package io.army;

import io.army.boot.DomainValuesGenerator;
import io.army.dialect.Database;

/**
 * <p>
 * This interface encapsulate rm(Resource Manager) SessionFactory api than can directly access database.
 * </p>
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@code io.army.sync.SessionFactory}</li>
 *         <li>{@code io.army.boot.sync.RmSessionFactory}</li>
 *         <li>{@code io.army.reactive.ReactiveSessionFactory}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveRmSessionFactory}</li>
 *     </ul>
 * </p>
 */
public interface GenericRmSessionFactory extends GenericSessionFactory {


    int databaseIndex();

    int tableCountPerDatabase();

    Database actualDatabase();

    DomainValuesGenerator domainValuesGenerator();

    boolean compareDefaultOnMigrating();

}
