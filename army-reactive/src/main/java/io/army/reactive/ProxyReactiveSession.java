package io.army.reactive;


/**
 * This interface is a proxy of {@link Session} for obtain current {@link Session}.
 * For example, spring DAO bean.
 */
public interface ProxyReactiveSession extends SingleDatabaseReactiveSession, GenericProxyReactiveSession {


}
