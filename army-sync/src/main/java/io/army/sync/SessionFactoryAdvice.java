package io.army.sync;

import io.army.env.Environment;

public interface SessionFactoryAdvice {

    int order();

    void beforeInstance(Environment environment);

    void beforeInit(GenericSyncApiSessionFactory sessionFactory);

    void afterInit(GenericSyncApiSessionFactory sessionFactory);
}
