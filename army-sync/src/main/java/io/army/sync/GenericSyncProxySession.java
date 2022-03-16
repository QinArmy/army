package io.army.sync;

import io.army.session.GenericCurrentSession;

public interface GenericSyncProxySession extends GenericCurrentSession, GenericSyncApiSession {

    boolean hasCurrentSession();

}
