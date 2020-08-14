package com.example.fortune.service.sync;

import com.example.domain.EDomain;
import com.example.fortune.dao.sync.FortuneSyncDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("fortuneBaseSyncService")
public class FortuneBaseSyncService implements FortuneSyncService {

    public static final String TX_MANAGER = "fortuneSyncTxManager";

    protected FortuneSyncDao baseDao;

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> boolean isExists(Class<D> domainClass, Object id) {
        return getBaseDao().isExists(domainClass, id);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue) {
        return getBaseDao().isExistsByUnique(domainClass, uniquePropName, uniqueValue);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return getBaseDao().isExistsByUnique(domainClass, propNameList, valueList);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> D get(Class<D> domainClass, Object id) {
        return getBaseDao().get(domainClass, id);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue) {
        return getBaseDao().getByUnique(domainClass, uniquePropName, uniqueValue);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return getBaseDao().getByUnique(domainClass, propNameList, valueList);
    }


    /*################################## blow setter method ##################################*/

    @Autowired
    public void setBaseDao(@Qualifier("fortuneSyncBaseDao") FortuneSyncDao baseDao) {
        this.baseDao = baseDao;
    }

    protected FortuneSyncDao getBaseDao() {
        return this.baseDao;
    }
}
