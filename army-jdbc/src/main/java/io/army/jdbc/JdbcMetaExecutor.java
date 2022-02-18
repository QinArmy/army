package io.army.jdbc;

import io.army.lang.Nullable;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.utils._SyncExceptions;

import java.sql.*;
import java.util.HashMap;
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

            appendIndex(catalog, schema, metaData, true, tableBuilderMap);
            appendIndex(catalog, schema, metaData, false, tableBuilderMap);

            return _SchemaInfo.create(catalog, schema, tableBuilderMap);
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
            for (String tableName, currentTableName = null; resultSet.next(); ) {

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
                        .defaultExp(resultSet.getString(""))
                        .nullable(!"NO".equals(resultSet.getString("IS_NULLABLE")))

                        .defaultExp(resultSet.getString("COLUMN_DEF"))
                        .comment(resultSet.getString("REMARKS"))
                        .autoincrement("YES".equals(resultSet.getString("IS_AUTOINCREMENT")))
                        .precision(resultSet.getInt("COLUMN_SIZE"));

                switch (JDBCType.valueOf(resultSet.getString("DATA_TYPE"))) {
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
            , final Map<String, _TableInfo.Builder> tableBuilderMap) throws SQLException {

        try (ResultSet resultSet = metaData.getIndexInfo(catalog, schema, "%", unique, false)) {

            _TableInfo.Builder tableBuilder = null;
            final _IndexInfo.Builder builder = _IndexInfo.builder();
            Boolean asc;
            for (String ascStr, indexName, lastIndexName = null, tableName, lastTableName = null; resultSet.next(); ) {
                tableName = resultSet.getString("TABLE_NAME");

                if (lastTableName == null) {
                    lastTableName = tableName;
                    tableBuilder = tableBuilderMap.get(tableName);
                    if (tableBuilder == null) {
                        lastTableName = null;
                        continue;
                    }
                }
                indexName = resultSet.getString("INDEX_NAME");
                if (lastIndexName == null) {
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

                if (!tableName.equals(lastTableName)) {
                    lastTableName = null;
                }
                if (lastTableName != null && indexName.equals(lastIndexName)) {
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
