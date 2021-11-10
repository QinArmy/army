package io.army.sync;

import io.army.SessionException;

import java.io.Flushable;

public interface GenericSyncApiSession extends GenericSyncSession, Flushable {

    @Override
    void flush() throws SessionException;
}
