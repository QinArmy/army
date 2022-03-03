package io.army.jdbc;

import io.army.lang.Nullable;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.utils._SyncExceptions;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class JdbcMetaExecutor implements MetaExecutor {

    static JdbcMetaExecutor create(Connection conn) {
        return new JdbcMetaExecutor(conn);
    }

    private final Connection conn;

    private JdbcMetaExecutor(Connection conn) {
        this.conn = conn;
    }


    @Override
    public _SchemaInfo extractInfo() throws DataAccessException {
        final Connection conn = this.conn;

        try {
            final DatabaseMetaData metaData;
            metaData = conn.getMetaData();
            final String catalog, schema;
            catalog = conn.getCatalog();
            schema = conn.getSchema();

            final Map<String, _TableInfo.Builder> tableBuilderMap;
            tableBuilderMap = getTableBuilder(catalog, schema, metaData);

            appendColumn(catalog, schema, metaData, tableBuilderMap);
            final _IndexInfo.Builder indexBuilder = _IndexInfo.builder();
            for (_TableInfo.Builder tableBuilder : tableBuilderMap.values()) {
                appendIndex(catalog, schema, metaData, true, tableBuilder, indexBuilder);
                appendIndex(catalog, schema, metaData, false, tableBuilder, indexBuilder);
            }
            return _SchemaInfo.create(catalog, schema, tableBuilderMap);
        } catch (SQLException e) {
            throw _SyncExceptions.wrapDataAccess(e);
        }
    }

    @Override
    public void executeDdl(final List<String> ddlList) throws DataAccessException {
        try (Statement stmt = this.conn.createStatement()) {
            for (String ddl : ddlList) {
                stmt.addBatch(ddl);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw _SyncExceptions.wrapDataAccess(e);
        }
    }

    @Override
    public void close() throws DataAccessException {
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw _SyncExceptions.wrapDataAccess(e);
        }
    }


    private Map<String, _TableInfo.Builder> getTableBuilder(final String catalog, final String schema
            , final DatabaseMetaData metaData) throws SQLException {

        try (ResultSet resultSet = metaData.getTables(catalog, schema, "%", new String[]{"TABLE", "VIEW"})) {
            final Map<String, _TableInfo.Builder> builderMap = new HashMap<>();
            for (String tableName, type; resultSet.next(); ) {
                tableName = resultSet.getString("TABLE_NAME");
                type = resultSet.getString("TABLE_TYPE");

                final _TableInfo.Builder builder;
                builder = _TableInfo.builder(tableName)
                        .comment(resultSet.getString("REMARKS"));
                switch (type) {
                    case "TABLE":
                        builder.type(_TableType.TABLE);
                        break;
                    case "VIEW":
                        builder.type(_TableType.VIEW);
                        break;
                    default: {
                        String m = String.format("%s table type[%s] unexpected.", tableName, type);
                        throw new DataAccessException(m);
                    }
                }
                builderMap.putIfAbsent(tableName, builder);

            }// for
            return builderMap;
        }//try

    }

    private void appendColumn(final @Nullable String catalog, final @Nullable String schema
            , final DatabaseMetaData metaData, final Map<String, _TableInfo.Builder> tableBuilderMap)
            throws SQLException {

        try (ResultSet resultSet = metaData.getColumns(catalog, schema, "%", "%")) {
            _TableInfo.Builder tableBuilder = null;
            final _ColumnInfo.Builder builder = _ColumnInfo.builder();
            for (String tableName, currentTableName = null, nullable; resultSet.next(); ) {

                tableName = resultSet.getString("TABLE_NAME");
                if (!tableName.equals(currentTableName)) {
                    currentTableName = tableName;
                    tableBuilder = tableBuilderMap.get(tableName);
                }
                if (tableBuilder == null) {
                    continue;
                }

                builder.name(resultSet.getString("COLUMN_NAME"))
                        .type(resultSet.getString("TYPE_NAME"))
                        .defaultExp(resultSet.getString("COLUMN_DEF"))
                        .defaultExp(resultSet.getString("COLUMN_DEF"))

                        .comment(resultSet.getString("REMARKS"))
                        .autoincrement("YES".equals(resultSet.getString("IS_AUTOINCREMENT")))
                        .precision(resultSet.getInt("COLUMN_SIZE"));
                nullable = resultSet.getString("IS_NULLABLE");
                switch (nullable) {
                    case "YES":
                        builder.nullable(Boolean.TRUE);
                        break;
                    case "NO":
                        builder.nullable(Boolean.FALSE);
                        break;
                    case "":
                        builder.nullable(null);
                    default: {
                        String m = String.format("IS_NULLABLE is excepted value[%s]", nullable);
                        throw new DataAccessException(m);
                    }


                }

                switch (JDBCType.valueOf(resultSet.getInt("DATA_TYPE"))) {
                    case DECIMAL:
                        builder.scale(resultSet.getInt("DECIMAL_DIGITS"));
                        break;
                    case TIME:
                    case TIMESTAMP:
                    case TIME_WITH_TIMEZONE:
                    case TIMESTAMP_WITH_TIMEZONE:
                        builder.scale(resultSet.getInt("SQL_DATETIME_SUB"));
                        break;
                    default:
                        builder.scale(-1);
                }

                tableBuilder.appendColumn(builder.buildAndClear());

            }


        }//try

    }

    private void appendIndex(final @Nullable String catalog, final @Nullable String schema
            , final DatabaseMetaData metaData, final boolean unique
            , final _TableInfo.Builder tableBuilder, _IndexInfo.Builder builder) throws SQLException {
        final String tableName = tableBuilder.name();
        try (ResultSet resultSet = metaData.getIndexInfo(catalog, schema, tableName, unique, false)) {
            Boolean asc;
            for (String ascStr, indexName, lastIndexName = null; resultSet.next(); ) {
                if (!tableName.equals(resultSet.getString("TABLE_NAME"))) {
                    throw new DataAccessException(String.format("Table[%s] not match.", tableName));
                }
                indexName = resultSet.getString("INDEX_NAME");
                if (lastIndexName == null) {
                    builder.name(indexName);
                    lastIndexName = indexName;
                }
                ascStr = resultSet.getString("ASC_OR_DESC");
                if (ascStr == null) {
                    asc = null;
                } else if (ascStr.equals("A")) {
                    asc = Boolean.TRUE;
                } else if (ascStr.equals("D")) {
                    asc = Boolean.FALSE;
                } else {
                    String m = String.format("Index ASC_OR_DESC[%s] error, for table[%s] index[%s]"
                            , ascStr, tableName, indexName);
                    throw new DataAccessException(m);
                }

                if (indexName.equals(lastIndexName)) {
                    builder.appendColumn(resultSet.getString("COLUMN_NAME"), asc);
                } else {
                    builder.unique(!resultSet.getBoolean("NON_UNIQUE"));
                    tableBuilder.appendIndex(builder.buildAndClear());
                    lastIndexName = null;
                }

            }//for

        }


    }


}
