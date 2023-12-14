package io.army.session.mysql;

import io.army.dialect.Database;
import io.army.session.ReactiveSessionTestSupport;

abstract class MySQLReactiveSessionTestsSupport extends ReactiveSessionTestSupport {


    MySQLReactiveSessionTestsSupport() {
        super(Database.MySQL);
    }

}
