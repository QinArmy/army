package com.example.dao;

import com.example.domain.EDomain;
import org.springframework.lang.Nullable;

import java.util.List;

public interface BaseDao {

    boolean supportSessionCache();

    <D extends EDomain> void save(D domain);


    <D extends EDomain> boolean isExists(Class<D> domainClass, Object id);

    <D extends EDomain> boolean isExists(Class<D> domainClass, Object id, @Nullable Boolean visible);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible);

    @Nullable
    <D extends EDomain> D get(Class<D> domainClass, Object id);

    @Nullable
    <D extends EDomain> D get(Class<D> domainClass, Object id, @Nullable Boolean visible);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible);
}
