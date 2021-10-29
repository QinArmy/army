package io.army.sync;

import io.army.GenericProxySession;

public interface GenericSyncProxySession extends GenericProxySession, GenericSyncApiSession {

    boolean hasCurrentSession();

}
