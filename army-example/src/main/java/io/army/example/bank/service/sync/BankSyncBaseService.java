/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.example.bank.service.sync;


import io.army.example.common.BaseService;
import io.army.example.common.Domain;
import io.army.example.common.SyncBaseDao;
import io.army.example.common.SyncBaseService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("bankSyncBaseService")
@Profile(BaseService.SYNC)
public class BankSyncBaseService implements SyncBaseService, InitializingBean, ApplicationContextAware {


    public static final String TX_MANAGER = "bankSyncTransactionManager";

    protected static final String REQUEST_NO = "requestNo";

    protected ApplicationContext applicationContext;

    protected SyncBaseDao baseDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.baseDao = this.applicationContext.getBean("bankSyncBaseDao", SyncBaseDao.class);
    }

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
    public Map<String, Object> findByIdAsMap(Class<?> domainClass, Object id) {
        return this.baseDao.findByIdAsMap(domainClass, id);
    }


    protected SyncBaseDao getBaseDao() {
        return this.baseDao;
    }


}
