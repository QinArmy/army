package io.army.jdbd;

import io.army.session.TransactionStatus;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.TransactionOption;

/**
 * <p>This class is MySQL {@link JdbdStmtExecutor}.
 *
 * @since 1.0
 */
abstract class MySQLStmtExecutor<S extends DatabaseSession> extends JdbdStmtExecutor<S> {


    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        super(factory, session);
    }


    @Override
    final TransactionStatus mapToArmyTransactionStatus(io.jdbd.session.TransactionStatus jdbdStatus) {

        return null;
    }

    @Override
    final TransactionOption mapToJdbdTransactionOption(io.army.session.TransactionOption armyOption) {
        return null;
    }


}
