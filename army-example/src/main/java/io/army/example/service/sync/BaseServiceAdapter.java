package io.army.example.service.sync;

import io.army.domain.IDomain;
import io.army.example.domain.Domain;
import io.army.example.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("baseServiceAdapter")
@Profile(BaseService.SYNC)
public class BaseServiceAdapter implements BaseService {

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
    public Mono<Map<String, Object>> findByIdAsMap(Class<? extends IDomain> domainClass, Object id) {
        return Mono.defer(() -> Mono.justOrEmpty(getBaseService().findByIdAsMap(domainClass, id)));
    }

    protected SyncBaseService getBaseService() {
        return this.baseService;
    }


    @Autowired
    public void setBaseService(@Qualifier("syncBaseService") SyncBaseService baseService) {
        this.baseService = baseService;
    }


}
