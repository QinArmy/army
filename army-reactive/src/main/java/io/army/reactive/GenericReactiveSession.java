package io.army.reactive;

import io.army.session.GenericSession;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link ReactiveSession}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveTmSession}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveRmSession}</li>
 *     </ul>
 * </p>
 */
interface GenericReactiveSession extends BaseReactiveSession, GenericSession {

    Mono<Void> close();

}
