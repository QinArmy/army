package io.army.sync;

import io.army.session.GenericProxySession;

public interface GenericSyncProxySession extends GenericProxySession, GenericSyncApiSession {

    boolean hasCurrentSession();

}
