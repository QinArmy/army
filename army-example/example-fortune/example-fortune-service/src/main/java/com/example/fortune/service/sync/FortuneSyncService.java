package com.example.fortune.service.sync;


import com.example.domain.EDomain;
import org.springframework.lang.Nullable;

import java.util.List;

public interface FortuneSyncService {

    <D extends EDomain> boolean isExists(Class<D> domainClass, Object id);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList);

    @Nullable
    <D extends EDomain> D get(Class<D> domainClass, Object id);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    @Nullable
    <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList);
}
