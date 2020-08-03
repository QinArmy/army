package io.army.sync;

import io.army.GenericTmSessionFactory;
import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.Isolation;


public interface TmSessionFactory extends GenericSyncApiSessionFactory, GenericTmSessionFactory {

    @Override
    ProxyTmSession proxySession();

    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder transactionName(@Nullable String transactionName);

        SessionBuilder currentSession(boolean current);

        SessionBuilder isolation(Isolation isolation);

        SessionBuilder readOnly(boolean readOnly);

        SessionBuilder timeout(int timeoutSeconds);

        TmSession build() throws SessionException;

    }
}
