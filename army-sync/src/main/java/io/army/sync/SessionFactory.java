package io.army.sync;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.session.DialectSessionFactory;
import io.army.session.FactoryMode;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 * <ul>
 *     <li>{@link FactoryMode#NO_SHARDING}</li>
 *     <li>{@link FactoryMode#TABLE_SHARDING}</li>
 * </ul>
 */
public interface SessionFactory extends GenericSyncApiSessionFactory, DialectSessionFactory {

    @Override
    ProxySession proxySession();

    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder name(@Nullable String name);

        /**
         * Optional,default is {@code false}
         */
        SessionBuilder currentSession(boolean current);

        /**
         * Optional,default is {@link SessionFactory#readonly()}
         */
        SessionBuilder readOnly(boolean readOnly);

        /**
         * Optional,default is {@code true}
         */
        @Deprecated
        SessionBuilder resetConnection(boolean reset);

        Session build() throws SessionException;

    }
}
