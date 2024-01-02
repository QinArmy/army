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
import io.army.example.common.SyncBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("bankBaseServiceAdapter")
@Profile(BaseService.SYNC)
public class BankBaseServiceAdapter implements BaseService {

    protected SyncBaseService baseService;

    @Override
    public <T extends Domain> Mono<T> get(Class<T> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(this.getBaseService().get(domainClass, id)));
    }

    @Override
    public <T extends Domain> Mono<Void> save(T domain) {
        return Mono.defer(() -> {
            this.getBaseService().save(domain);
            return Mono.empty();
        });
    }

    @Override
    public <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(this.getBaseService().findById(domainClass, id)));
    }

    @Override
    public Mono<Map<String, Object>> findByIdAsMap(Class<?> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(this.getBaseService().findByIdAsMap(domainClass, id)));
    }


    protected SyncBaseService getBaseService() {
        return this.baseService;
    }


    @Autowired
    public void setBaseService(@Qualifier("bankSyncBaseService") SyncBaseService baseService) {
        this.baseService = baseService;
    }


}
