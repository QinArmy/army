package io.army.sync.dao;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * <p>This interface is designed for dao layer.
 *
 * @since 1.0
 */
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
