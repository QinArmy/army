package io.army.reactive;

import io.army.session.Option;
import io.army.session._ArmySession;
import io.army.session._ArmySessionFactory;
import io.army.tx.TransactionInfo;
import reactor.core.publisher.Mono;

import java.util.function.Function;

abstract class _ArmyReactiveSession<S extends ReactiveSession> extends _ArmySession implements ReactiveSession {

    protected _ArmyReactiveSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {
        super(builder);
    }


    @Override
    public final Mono<TransactionInfo> transactionStatus() {
        return null;
    }

    @Override
    public final Mono<Object> setSavePoint() {
        return null;
    }

    @Override
    public final Mono<Object> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return null;
    }

}
