package io.army.cache;

import io.army.GenericSession;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class SessionCacheImpl implements SessionCache {


    private final DomainProxyFactory domainProxyFactory;

    private final GenericSession session;


    private final Map<TableMeta<?>, Map<Object, IDomain>> tableCacheById = new HashMap<>();


    private final Map<TableMeta<?>, Map<UniqueKey, IDomain>> tableCacheByUnique = new HashMap<>();

    private final Map<Object, UniqueKey> idUniqueMap = new HashMap<>();

    private final Map<Object, DomainUpdateAdvice> adviceCacheById = new HashMap<>();

    SessionCacheImpl(DomainProxyFactory domainProxyFactory, GenericSession session) {
        this.domainProxyFactory = domainProxyFactory;
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T getDomain(TableMeta<T> tableMeta, Object key)
            throws SessionCacheException {
        T cacheDomain = null;
        if (key instanceof UniqueKey) {
            Map<UniqueKey, IDomain> cacheByUnique = this.tableCacheByUnique.get(tableMeta);
            if (cacheByUnique != null) {
                cacheDomain = (T) cacheByUnique.get(key);
            }
        } else {
            Map<Object, IDomain> cacheById = this.tableCacheById.get(tableMeta);
            if (cacheById != null) {
                cacheDomain = (T) cacheById.get(key);
            }
        }
        return cacheDomain;
    }

    @Override
    public <T extends IDomain> T cacheDomainById(TableMeta<T> tableMeta, T domain, Object id)
            throws SessionCacheException {
        return null;
    }

    @Override
    public <T extends IDomain> T cacheDomainByUnique(TableMeta<T> tableMeta, T domain, UniqueKey uniqueKey)
            throws SessionCacheException {
        return null;
    }

    @Override
    public Collection<DomainUpdateAdvice> updateAdvices() {
        return null;
    }

    @Override
    public void clear(GenericSession session) {
        Assert.isTrue(session == this.session, "session error.");

        for (DomainUpdateAdvice advice : this.adviceCacheById.values()) {
            Assert.state(!advice.hasUpdate(), "exits no execute update.");
        }

        this.tableCacheById.clear();
        this.tableCacheByUnique.clear();
        this.idUniqueMap.clear();
        this.adviceCacheById.clear();
    }
}
