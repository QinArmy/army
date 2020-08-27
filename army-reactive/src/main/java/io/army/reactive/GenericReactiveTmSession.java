package io.army.reactive;

import io.army.GenericSession;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link ReactiveSession}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveTmSession}</li>
 *     </ul>
 * </p>
 */
public interface GenericReactiveTmSession extends BaseReactiveApiSession, GenericSession {


    Mono<Void> close();

}
