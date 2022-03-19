package io.army.example.dao.sync.dao;

import io.army.example.domain.Domain;
import io.army.lang.Nullable;

import java.util.List;

public interface BaseDao {

    <T extends Domain> void save(T domain);

    <T extends Domain> void batchSave(List<T> domainList);

    @Nullable
    <T extends Domain> T get(Class<T> domainClass, Object id);

    @Nullable
    <T extends Domain> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    @Nullable
    <T extends Domain> T findById(Class<T> domainClass, Object id);

    void flush();

}
