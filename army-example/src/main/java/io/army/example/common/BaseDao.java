package io.army.example.common;


import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface BaseDao {

    <T extends Domain> Mono<Void> save(T domain);

    <T extends Domain> Mono<Void> batchSave(List<T> domainList);

    <T extends Domain> Mono<T> get(Class<T> domainClass, Object id);

    <T extends Domain> Mono<T> getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    <T extends Domain> Mono<T> getById(Class<T> domainClass, Object id);

    Mono<Map<String, Object>> getByIdAsMap(Class<?> domainClass, Object id);

    Mono<Void> flush();
}
