package io.army.reactive;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import reactor.core.publisher.Mono;


/**
 * <p>
 * this interface have four direct sub interfaces:
 *     <ul>
 *         <li>{@link ReactiveSession}</li>
 *         <li>{@link ProxyReactiveSession}</li>
 *         <li>{@code io.army.reactive.ReactiveTmSession}</li>
 *         <li>{@code io.army.reactive.ProxyReactiveTmSession}</li>
 *     </ul>
 * </p>
 *
 * @see ReactiveSession
 * @see ProxyReactiveSession
 */
public interface GenericReactiveApiSession extends GenericReactiveSession {

    Mono<Void> valueInsert(Insert insert);

    Mono<Void> valueInsert(Insert insert, Visible visible);

    Mono<Void> flush();

}
