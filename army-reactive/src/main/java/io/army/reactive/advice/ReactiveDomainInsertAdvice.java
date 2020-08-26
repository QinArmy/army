package io.army.reactive.advice;

import io.army.advice.GenericDomainAdvice;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveApiSession;
import reactor.core.publisher.Mono;

public interface ReactiveDomainInsertAdvice extends GenericDomainAdvice {

    default Mono<Void> beforeInsert(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> afterInsert(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
        return Mono.empty();
    }

    default Mono<Void> InsertThrows(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession, Throwable ex) {
        return Mono.empty();
    }
}
