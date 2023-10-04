package io.army.jdbd;

import io.army.tx.TransactionInfo;
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
    final TransactionInfo mapToArmyTransactionStatus(io.jdbd.session.TransactionStatus jdbdStatus) {

        return null;
    }

    @Override
    final TransactionOption mapToJdbdTransactionOption(io.army.tx.TransactionOption armyOption) {
        return null;
    }


}
