package io.army.reactive;

public interface ReactiveLocalSessionFactory extends ReactiveSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, ReactiveSession> {

    }

}
