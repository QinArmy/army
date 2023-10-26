package io.army.reactive;

/**
 * <p>This interface representing reactive RM(Resource Manager) session in XA transaction.
 *
 * @since 1.0
 */
public interface ReactiveRmSession extends ReactiveSession {

    @Override
    ReactiveRmSessionFactory sessionFactory();


}
