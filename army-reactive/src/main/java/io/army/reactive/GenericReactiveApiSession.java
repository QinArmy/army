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


    /**
     * @return a unmodifiable map
     */
    Mono<Integer> batchUpdate(Update update);

    /**
     * @return a unmodifiable map
     */
    Mono<Integer> batchUpdate(Update update, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Mono<Long> batchLargeUpdate(Update update);

    /**
     * @return a unmodifiable map
     */
    Mono<Long> batchLargeUpdate(Update update, Visible visible);

    Mono<Void> valueInsert(Insert insert);

    Mono<Void> valueInsert(Insert insert, Visible visible);

    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Mono<Integer> batchDelete(Delete delete);

    /**
     * @return a unmodifiable map
     */
    Mono<Integer> batchDelete(Delete delete, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Mono<Long> batchLargeDelete(Delete delete);

    /**
     * @return a unmodifiable map
     */
    Mono<Long> batchLargeDelete(Delete delete, Visible visible);

}
