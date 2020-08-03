package io.army.sync;

import io.army.GenericRmSessionFactory;
import io.army.SessionException;
import io.army.ShardingMode;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 * <ul>
 *     <li>{@link ShardingMode#NO_SHARDING}</li>
 *     <li>{@link ShardingMode#SINGLE_DATABASE_SHARDING}</li>
 * </ul>
 */
public interface SessionFactory extends GenericSyncApiSessionFactory, GenericRmSessionFactory {

    @Override
    ProxySession proxySession();

    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        SessionBuilder resetConnection(boolean reset);

        Session build() throws SessionException;

    }
}
