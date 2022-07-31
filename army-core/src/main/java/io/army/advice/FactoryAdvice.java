package io.army.advice;

import io.army.env.ArmyEnvironment;
import io.army.meta.ServerMeta;
import io.army.session.SessionFactory;

public interface FactoryAdvice {

    int order();

    void beforeInstance(ServerMeta serverMeta, ArmyEnvironment environment);

    void beforeInitialize(SessionFactory sessionFactory);

    void afterInitialize(SessionFactory sessionFactory);
}
