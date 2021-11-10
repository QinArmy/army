package io.army.beans;

import io.army.session.GenericSessionFactory;

public interface ArmyBean {

    default void initializing(GenericSessionFactory sessionFactory) throws Exception {

    }

    default void armyBeanDestroy() {

    }
}
