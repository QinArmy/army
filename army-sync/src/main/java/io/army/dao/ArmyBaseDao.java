package io.army.dao;

import io.army.domain.IDomain;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ArmyBaseDao {

    <T extends IDomain> void save(T domain);

    <T extends IDomain> void multiSave(Class<T> domainClass, List<T> domainList);

    <T extends IDomain> void batchSave(Class<T> domainClass, List<T> domainList);

    <T extends IDomain> boolean isExists(Class<T> domainClass, Object id);

    <T extends IDomain> boolean isExists(Class<T> domainClass, Object id, @Nullable Boolean visible);

    <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue);

    <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible);

    <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible);

    <T extends IDomain> T get(Class<T> domainClass, Object id);

    <T extends IDomain> T get(Class<T> domainClass, Object id, @Nullable Boolean visible);

    <T extends IDomain> T getByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue);

    <T extends IDomain> T getByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> T getByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible);

    <T extends IDomain> T getByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible);
}
