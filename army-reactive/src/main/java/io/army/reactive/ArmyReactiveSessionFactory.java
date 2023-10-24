package io.army.reactive;

import io.army.reactive.executor.StmtExecutor;
import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;

abstract class ArmyReactiveSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {

    ArmyReactiveSessionFactory(FactoryBuilderSupport support) throws SessionFactoryException {
        super(support);
    }


    static abstract class ReactiveSessionBuilder<B, S extends ReactiveSession> extends ArmySessionBuilder<B, S> {

        StmtExecutor stmtExecutor;

        ReactiveSessionBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


    }//ReactiveSessionBuilder

}
