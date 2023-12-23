package io.army.sync.dao;


import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>This interface is designed for dao layer.
 *
 * @since 0.6.0
 */
public interface SyncDaoSupport {

    <T> void save(T domain);

    <T> void batchSave(List<T> domainList);

    @Nullable
    <T> T get(Class<T> domainClass, Object id);

    @Nullable
    <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    @Nullable
    <T> T findById(Class<T> domainClass, Object id);

    @Nullable
    <T> T findByUnique(Class<T> domainClass, String fieldName, Object fieldValue);


}
