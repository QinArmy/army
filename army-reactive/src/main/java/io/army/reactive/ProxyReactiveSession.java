package io.army.reactive;


/**
 * This interface is a proxy of {@link ReactiveSession} for obtain current {@link ReactiveSession}.
 * For example, spring DAO bean.
 */
public interface ProxyReactiveSession extends SingleDatabaseReactiveSession, GenericProxyReactiveSession {


}
