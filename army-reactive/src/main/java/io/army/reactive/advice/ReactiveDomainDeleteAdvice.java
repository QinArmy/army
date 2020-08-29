package io.army.reactive.advice;

import io.army.advice.GenericDomainAdvice;
import io.army.meta.TableMeta;
import io.army.reactive.GenericProxyReactiveSession;
import reactor.core.publisher.Mono;

public interface ReactiveDomainDeleteAdvice extends GenericDomainAdvice {


    default Mono<Void> beforeDelete(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> afterDelete(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> deleteThrows(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession, Throwable ex) {
        return Mono.empty();
    }

}
