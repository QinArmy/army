package io.army.reactive;

import io.army.session._ArmySessionFactory;

abstract class ArmyReactiveRmSession extends ArmyReactiveSession {

    private ArmyReactiveRmSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {

        super(builder);
    }


}
