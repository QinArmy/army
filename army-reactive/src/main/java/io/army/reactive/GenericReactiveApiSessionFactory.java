package io.army.reactive;


import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link SessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveTmSessionFactory}</li>
 * </ul>
 */
public interface GenericReactiveApiSessionFactory extends GenericReactiveSessionFactory {

    GenericProxyReactiveSession proxySession();

    Mono<Boolean> hasCurrentSession();

    Function<Throwable, Throwable> composeExceptionFunction();
}
