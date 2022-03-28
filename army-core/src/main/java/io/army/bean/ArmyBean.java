package io.army.bean;

import io.army.session.GenericSessionFactory;

@Deprecated
public interface ArmyBean {

    default void initializing(GenericSessionFactory sessionFactory) throws Exception {

    }

    default void armyBeanDestroy() {

    }
}
