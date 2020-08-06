package io.army.boot.sync;

import io.army.SessionException;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.sync.GenericSyncSession;

import java.io.Flushable;

public interface GenericSyncApiSession extends GenericSyncSession, Flushable {

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);


    @Override
    void flush() throws SessionException;
}
