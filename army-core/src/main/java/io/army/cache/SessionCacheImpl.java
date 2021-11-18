package io.army.cache;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericSession;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

final class SessionCacheImpl implements SessionCache {


    private final DomainProxyFactory domainProxyFactory;

    private final GenericSession session;

    private final Map<TableMeta<?>, Map<Object, IDomain>> tableCacheById = new HashMap<>();

    private final Map<TableMeta<?>, Map<UniqueKey, IDomain>> tableCacheByUnique = new HashMap<>();

    private final Map<TableMeta<?>, Map<Object, Set<UniqueKey>>> tableIdUniqueMap = new HashMap<>();

    private final Map<TableMeta<?>, Map<Object, DomainUpdateAdvice>> adviceCacheById = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T cacheDomainById(TableMeta<T> tableMeta, T domain)
            throws SessionCacheException {
        // 1. create proxy
        Pair<IDomain, DomainUpdateAdvice> pair = domainProxyFactory.createDomainProxy(domain);
        final IDomain proxy = pair.getFirst();

        // 2. cache proxy by id
        Map<Object, IDomain> cacheById = this.tableCacheById.computeIfAbsent(tableMeta, key -> new HashMap<>());
        // do cache proxy by id
        cacheById.put(proxy.getId(), proxy);

        if (this.tableIdUniqueMap.containsKey(tableMeta)) {
            // 2.1 catch proxy by unique (optional)
            doCacheByUniqueForId(tableMeta, proxy);
        }

        Map<Object, DomainUpdateAdvice> updateAdviceMapById;
        // 3. cache DomainUpdateAdvice
        updateAdviceMapById = this.adviceCacheById.computeIfAbsent(tableMeta, key -> new HashMap<>());
        updateAdviceMapById.put(proxy.getId(), pair.getSecond());
        return (T) proxy;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T cacheDomainByUnique(TableMeta<T> tableMeta, T domain, final UniqueKey uniqueKey)
            throws SessionCacheException {
        // 1. create proxy
        Pair<IDomain, DomainUpdateAdvice> pair = domainProxyFactory.createDomainProxy(domain);
        final IDomain proxy = pair.getFirst();

        // 2. cache proxy by unique
        Map<UniqueKey, IDomain> cacheByUnique;
        cacheByUnique = this.tableCacheByUnique.computeIfAbsent(tableMeta, key -> new HashMap<>());
        // do cache proxy by unique
        cacheByUnique.put(uniqueKey, proxy);

        // 3. cache proxy by id
        Map<Object, IDomain> cacheById = this.tableCacheById.computeIfAbsent(tableMeta, key -> new HashMap<>());
        // do cache proxy by id
        cacheById.put(proxy.getId(), proxy);

        // 4. map id to unique set
        Map<Object, Set<UniqueKey>> idUniqueSetMap;
        idUniqueSetMap = this.tableIdUniqueMap.computeIfAbsent(tableMeta, key -> new HashMap<>());
        Set<UniqueKey> uniqueKeySet = idUniqueSetMap.computeIfAbsent(proxy.getId(), key -> new HashSet<>());
        // do map id to unique set
        uniqueKeySet.add(uniqueKey);

        Map<Object, DomainUpdateAdvice> updateAdviceMapById;
        // 5. cache DomainUpdateAdvice
        updateAdviceMapById = this.adviceCacheById.computeIfAbsent(tableMeta, key -> new HashMap<>());
        updateAdviceMapById.put(proxy.getId(), pair.getSecond());
        return (T) proxy;
    }


    @Override
    public Collection<DomainUpdateAdvice> updateAdvices() {
        List<DomainUpdateAdvice> adviceList = new ArrayList<>();
        for (Map<Object, DomainUpdateAdvice> values : this.adviceCacheById.values()) {
            adviceList.addAll(values.values());
        }
        return Collections.unmodifiableList(adviceList);
    }

    @Override
    public void clear(GenericSession session) {
        Assert.isTrue(session == this.session, "session error.");

        for (DomainUpdateAdvice advice : updateAdvices()) {
            Assert.state(!advice.hasUpdate(), "exits no execute update.");
        }

        this.tableCacheById.clear();
        this.tableCacheByUnique.clear();
        this.tableIdUniqueMap.clear();
        this.adviceCacheById.clear();
    }

    /*################################## blow private method ##################################*/

    private void doCacheByUniqueForId(TableMeta<?> tableMeta, IDomain proxy) {
        Map<Object, Set<UniqueKey>> uniqueKeySetMap = this.tableIdUniqueMap.get(tableMeta);
        if (CollectionUtils.isEmpty(uniqueKeySetMap)) {
            return;
        }
        Set<UniqueKey> uniqueKeySet = uniqueKeySetMap.get(proxy.getId());
        if (CollectionUtils.isEmpty(uniqueKeySet)) {
            return;
        }
        Map<UniqueKey, IDomain> cacheByUnique = this.tableCacheByUnique.get(tableMeta);
        for (UniqueKey uniqueKey : uniqueKeySet) {
            if (cacheByUnique == null) {
                throw new SessionCacheException("TableMeta[%s] not found cache by UniqueKey[%s] for id[%s]"
                        , tableMeta, uniqueKey, proxy.getId());
            }
            // cache proxy by unique key
            cacheByUnique.put(uniqueKey, proxy);
        }

    }
}
