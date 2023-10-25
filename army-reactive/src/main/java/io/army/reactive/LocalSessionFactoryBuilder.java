package io.army.reactive;

import io.army.session.SessionFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LocalSessionFactoryBuilder extends ArmyReactiveFactorBuilder implements FactoryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSessionFactoryBuilder.class);

    private Object dataSource;

    LocalSessionFactoryBuilder() {

    }


    @Override
    public FactoryBuilder dataSource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public ReactiveLocalSessionFactory build() throws SessionFactoryException {
        return null;
    }
}
