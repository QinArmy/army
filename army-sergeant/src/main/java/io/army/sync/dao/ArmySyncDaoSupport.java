package io.army.sync.dao;

import io.army.criteria.Select;
import io.army.modelgen._MetaBridge;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionContext;
import io.army.util.ArmyCriteria;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>This class is a abstract implementation of {@link SyncDaoSupport}
 *
 * @since 0.6.0
 */
public abstract class ArmySyncDaoSupport implements SyncDaoSupport {

    protected final SyncSessionContext sessionContext;

    protected ArmySyncDaoSupport(SyncSessionContext sessionContext) {
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
        return findByUnique(domainClass, _MetaBridge.ID, id);
    }

    @Override
    public <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        return findByUnique(domainClass, fieldName, fieldValue);
    }

    @Override
    public <T> T findById(Class<T> domainClass, Object id) {
        return findByUnique(domainClass, _MetaBridge.ID, id);
    }

    @Nullable
    @Override
    public <T> T findByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        final SyncSession session;
        session = this.sessionContext.currentSession();

        final Select stmt;
        stmt = ArmyCriteria.queryDomainByUniqueStmt(session, domainClass, fieldName, fieldValue);
        return session.queryOne(stmt, domainClass);
    }


}
