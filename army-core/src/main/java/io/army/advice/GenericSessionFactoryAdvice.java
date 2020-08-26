package io.army.advice;

import io.army.GenericSessionFactory;
import io.army.env.ArmyEnvironment;

public interface GenericSessionFactoryAdvice {

    int order();

    void beforeInstance(ArmyEnvironment environment);

    void beforeInitialize(GenericSessionFactory sessionFactory);

    void afterInitialize(GenericSessionFactory sessionFactory);
}
