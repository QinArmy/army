package io.army.example.service.sync;

import io.army.common.Domain;
import io.army.domain.IDomain;
import io.army.example.dao.sync.dao.BaseDao;
import io.army.example.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("syncBaseService")
@Profile(BaseService.SYNC)
public class SyncBaseServiceImpl implements SyncBaseService {

    public static final String TX_MANAGER = "exampleSyncTransactionManager";

    private BaseDao baseDao;


    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <T extends Domain> T get(Class<T> domainClass, Object id) {
        return getBaseDao().get(domainClass, id);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public <T extends Domain> void save(T domain) {
        getBaseDao().save(domain);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <T extends Domain> T findById(Class<T> domainClass, Object id) {
        return this.baseDao.findById(domainClass, id);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Map<String, Object> findByIdAsMap(Class<? extends IDomain> domainClass, Object id) {
        return this.baseDao.findByIdAsMap(domainClass, id);
    }


    protected BaseDao getBaseDao() {
        return this.baseDao;
    }

    @Autowired
    public void setBaseDao(@Qualifier("syncBaseDao") BaseDao baseDao) {
        this.baseDao = baseDao;
    }


}
