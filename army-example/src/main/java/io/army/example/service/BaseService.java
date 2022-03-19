package io.army.example.service;

import io.army.example.domain.Domain;
import reactor.core.publisher.Mono;

public interface BaseService {

    String REACTIVE = "reactive";

    String SYNC = "sync";


    <T extends Domain> Mono<T> get(Class<T> domainClass, Object id);

    <T extends Domain> Mono<Void> save(T domain);

    <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id);


}
