package io.army.boot.sync;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.sync.GenericSyncSession;

public interface GenericSyncApiSession extends GenericSyncSession {

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);


}
