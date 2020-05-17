package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.env.Environment;

public interface SessionFactoryAdvice {

    int order();

    void beforeInstance(Environment environment);

    void beforeInit(GenericSessionFactory sessionFactory);

    void afterInit(GenericSessionFactory sessionFactory);
}
