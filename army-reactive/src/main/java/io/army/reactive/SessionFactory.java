package io.army.reactive;

import io.army.session.GenericSessionFactory;
import io.army.session.SessionException;
import reactor.core.publisher.Mono;

public interface SessionFactory extends GenericSessionFactory {


    SessionBuilder builder();

    Mono<Void> close();


    interface SessionBuilder {

        SessionBuilder readonly(boolean readonly);

        Mono<Session> build() throws SessionException;
    }

}
