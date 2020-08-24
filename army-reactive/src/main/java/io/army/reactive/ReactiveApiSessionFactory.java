package io.army.reactive;


/**
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link io.army.reactive.ReactiveSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveTmSessionFactory}</li>
 * </ul>
 */
public interface ReactiveApiSessionFactory extends GenericReactiveSessionFactory {

    GenericReactiveApiSession proxySession();

    boolean hasCurrentSession();
}
