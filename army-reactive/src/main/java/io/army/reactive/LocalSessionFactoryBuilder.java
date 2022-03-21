package io.army.reactive;

import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LocalSessionFactoryBuilder extends FactoryBuilderSupport implements FactoryBuilder {

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
    public SessionFactory build() throws SessionFactoryException {
        return null;
    }
}
