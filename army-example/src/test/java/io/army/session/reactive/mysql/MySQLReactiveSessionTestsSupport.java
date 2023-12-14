package io.army.session.reactive.mysql;

import io.army.dialect.Database;
import io.army.session.reactive.ReactiveSessionTestSupport;

abstract class MySQLReactiveSessionTestsSupport extends ReactiveSessionTestSupport {


    MySQLReactiveSessionTestsSupport() {
        super(Database.MySQL);
    }

}
