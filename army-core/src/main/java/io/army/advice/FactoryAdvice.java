package io.army.advice;

import io.army.env.ArmyEnvironment;
import io.army.session.GenericSessionFactory;

public interface FactoryAdvice {

    void beforeInstance(ArmyEnvironment environment);

    void beforeInitialize(GenericSessionFactory sessionFactory);

    void afterInitialize(GenericSessionFactory sessionFactory);
}
