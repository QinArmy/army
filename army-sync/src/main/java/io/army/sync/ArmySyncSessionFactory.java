package io.army.sync;

import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;

abstract class ArmySyncSessionFactory extends _ArmySessionFactory implements SyncSessionFactory {

    ArmySyncSessionFactory(FactoryBuilderSupport support) throws SessionFactoryException {
        super(support);
    }


}
