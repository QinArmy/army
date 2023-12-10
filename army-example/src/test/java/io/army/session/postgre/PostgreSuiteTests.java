package io.army.session.postgre;

import io.army.dialect.Database;
import io.army.session.SyncSessionTestSupport;

abstract class PostgreSuiteTests extends SyncSessionTestSupport {

    PostgreSuiteTests() {
        super(Database.PostgreSQL);
    }


}
