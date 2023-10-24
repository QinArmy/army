package io.army.jdbd;

import io.army.session.Option;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Isolation;

/**
 * <p>This class is MySQL {@link JdbdStmtExecutor}.
 *
 * @since 1.0
 */
abstract class MySQLStmtExecutor<S extends DatabaseSession> extends JdbdStmtExecutor<S> {

    private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        super(factory, session);
    }


    @Override
    TransactionInfo mapToArmyTransactionInfo(io.jdbd.session.TransactionInfo jdbdInfo) {
        return null;
    }

    @Override
    final io.jdbd.session.TransactionOption mapToJdbdTransactionOption(TransactionOption armyOption) {
        final Isolation jdbdIsolation;
        jdbdIsolation = mapToStandardJdbdIsolation(armyOption.isolation());

        final io.jdbd.session.TransactionOption jdbdOption;
        final Boolean consistentSnapshot;
        consistentSnapshot = armyOption.valueOf(WITH_CONSISTENT_SNAPSHOT);

        if (consistentSnapshot == null) {
            jdbdOption = io.jdbd.session.TransactionOption.option(jdbdIsolation, armyOption.isReadOnly());
        } else {
            jdbdOption = io.jdbd.session.TransactionOption.builder()
                    .option(io.jdbd.session.Option.ISOLATION, jdbdIsolation)
                    .option(io.jdbd.session.Option.READ_ONLY, armyOption.isReadOnly())
                    .option(io.jdbd.session.Option.WITH_CONSISTENT_SNAPSHOT, consistentSnapshot)
                    .build();
        }
        return jdbdOption;
    }


}
