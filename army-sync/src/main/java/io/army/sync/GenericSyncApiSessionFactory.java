package io.army.sync;

import io.army.session.GenericProxySession;

/**
 * This interface representing a sync api session factory(used by developer).
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link SessionFactory}</li>
 *     <li>{@code io.army.TmSessionFactory}</li>
 * </ul>
 */
public interface GenericSyncApiSessionFactory extends SyncSessionFactory {

    GenericProxySession proxySession();


    boolean hasCurrentSession();

}
