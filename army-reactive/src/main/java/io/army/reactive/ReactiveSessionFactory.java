package io.army.reactive;

import io.army.session.SessionFactory;

/**
 * <p>This interface representing a reactive database session factory.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link ReactiveLocalSessionFactory}</li>
 *     <li>{@link ReactiveRmSessionFactory}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface ReactiveSessionFactory extends SessionFactory, Closeable {


}
