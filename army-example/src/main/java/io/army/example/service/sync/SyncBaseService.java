package io.army.example.service.sync;

import io.army.example.domain.Domain;

public interface SyncBaseService {

    <T extends Domain> T get(Class<T> domainClass, Object id);

    <T extends Domain> void save(T domain);

}
