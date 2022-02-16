package io.army.jdbc;

import io.army.lang.Nullable;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.utils._SyncExceptions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        final DatabaseMetaData metaData;
        try {
            metaData = conn.getMetaData();
            final String catalog, schema;
            catalog = conn.getCatalog();
            schema = conn.getSchema();

            final List<_TableInfo.Builder> tableBuilderList;
            tableBuilderList = getTableBuilder(catalog, schema, metaData);
            for (_TableInfo.Builder builder : tableBuilderList) {
                appendColumn(catalog, schema, metaData, builder);
            }

            return null;
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


    private List<_TableInfo.Builder> getTableBuilder(final String catalog, final String schema
            , final DatabaseMetaData metaData) throws SQLException {

        try (ResultSet resultSet = metaData.getTables(catalog, schema, "%", new String[]{"TABLE", "VIEW"})) {
            final List<_TableInfo.Builder> builderList = new ArrayList<>();
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
                builderList.add(builder);

            }// for
            return builderList;
        }//try

    }

    private void appendColumn(final @Nullable String catalog, final @Nullable String schema
            , final DatabaseMetaData metaData, final _TableInfo.Builder tableBuilder) throws SQLException {

        try (ResultSet resultSet = metaData.getColumns(catalog, schema, tableBuilder.name(), "%")) {

            for (_FieldInfo.Builder builder = _FieldInfo.builder(); resultSet.next(); ) {

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

                tableBuilder.appendField(builder.buildAndClear());
            }


        }//try

    }

    private void appendIndex(final @Nullable String catalog, final @Nullable String schema
            , final DatabaseMetaData metaData, final boolean unique
            , final _TableInfo.Builder tableBuilder) throws SQLException {

        try (ResultSet resultSet = metaData.getIndexInfo(catalog, schema, tableBuilder.name(), unique, false)) {
            final _IndexInfo.Builder builder = _IndexInfo.builder();
            for (String asc; resultSet.next(); ) {
                builder.name(resultSet.getString("INDEX_NAME"))
                        .type("");
                asc = resultSet.getString("ASC_OR_DESC");
                if (asc == null) {

                } else {

                }
            }

        }


    }


}
