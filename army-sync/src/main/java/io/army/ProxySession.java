package io.army;

import io.army.domain.IDomain;
import io.army.env.Environment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

public interface ProxySession extends GenericSyncApiSession {

    boolean hasCurrentSession();

    Environment environment();

    @Nullable
    <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass);

}
