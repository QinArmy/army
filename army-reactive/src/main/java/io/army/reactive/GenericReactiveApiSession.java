package io.army.reactive;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * <p>
 * this interface have three direct sub interfaces:
 *     <ul>
 *         <li>{@link ReactiveSession}</li>
 *         <li>{@link ProxyReactiveSession}</li>
 *         <li>{@code io.army.ReactiveTmSession}</li>
 *     </ul>
 * </p>
 *
 * @see ReactiveSession
 * @see ProxyReactiveSession
 */
public interface GenericReactiveApiSession extends GenericReactiveSession {


    Mono<Integer> batchUpdate(Update update);

    Mono<Integer> batchUpdate(Update update, Visible visible);

    Mono<Long> batchLargeUpdate(Update update);

    Mono<Long> batchLargeUpdate(Update update, Visible visible);

    Mono<Void> valueInsert(Insert insert);

    Mono<Void> valueInsert(Insert insert, Visible visible);

    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    Mono<Integer> batchDelete(Delete delete);

    Mono<Integer> batchDelete(Delete delete, Visible visible);

    Mono<Long> batchLargeDelete(Delete delete);

    Mono<Long> batchLargeDelete(Delete delete, Visible visible);

}