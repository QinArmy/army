package io.army.advice;

import io.army.env.ArmyEnvironment;
import io.army.meta.ServerMeta;
import io.army.session.GenericSessionFactory;

public interface FactoryAdvice {

    int order();

    void beforeInstance(ServerMeta serverMeta, ArmyEnvironment environment);

    void beforeInitialize(GenericSessionFactory sessionFactory);

    void afterInitialize(GenericSessionFactory sessionFactory);
}
