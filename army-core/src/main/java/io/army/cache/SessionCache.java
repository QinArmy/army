package io.army.cache;

import io.army.GenericSession;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Collection;

public interface SessionCache {

    @Nullable
    <T extends IDomain> T getDomain(TableMeta<T> tableMeta, Object key) throws SessionCacheException;

    <T extends IDomain> T cacheDomainById(TableMeta<T> tableMeta, T domain, Object id)
            throws SessionCacheException;

    <T extends IDomain> T cacheDomainByUnique(TableMeta<T> tableMeta, T domain, UniqueKey uniqueKey)
            throws SessionCacheException;

    Collection<DomainUpdateAdvice> updateAdvices();


    void clear(GenericSession session);

}
