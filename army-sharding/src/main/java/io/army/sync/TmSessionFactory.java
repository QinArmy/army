package io.army.sync;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.session.GenericTmSessionFactory;
import io.army.tx.Isolation;


public interface TmSessionFactory extends GenericSyncApiSessionFactory, GenericTmSessionFactory {

    @Override
    ProxyTmSession proxySession();

    SessionBuilder builder();

    interface SessionBuilder {

        /**
         * Optional
         */
        SessionBuilder transactionName(@Nullable String transactionName);

        /**
         * Optional,default is {@code false}
         */
        SessionBuilder currentSession(boolean current);

        /**
         * Required, default is {@code null}
         */
        SessionBuilder isolation(Isolation isolation);

        /**
         * Optional,default is {@link TmSessionFactory#readonly()}
         */
        SessionBuilder readOnly(boolean readOnly);

        /**
         * Optional,default is {@code -1}
         */
        SessionBuilder timeout(int timeoutSeconds);

        TmSession build() throws SessionException;

    }
}
