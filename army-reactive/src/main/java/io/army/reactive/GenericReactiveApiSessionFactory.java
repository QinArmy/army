package io.army.reactive;

public interface GenericReactiveApiSessionFactory extends GenericReactiveSessionFactory {

    ProxyReactiveSession proxySession();
}
