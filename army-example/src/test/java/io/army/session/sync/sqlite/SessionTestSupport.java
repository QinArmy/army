package io.army.session.sync.sqlite;

import io.army.dialect.Database;
import io.army.session.sync.SyncSessionTestSupport;


abstract class SessionTestSupport extends SyncSessionTestSupport {

    SessionTestSupport() {
        super(Database.SQLite);
    }


}
