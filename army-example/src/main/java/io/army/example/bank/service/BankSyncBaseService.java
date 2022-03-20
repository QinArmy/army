package io.army.example.bank.service;

import io.army.domain.IDomain;
import io.army.example.common.BaseDao;
import io.army.example.common.BaseService;
import io.army.example.common.Domain;
import io.army.example.common.SyncBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("bankSyncBaseService")
@Profile(BaseService.SYNC)
public class BankSyncBaseService implements SyncBaseService {


    public static final String TX_MANAGER = "bankSyncTransactionManager";


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
    public void setBaseDao(@Qualifier("bankSyncBaseDao") BaseDao baseDao) {
        this.baseDao = baseDao;
    }


}
