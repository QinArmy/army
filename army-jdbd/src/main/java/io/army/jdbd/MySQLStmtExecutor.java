package io.army.jdbd;

import io.army.mapping.MappingType;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.Option;
import io.army.sqltype.DataType;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import io.jdbd.statement.ParametrizedStatement;

import javax.annotation.Nullable;

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

    @Nullable
    static io.jdbd.session.Option<?> mapToJdbdDialectOption(Option<?> option) {
        return null;
    }

    @Nullable
    static Option<?> mapToArmyDialectOption(io.jdbd.session.Option<?> option) {
        return null;
    }

    private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

    /**
     * private constructor
     */
    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String name) {
        super(factory, session, name);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/data-types.html">MySQL Data Types</a>
     */
    @Override
    final DataType getDataType(final ResultRowMeta meta, final int indexBasedZero) {
        return getMySqlType(meta.getDataType(indexBasedZero).typeName());
    }

    @Nullable
    @Override
    final Object get(final DataRow row, final int indexBasedZero, final DataType dataType) {

        return null;
    }

    @Override
    final void bind(ParametrizedStatement statement, final int indexBasedZero, final MappingType type,
                    final DataType dataType, final @Nullable Object value) {

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
