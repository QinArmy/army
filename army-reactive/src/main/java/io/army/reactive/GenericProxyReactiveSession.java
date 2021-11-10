package io.army.reactive;

import io.army.session.GenericProxySession;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@code io.army.reactive.ProxyReactiveSession}</li>
 *         <li>{@code io.army.boot.reactive.ProxyReactiveTmSession}</li>
 *     </ul>
 * </p>
 */
public interface GenericProxyReactiveSession extends BaseReactiveApiSession, GenericProxySession {

    Mono<Boolean> readOnly();

    Mono<Boolean> hasTransaction();


}
