package io.army.boot;

import io.army.SessionFactory;

public interface SessionFactoryInterceptor {

    void beforeInit(SessionFactory sessionFactory);

    void afterInit(SessionFactory sessionFactory);
}
