package io.army.example.pill.service.sync;


import io.army.example.common.BaseService;
import io.army.example.common.Domain;
import io.army.example.common.SyncBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("pillBaseServiceAdapter")
@Profile(BaseService.SYNC)
public class PillBaseServiceAdapter implements BaseService {

    private SyncBaseService baseService;

    @Override
    public <T extends Domain> Mono<T> get(Class<T> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(getBaseService().get(domainClass, id)));
    }

    @Override
    public <T extends Domain> Mono<Void> save(T domain) {
        return Mono.defer(() -> {
            getBaseService().save(domain);
            return Mono.empty();
        });
    }

    @Override
    public <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(getBaseService().findById(domainClass, id)));
    }

    @Override
    public Mono<Map<String, Object>> findByIdAsMap(Class<?> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(getBaseService().findByIdAsMap(domainClass, id)));
    }

    protected SyncBaseService getBaseService() {
        return this.baseService;
    }


    @Autowired
    public void setBaseService(@Qualifier("pillSyncBaseService") SyncBaseService baseService) {
        this.baseService = baseService;
    }


}
