package io.army.jdbd;

import io.army.session.Option;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.TransactionOption;

import java.util.function.Function;

/**
 * <p>This class is MySQL {@link JdbdStmtExecutor}.
 *
 * @since 1.0
 */
abstract class MySQLStmtExecutor<S extends DatabaseSession>
        extends JdbdStmtExecutor<S> {


    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        super(factory, session);
    }


    @Override
    final Function<Option<?>, ?> readJdbdTransactionOptions(TransactionOption jdbdOption) {
        return null;
    }

    @Override
    final Function<io.jdbd.session.Option<?>, ?> readArmyTransactionOptions(io.army.session.TransactionOption jdbdOption) {
        return null;
    }

    @Override
    final Function<io.jdbd.session.Option<?>, ?> readArmySetSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return io.jdbd.session.Option.EMPTY_OPTION_FUNC;
    }

    @Override
    final Function<io.jdbd.session.Option<?>, ?> readArmyReleaseSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return io.jdbd.session.Option.EMPTY_OPTION_FUNC;
    }

    @Override
    final Function<io.jdbd.session.Option<?>, ?> readArmyRollbackSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return io.jdbd.session.Option.EMPTY_OPTION_FUNC;
    }


}
