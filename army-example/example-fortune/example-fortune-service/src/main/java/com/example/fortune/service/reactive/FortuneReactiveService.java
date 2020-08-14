package com.example.fortune.service.reactive;

import com.example.domain.EDomain;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FortuneReactiveService {

    <D extends EDomain> Mono<Boolean> isExists(Class<D> domainClass, Object id);

    <D extends EDomain> Mono<Boolean> isExistsByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    <D extends EDomain> Mono<Boolean> isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList);

    @Nullable
    <D extends EDomain> Mono<D> get(Class<D> domainClass, Object id);

    @Nullable
    <D extends EDomain> Mono<D> getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue);

    @Nullable
    <D extends EDomain> Mono<D> getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList);
}
