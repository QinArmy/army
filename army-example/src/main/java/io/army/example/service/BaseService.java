package io.army.example.service;

import io.army.common.Domain;
import io.army.domain.IDomain;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BaseService {

    String REACTIVE = "reactive";

    String SYNC = "sync";


    <T extends Domain> Mono<T> get(Class<T> domainClass, Object id);

    <T extends Domain> Mono<Void> save(T domain);

    <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id);

    Mono<Map<String, Object>> findByIdAsMap(Class<? extends IDomain> domainClass, Object id);
}
