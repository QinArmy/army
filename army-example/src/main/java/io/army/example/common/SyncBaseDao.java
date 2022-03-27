package io.army.example.common;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface SyncBaseDao {

    <T extends Domain> void save(T domain);

    <T extends Domain> void batchSave(List<T> domainList);

    @Nullable
    <T extends Domain> T get(Class<T> domainClass, Object id);

    @Nullable
    <T extends Domain> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    @Nullable
    <T extends Domain> T findById(Class<T> domainClass, Object id);

    @Nullable
    Map<String, Object> findByIdAsMap(Class<? extends IDomain> domainClass, Object id);

    void flush();

}
