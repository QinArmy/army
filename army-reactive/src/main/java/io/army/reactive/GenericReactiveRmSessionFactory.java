package io.army.reactive;

import io.army.dialect.Dialect;

public interface GenericReactiveRmSessionFactory extends GenericReactiveSessionFactory {

    Dialect dialect();
}
