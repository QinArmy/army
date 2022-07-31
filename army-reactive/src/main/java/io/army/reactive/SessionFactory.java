package io.army.reactive;

import io.army.session.SessionException;
import reactor.core.publisher.Mono;

public interface SessionFactory extends io.army.session.SessionFactory {


    SessionBuilder builder();

    Mono<Void> close();


    interface SessionBuilder {

        SessionBuilder readonly(boolean readonly);

        Mono<Session> build() throws SessionException;
    }

}
