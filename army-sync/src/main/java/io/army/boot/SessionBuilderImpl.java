package io.army.boot;

import io.army.Session;
import io.army.SessionException;
import io.army.SessionFactory;

class SessionBuilderImpl implements SessionFactory.SessionBuilder {

    private boolean currentSession;

    @Override
    public SessionFactory.SessionBuilder currentSession() {
        this.currentSession = true;
        return this;
    }

    @Override
    public Session build() throws SessionException {
        return null;
    }
}
