package io.army.boot.sync;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.sync.GenericSyncSession;

import java.util.List;

public interface GenericSyncApiSession extends GenericSyncSession {

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);
}
