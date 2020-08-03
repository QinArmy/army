package io.army;

import io.army.domain.IDomain;
import io.army.env.Environment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

/**
 * This interface is base interface of below:
 * <ul>
 *     <li>{@code  io.army.sync.ProxySession}</li>
 *     <li>{@code  io.army.sync.ProxyTmSession}</li>
 * </ul>
 */
public interface GenericProxySession {

    boolean hasCurrentSession();

    Environment environment();

    @Nullable
    <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass);


}
