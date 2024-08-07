/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.jdbc;

import io.army.executor.DataAccessException;
import io.army.executor.SyncMetaExecutor;
import io.army.schema.*;
import io.army.util._Collections;

import io.army.lang.Nullable;
import javax.sql.XAConnection;
import java.sql.*;
import java.util.List;
import java.util.Map;

class JdbcMetaExecutor implements SyncMetaExecutor {

    static JdbcMetaExecutor from(JdbcExecutorFactory factory, Connection conn) {
        return new JdbcMetaExecutor(factory, conn);
    }

    static JdbcMetaExecutor fromXa(JdbcExecutorFactory factory, XAConnection xaConn) {
        try {
            final Connection conn;
            conn = xaConn.getConnection();

            return new XaConnMetaExecutor(factory, xaConn, conn);
        } catch (SQLException e) {
            try {
                xaConn.close();
            } catch (SQLException ex) {
                // ignore ex
                throw JdbcExecutor.wrapError(e);
            }
            throw JdbcExecutor.wrapError(e);
        }
    }


    // private static final Logger LOG = LoggerFactory.getLogger(JdbcMetaExecutor.class);

    private final JdbcExecutorFactory factory;

    private final Connection conn;

    /**
     * private constructor
     */

    private JdbcMetaExecutor(JdbcExecutorFactory factory, Connection conn) {
        this.factory = factory;
        this.conn = conn;
    }


    @Override
    public final SchemaInfo extractInfo() throws DataAccessException {
        final Connection conn = this.conn;

        try {
            final DatabaseMetaData metaData;
            metaData = conn.getMetaData();
            final String catalog, schema;
            catalog = conn.getCatalog();
            schema = conn.getSchema();

            final Map<String, TableInfo.Builder> tableBuilderMap;
            tableBuilderMap = getTableBuilder(catalog, schema, metaData);

            appendColumn(catalog, schema, metaData, tableBuilderMap);
            final IndexInfo.Builder indexBuilder = IndexInfo.builder();
            for (TableInfo.Builder tableBuilder : tableBuilderMap.values()) {
                appendIndex(catalog, schema, metaData, true, tableBuilder, indexBuilder);
                appendIndex(catalog, schema, metaData, false, tableBuilder, indexBuilder);
            }

            return SchemaInfo.create(catalog, schema, tableBuilderMap);
        } catch (SQLException e) {
            throw JdbcExecutor.wrapException(e);
        }
    }


    @Override
    public final void executeDdl(final List<String> ddlList) throws DataAccessException {
        final int size = ddlList.size();
        if (size == 0) {
            return;
        }
        try (Statement stmt = this.conn.createStatement()) {

            // execute ddl
            String ddl;
            for (int i = 0; i < size; i++) {
                ddl = ddlList.get(i);
                stmt.addBatch(ddl);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw JdbcExecutor.wrapException(e);
        }
    }

    @Override
    public final void close() throws DataAccessException {
        Throwable error = null;
        try {
            this.conn.close();

        } catch (Throwable e) {
            error = e;
        }

        if (this instanceof XaConnMetaExecutor) {
            try {
                ((XaConnMetaExecutor) this).xaConn.close();
            } catch (Throwable e) {
                if (error == null) {
                    error = e;
                }
            }
        }

        if (error != null) {
            throw JdbcExecutor.wrapError(error);
        }

    }


    private Map<String, TableInfo.Builder> getTableBuilder(final String catalog, final @Nullable String schema,
                                                           final DatabaseMetaData metaData) throws SQLException {

        try (ResultSet resultSet = metaData.getTables(catalog, schema, "%", new String[]{"TABLE", "VIEW"})) {
            final Map<String, TableInfo.Builder> builderMap = _Collections.hashMap();
            for (String tableName, type; resultSet.next(); ) {
                tableName = resultSet.getString("TABLE_NAME");
                type = resultSet.getString("TABLE_TYPE");

                final TableInfo.Builder builder;
                builder = TableInfo.builder(false, tableName)
                        .comment(resultSet.getString("REMARKS"));
                switch (type) {
                    case "TABLE":
                        builder.type(TableType.TABLE);
                        break;
                    case "VIEW":
                        builder.type(TableType.VIEW);
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

    private void appendColumn(final @Nullable String catalog, final @Nullable String schema,
                              final DatabaseMetaData metaData, final Map<String, TableInfo.Builder> tableBuilderMap)
            throws SQLException {

        try (ResultSet resultSet = metaData.getColumns(catalog, schema, "%", "%")) {
            TableInfo.Builder tableBuilder = null;
            final ColumnInfo.Builder builder = ColumnInfo.builder();
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

                        .comment(resultSet.getString("REMARKS"))
                        .autoincrement("YES".equals(resultSet.getString("IS_AUTOINCREMENT")))
                        .precision(resultSet.getInt("COLUMN_SIZE"));
                nullable = resultSet.getString("IS_NULLABLE");
                switch (nullable) {
                    case "YES":
                        builder.notNull(Boolean.FALSE);
                        break;
                    case "NO":
                        builder.notNull(Boolean.TRUE);
                        break;
                    case "":
                        builder.notNull(null);
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
            , final TableInfo.Builder tableBuilder, IndexInfo.Builder builder) throws SQLException {
        final String tableName = tableBuilder.name();
        try (ResultSet resultSet = metaData.getIndexInfo(catalog, schema, tableName, unique, false)) {
            Boolean asc;
            String lastIndexName = null;
            for (String ascStr, indexName; resultSet.next(); ) {
                if (!tableName.equals(resultSet.getString("TABLE_NAME"))) {
                    throw new DataAccessException(String.format("Table[%s] not match.", tableName));
                }

                indexName = resultSet.getString("INDEX_NAME");
                if (lastIndexName == null) {
                    builder.name(indexName);
                } else if (!lastIndexName.equals(indexName)) {
                    tableBuilder.appendIndex(builder.buildAndClear());
                    builder.name(indexName);
                }
                lastIndexName = indexName;
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
                builder.unique(!resultSet.getBoolean("NON_UNIQUE"));
                builder.appendColumn(resultSet.getString("COLUMN_NAME"), asc);

            }//for

            if (lastIndexName != null && lastIndexName.equals(builder.name())) {
                tableBuilder.appendIndex(builder.buildAndClear());
            }

        }//try


    }


    private static final class XaConnMetaExecutor extends JdbcMetaExecutor {

        private final XAConnection xaConn;

        private XaConnMetaExecutor(JdbcExecutorFactory factory, XAConnection xaConn, Connection conn) {
            super(factory, conn);
            this.xaConn = xaConn;
        }

    } // JdbcXaConnMetaExecutor


}
