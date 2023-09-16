package io.army.reactive;

import io.army.session._ArmySession;
import io.army.session._ArmySessionFactory;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import java.util.List;

abstract class _ArmyReactiveSession extends _ArmySession implements ReactiveSession {

    protected _ArmyReactiveSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {
        super(builder);
    }


    final <R> Mono<R> justOne(List<R> list) {
        final Mono<R> mono;
        switch (list.size()) {
            case 0:
                mono = Mono.empty();
                break;
            case 1:
                mono = Mono.just(list.get(0));
                break;
            default: {
                mono = Mono.error(_Exceptions.nonUnique(list));
            }
        }
        return mono;
    }


}
