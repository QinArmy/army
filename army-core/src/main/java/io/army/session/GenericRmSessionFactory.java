package io.army.session;

import io.army.boot.DomainValuesGenerator;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

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


    byte databaseIndex();

    byte tableCountPerDatabase();

    ServerMeta serverMeta();

    Dialect dialect();

    DomainValuesGenerator domainValuesGenerator();

    FactoryMode factoryMode();


}