package io.army.session.executor;

public interface DatabaseSessionHolder {

    <T> T getDatabaseSession(Class<T> typeClass);


}
