package io.army.reactive;

import io.army.session.SessionFactory;

public interface ReactiveSessionFactory extends SessionFactory, Closeable {


    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, ReactiveSession> {

    }

}
