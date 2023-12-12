package io.army.sync.dao;

import io.army.sync.SyncSessionContext;

import java.util.List;
import java.util.Map;

/**
 * <p>This class is a abstract implementation of {@link ArmyDaoSupport}
 *
 * @since 1.0
 */
public abstract class ArmyDaoSupportImpl implements ArmyDaoSupport {

    protected final SyncSessionContext sessionContext;

    protected ArmyDaoSupportImpl(SyncSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public <T> void save(T domain) {
        this.sessionContext.currentSession().save(domain);
    }

    @Override
    public <T> void batchSave(List<T> domainList) {
        this.sessionContext.currentSession().batchSave(domainList);
    }

    @Override
    public <T> T get(Class<T> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T findById(Class<T> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> findByIdAsMap(Class<?> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }


}
