package io.army.jdbd;

import io.army.mapping.MappingType;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.ArmyOption;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.jdbd.meta.JdbdType;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.*;
import io.jdbd.statement.ParametrizedStatement;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.*;
import java.util.BitSet;
import java.util.UUID;

abstract class PostgreStmtExecutor<S extends DatabaseSession> extends JdbdStmtExecutor {

    static ReactiveLocalStmtExecutor localExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
        throw new UnsupportedOperationException();
    }

    static ReactiveRmStmtExecutor rmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    static io.jdbd.session.Option<?> mapToJdbdDialectOption(final ArmyOption<?> option) {
        final io.jdbd.session.Option<?> jdbdOption;
        if (DEFERRABLE.equals(option)) {
            jdbdOption = io.jdbd.session.Option.DEFERRABLE;
        } else {
            jdbdOption = null;
        }
        return jdbdOption;
    }

    @Nullable
    static ArmyOption<?> mapToArmyDialectOption(final io.jdbd.session.Option<?> option) {
        final ArmyOption<?> armyOption;
        if (io.jdbd.session.Option.DEFERRABLE.equals(option)) {
            armyOption = DEFERRABLE;
        } else {
            armyOption = null;
        }
        return armyOption;
    }


    /**
     * <p>
     * Transaction option of some database(eg: PostgreSQL)
     * <br/>
     *
     * @see LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)
     * @see <a href="https://www.postgresql.org/docs/current/sql-start-transaction.html">postgre : DEFERRABLE</a>
     */
    private static final ArmyOption<Boolean> DEFERRABLE = ArmyOption.from("DEFERRABLE", Boolean.class);


    /**
     * private constructor
     */
    private PostgreStmtExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String name) {
        super(factory, session, name);
    }

    @Override
    final DataType getDataType(ResultRowMeta meta, int indexBasedZero) {
        return getPostgreType(meta.getDataType(indexBasedZero).typeName());
    }

    @Override
    final void bind(ParametrizedStatement statement, final int indexBasedZero, final MappingType type,
                    final DataType dataType, final @Nullable Object value) {

        if (!(dataType instanceof PostgreType)) {
            if (!(value instanceof String)) {
                throw beforeBindMethodError(type, dataType, value);
            }
            statement.bind(indexBasedZero, io.jdbd.meta.DataType.userDefined(dataType.typeName()), value);
        } else if (dataType.isArray()) {
            if (!(value instanceof String)) {
                throw beforeBindMethodError(type, dataType, value);
            }
            statement.bind(indexBasedZero, io.jdbd.meta.DataType.buildIn(dataType.typeName()), value);
        } else switch ((PostgreType) dataType) {
            case NO_CAST_INTEGER:
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, JdbdType.INTEGER, value);
            }
            break;
            case PG_LSN: {
                if (!(value instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, io.jdbd.meta.DataType.buildIn(dataType.typeName()), value);
            }
            break;
            case UUID: {
                if (!(value instanceof UUID)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, io.jdbd.meta.DataType.buildIn(dataType.typeName()), value);
            }
            break;
            case BIT: {
                if (!(value instanceof BitSet)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, JdbdType.BIT, value);
            }
            break;
            case VARBIT: {
                if (!(value instanceof BitSet)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, JdbdType.VARBIT, value);
            }
            break;
            case MONEY: {
                if (!(value instanceof BigDecimal || value instanceof String)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                statement.bind(indexBasedZero, io.jdbd.meta.DataType.buildIn(dataType.typeName()), value);
            }
            break;
            case TEXT: // postgre client protocol body must less than 2^32 byte
            case NO_CAST_TEXT:
                statement.bind(indexBasedZero, JdbdType.MEDIUMTEXT, toJdbdLongTextValue(type, dataType, value));
                break;
            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:

            case INTERVAL:

            case TSVECTOR:
            case TSQUERY:

            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case DATERANGE:
            case TSTZRANGE:

            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case TSMULTIRANGE:
            case DATEMULTIRANGE:
            case TSTZMULTIRANGE:

            case PG_SNAPSHOT:

            case BOX:
            case LSEG:
            case LINE:
            case PATH:
            case POINT:
            case CIRCLE:
            case POLYGON:

            case CIDR:
            case INET:
            case MACADDR8:
            case MACADDR:
            case ACLITEM:
                statement.bind(indexBasedZero, io.jdbd.meta.DataType.buildIn(dataType.typeName()),
                        toJdbdLongTextValue(type, dataType, value)
                );
                break;
            case REF_CURSOR:
            case UNKNOWN:
                throw mapMethodError(type, dataType);
            default:
                bindArmyType(statement, indexBasedZero, type, dataType, ((PostgreType) dataType).armyType(), value);

        }
    }


    @Nullable
    @Override
    final Object get(final DataRow row, final int indexBasedZero, final MappingType type, final DataType dataType) {
        final Object value;
        if (!(dataType instanceof PostgreType)) {
            if ("hstore" .equalsIgnoreCase(dataType.typeName())) {
                value = row.getMap(indexBasedZero, String.class, Object.class);
            } else {
                value = row.get(indexBasedZero, String.class);
            }
        } else switch ((PostgreType) dataType) {
            case BOOLEAN:
                value = row.get(indexBasedZero, Boolean.class);
                break;
            case SMALLINT:
                value = row.get(indexBasedZero, Short.class);
                break;
            case NO_CAST_INTEGER:
            case INTEGER:
                value = row.get(indexBasedZero, Integer.class);
                break;
            case BIGINT:
            case PG_LSN:
                value = row.get(indexBasedZero, Long.class);
                break;
            case DECIMAL:
                value = row.get(indexBasedZero, BigDecimal.class);
                break;
            case FLOAT8:
                value = row.get(indexBasedZero, Double.class);
                break;
            case REAL:
                value = row.get(indexBasedZero, Float.class);
                break;

            case BYTEA: // postgre client protocol body must less than 2^32 byte
                value = row.get(indexBasedZero, byte[].class);
                break;
            case TIME:
                value = row.get(indexBasedZero, LocalTime.class);
                break;
            case DATE:
                value = row.get(indexBasedZero, LocalDate.class);
                break;
            case TIMETZ:
                value = row.get(indexBasedZero, OffsetTime.class);
                break;
            case TIMESTAMP:
                value = row.get(indexBasedZero, LocalDateTime.class);
                break;
            case TIMESTAMPTZ:
                value = row.get(indexBasedZero, OffsetDateTime.class);
                break;
            case UUID:
                value = row.get(indexBasedZero, UUID.class);
                break;
            case BIT:
            case VARBIT:
                value = row.get(indexBasedZero, BitSet.class);
                break;
            case CHAR:
            case VARCHAR:
            case TEXT:
            case NO_CAST_TEXT:  // postgre client protocol body must less than 2^32 byte

            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:

            case INTERVAL:

            case TSVECTOR:
            case TSQUERY:

            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case DATERANGE:
            case TSTZRANGE:

            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case TSMULTIRANGE:
            case DATEMULTIRANGE:
            case TSTZMULTIRANGE:

            case PG_SNAPSHOT:

            case BOX:
            case LSEG:
            case LINE:
            case PATH:
            case POINT:
            case CIRCLE:
            case POLYGON:

            case CIDR:
            case INET:
            case MACADDR8:
            case MACADDR:
            case ACLITEM:

            case MONEY:

            case BOOLEAN_ARRAY:
            case INTEGER_ARRAY:
            case SMALLINT_ARRAY:
            case BIGINT_ARRAY:
            case DECIMAL_ARRAY:
            case REAL_ARRAY:
            case FLOAT8_ARRAY:

            case CHAR_ARRAY:
            case VARCHAR_ARRAY:
            case TEXT_ARRAY:

            case BYTEA_ARRAY:

            case DATE_ARRAY:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:
            case INTERVAL_ARRAY:

            case BIT_ARRAY:
            case VARBIT_ARRAY:
            case UUID_ARRAY:

            case CIDR_ARRAY:
            case INET_ARRAY:
            case MACADDR_ARRAY:
            case MACADDR8_ARRAY:

            case JSON_ARRAY:
            case JSONB_ARRAY:
            case JSONPATH_ARRAY:
            case XML_ARRAY:

            case POINT_ARRAY:
            case LINE_ARRAY:
            case LSEG_ARRAY:
            case PATH_ARRAY:
            case BOX_ARRAY:
            case CIRCLE_ARRAY:
            case POLYGON_ARRAY:

            case TSQUERY_ARRAY:
            case TSVECTOR_ARRAY:

            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case DATERANGE_ARRAY:
            case TSRANGE_ARRAY:
            case TSTZRANGE_ARRAY:

            case INT4MULTIRANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:

            case MONEY_ARRAY:
            case ACLITEM_ARRAY:
            case PG_LSN_ARRAY:
            case PG_SNAPSHOT_ARRAY:
            case REF_CURSOR:
                value = row.get(indexBasedZero, String.class);
                break;
            case UNKNOWN:
            default:
                value = row.get(indexBasedZero);
        }
        return value;

    }


}
