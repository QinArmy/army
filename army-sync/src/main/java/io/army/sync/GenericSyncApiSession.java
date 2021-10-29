package io.army.sync;

import io.army.SessionException;
import io.army.sync.GenericSyncSession;

import java.io.Flushable;

public interface GenericSyncApiSession extends GenericSyncSession, Flushable {

    @Override
    void flush() throws SessionException;
}
