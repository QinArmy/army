package io.army.beans;

import io.army.GenericSessionFactory;

public interface ArmyBean {

    default void initializing(GenericSessionFactory sessionFactory) throws Exception {

    }

    default void armyBeanDestroy() {

    }
}
