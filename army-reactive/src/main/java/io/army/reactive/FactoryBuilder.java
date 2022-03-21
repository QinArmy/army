package io.army.reactive;

import io.army.session.SessionFactoryException;


public interface FactoryBuilder {

    FactoryBuilder dataSource(Object dataSource);

    SessionFactory build() throws SessionFactoryException;

    static FactoryBuilder builder() {
        return new LocalSessionFactoryBuilder();
    }


}
