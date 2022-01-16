package io.army.session;

import io.army.boot.DomainValuesGenerator;
import io.army.dialect._Dialect;
import io.army.meta.ServerMeta;
import io.army.sharding.RouteContext;

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
public interface DialectSessionFactory extends GenericSessionFactory, RouteContext {


    byte databaseIndex();

    byte tableCountPerDatabase();

    ServerMeta serverMeta();

    _Dialect dialect();

    DomainValuesGenerator domainValuesGenerator();

    FactoryMode factoryMode();


}
