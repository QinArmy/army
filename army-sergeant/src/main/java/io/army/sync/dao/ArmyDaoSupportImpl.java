package io.army.sync.dao;

import io.army.sync.SessionContext;

import java.util.List;
import java.util.Map;

public abstract class ArmyDaoSupportImpl implements ArmyDaoSupport {

    protected final SessionContext sessionContext;

    protected ArmyDaoSupportImpl(SessionContext sessionContext) {
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
