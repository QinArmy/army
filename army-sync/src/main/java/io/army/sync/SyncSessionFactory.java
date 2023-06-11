package io.army.sync;

import io.army.session.SessionFactory;

/**
 * <p>
 * This interface representing blocking way session factory.
 * This interface is only base interface of :
 * <ul>
 *     <li>{@link LocalSessionFactory}</li>
 *     <li>{@link RmSessionFactory}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SyncSessionFactory extends SessionFactory {


}
