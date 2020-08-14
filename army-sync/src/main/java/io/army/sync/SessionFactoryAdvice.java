package io.army.sync;

import io.army.env.ArmyEnvironment;

public interface SessionFactoryAdvice {

    int order();

    void beforeInstance(ArmyEnvironment environment);

    void beforeInitialize(GenericSyncApiSessionFactory sessionFactory);

    void afterInitialize(GenericSyncApiSessionFactory sessionFactory);
}
