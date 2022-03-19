package io.army.example.dao.sync.dao;

import io.army.example.domain.Domain;

import java.util.List;

public interface BaseDao {

    <T extends Domain> void save(T domain);

    <T extends Domain> void batchSave(List<T> domainList);

    <T extends Domain> T get(Class<T> domainClass, Object id);

    <T extends Domain> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    void flush();

}
