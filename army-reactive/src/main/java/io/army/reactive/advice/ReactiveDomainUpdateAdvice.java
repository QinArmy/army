package io.army.reactive.advice;

import io.army.advice.GenericDomainAdvice;
import io.army.meta.TableMeta;
import io.army.reactive.GenericProxyReactiveSession;
import reactor.core.publisher.Mono;


public interface ReactiveDomainUpdateAdvice extends GenericDomainAdvice {


    default Mono<Void> beforeUpdate(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> afterUpdate(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> updateThrows(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession, Throwable ex) {
        return Mono.empty();
    }

}
