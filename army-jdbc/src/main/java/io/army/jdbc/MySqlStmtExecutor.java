package io.army.jdbc;


import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.sync.utils._SyncExceptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

final class MySqlStmtExecutor extends AbstractStmtExecutor {

    static MySqlStmtExecutor create(Connection conn) {
        return new MySqlStmtExecutor(conn);
    }

    private MySqlStmtExecutor(Connection conn) {
        super(conn);
    }


    @Override
    void bind(final PreparedStatement stmt, final int index, final SqlType sqlDataType, final Object nonNull)
            throws SQLException {
        switch ((MySqlType) sqlDataType) {
            case BOOLEAN:
                stmt.setBoolean(index, (Boolean) nonNull);
                break;
            case TINYINT:
                stmt.setByte(index, (Byte) nonNull);
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT:
                stmt.setShort(index, (Short) nonNull);
                break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                stmt.setInt(index, (Integer) nonNull);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                stmt.setLong(index, (Long) nonNull);
                break;
            case BIGINT_UNSIGNED:
                stmt.setObject(index, nonNull, JDBCType.BIGINT);
                break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                stmt.setBigDecimal(index, (BigDecimal) nonNull);
                break;
            case FLOAT:
                stmt.setFloat(index, (Float) nonNull);
                break;
            case DOUBLE:
                stmt.setDouble(index, (Double) nonNull);
                break;
            case TIME:
                stmt.setTime(index, Time.valueOf(((LocalTime) nonNull)));
                break;
            case DATE:
                stmt.setDate(index, Date.valueOf((LocalDate) nonNull));
                break;
            case DATETIME:
                stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) nonNull));
                break;
            case ENUM: {
                if (nonNull instanceof Integer) {
                    stmt.setInt(index, (Integer) nonNull);
                } else {
                    stmt.setString(index, (String) nonNull);
                }
            }
            break;
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
                stmt.setString(index, (String) nonNull);
                break;
            case JSON:
            case LONGTEXT: {
                setLongText(stmt, index, nonNull);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
                stmt.setBytes(index, (byte[]) nonNull);
                break;
            case LONGBLOB: {
                setLongBinary(stmt, index, nonNull);
            }
            break;
            case SET: {
                final Set<?> set = (Set<?>) nonNull;
                final StringBuilder builder = new StringBuilder();
                int i = 0;
                for (Object o : set) {
                    if (i > 0) {
                        builder.append(',');
                    }
                    if (o instanceof Enum) {
                        builder.append(((Enum<?>) o).name());
                    } else if (o instanceof String) {
                        builder.append(o);
                    } else {
                        String m = String.format("Parameter[%s] element of %s error.", index, Set.class.getName());
                        throw new SQLException(m);
                    }
                    i++;
                }
                stmt.setString(index, builder.toString());
            }
            break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTILINESTRING:
            case MULTIPOLYGON:
            case GEOMETRYCOLLECTION: {
                if (nonNull instanceof String) {
                    stmt.setString(index, (String) nonNull);
                } else if (nonNull instanceof Reader) {
                    stmt.setCharacterStream(index, (Reader) nonNull);
                } else if (nonNull instanceof byte[]) {
                    stmt.setBytes(index, (byte[]) nonNull);
                } else if (nonNull instanceof InputStream) {
                    stmt.setBinaryStream(index, (InputStream) nonNull);
                } else if (nonNull instanceof Path) {
                    try (InputStream inputStream = Files.newInputStream((Path) nonNull, StandardOpenOption.READ)) {
                        stmt.setBinaryStream(index, inputStream);
                    } catch (IOException e) {
                        String m = String.format("Parameter[%s] %s[%s] read occur error."
                                , index, Path.class.getName(), nonNull);
                        throw new SQLException(m, e);
                    }
                }
            }
            default:
                throw _SyncExceptions.unexpectedEnum((MySqlType) sqlDataType);

        }

    }


}
