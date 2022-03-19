package io.army.sync;

import io.army.session.SessionException;

import java.io.Flushable;

public interface GenericSyncApiSession extends SyncSession, Flushable {


    @Override
    void flush() throws SessionException;
}
