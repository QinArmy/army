package io.army.example.common;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.Map;

public interface SyncBaseService {

    @Nullable
    <T extends Domain> T get(Class<T> domainClass, Object id);

    <T extends Domain> void save(T domain);

    @Nullable
    <T extends Domain> T findById(Class<T> domainClass, Object id);

    @Nullable
    Map<String, Object> findByIdAsMap(Class<? extends IDomain> domainClass, Object id);

}
