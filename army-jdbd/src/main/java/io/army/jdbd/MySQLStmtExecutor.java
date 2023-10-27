package io.army.jdbd;

import io.army.mapping.MappingType;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.Option;
import io.army.sqltype.SqlType;
import io.jdbd.meta.DataType;
import io.jdbd.result.DataRow;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;

/**
 * <p>This class is MySQL {@link JdbdStmtExecutor}.
 *
 * @since 1.0
 */
abstract class MySQLStmtExecutor extends JdbdStmtExecutor {

    static ReactiveLocalStmtExecutor localExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
        return new LocalExecutor(factory, session, name);
    }

    static ReactiveRmStmtExecutor rmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
        return new RmExecutor(factory, session, name);
    }

    private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

    /**
     * private constructor
     */
    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String name) {
        super(factory, session, name);
    }


    @Override
    final DataType mapToJdbdDataType(MappingType mappingType, SqlType sqlType) {
        return null;
    }

    @Override
    final SqlType getColumnMeta(DataRow row, int indexBasedZero) {
        return null;
    }

    @Override
    final Object get(DataRow row, int indexBasedZero, SqlType sqlType) {
        return null;
    }

    @Override
    final io.jdbd.session.Option<?> mapToJdbdDialectOption(Option<?> option) {
        return null;
    }

    @Override
    final Option<?> mapToArmyDialectOption(io.jdbd.session.Option<?> option) {
        return null;
    }


    private static final class LocalExecutor extends MySQLStmtExecutor implements ReactiveLocalStmtExecutor {

        private LocalExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
            super(factory, session, name);
        }

    } // LocalExecutor

    private static final class RmExecutor extends MySQLStmtExecutor implements ReactiveRmStmtExecutor {

        private RmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
            super(factory, session, name);
        }

    } // RmExecutor


}
