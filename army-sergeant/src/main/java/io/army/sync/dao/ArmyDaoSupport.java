package io.army.sync.dao;


import io.army.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface ArmyDaoSupport {

    <T> void save(T domain);

    <T> void batchSave(List<T> domainList);

    @Nullable
    <T> T get(Class<T> domainClass, Object id);

    @Nullable
    <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    @Nullable
    <T> T findById(Class<T> domainClass, Object id);

    @Nullable
    Map<String, Object> findByIdAsMap(Class<?> domainClass, Object id);

}
