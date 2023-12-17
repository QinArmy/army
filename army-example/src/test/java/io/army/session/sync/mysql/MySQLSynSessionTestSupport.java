package io.army.session.sync.mysql;

import io.army.dialect.Database;
import io.army.session.SyncSessionTestSupport;


abstract class MySQLSynSessionTestSupport extends SyncSessionTestSupport {

    MySQLSynSessionTestSupport() {
        super(Database.MySQL);
    }


}
