package io.army.session.mysql;

import io.army.session.ArmyOption;

public abstract class MySQLOption {

    private MySQLOption() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Transaction option of some database(eg: MySQL)
     * <br/>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : WITH CONSISTENT SNAPSHOT</a>
     */
    public static final ArmyOption<Boolean> WITH_CONSISTENT_SNAPSHOT = ArmyOption.from("WITH CONSISTENT SNAPSHOT", Boolean.class);


}
