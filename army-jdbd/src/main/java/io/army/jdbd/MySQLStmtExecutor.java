package io.army.jdbd;

import io.army.mapping.MappingType;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.Option;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import io.jdbd.statement.ParametrizedStatement;

import javax.annotation.Nullable;
import java.util.Locale;

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


    @Override
    final DataType getDataType(final ResultRowMeta meta, final int indexBasedZero) {

        final String typeName;
        typeName = meta.getDataType(indexBasedZero).typeName();

        final MySQLType type;
        switch (typeName.toUpperCase(Locale.ROOT)) {
            case "BOOLEAN":
            case "TINYINT":
            case "TINYINT UNSIGNED":
            case "SMALLINT":

            case "SMALLINT UNSIGNED":
            case "MEDIUMINT":
            case "MEDIUMINT UNSIGNED":
            case "INT":

            case "INT_UNSIGNED":
            case "BIGINT":
            case "BIGINT UNSIGNED":
            case "DECIMAL":

            case "DECIMAL UNSIGNED":
            case "FLOAT":
            case "FLOAT UNSIGNED":
            case "DOUBLE":

            case "DOUBLE UNSIGNED":
            case "TIME":
            case "DATE":
            case "YEAR":

            case "DATETIME":
            case "TIMESTAMP":
            case "CHAR":
            case "VARCHAR":

            case "BIT":
            case "ENUM":
            case "SET":
            case "JSON":

            case "TINYTEXT":
            case "MEDIUMTEXT":
            case "TEXT":
            case "LONGTEXT":

            case "BINARY":
            case "VARBINARY":
            case "TINYBLOB":
            case "MEDIUMBLOB":

            case "BLOB":
            case "LONGBLOB":
            case "GEOMETRY":
            case "UNKNOWN":
            default:
                type = MySQLType.UNKNOWN;

        }
        return null;
    }

    @Nullable
    @Override
    final Object get(DataRow row, int indexBasedZero, DataType dataType) {
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
