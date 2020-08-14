package io.army.boot.migratioin;

import io.army.ErrorCode;
import io.army.lang.Nullable;
import io.army.util.StringUtils;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SchemaExtractorImpl implements SchemaExtractor {

    private final Connection connection;

    SchemaExtractorImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public final SchemaInfo extract(@Nullable String routeSuffix) throws SchemaExtractException {
        try {
            SchemaInfo schemaInfo = new SchemaInfo(connection.getCatalog(), connection.getSchema());
            // extract schema's tableMeta info
            schemaInfo.tableMap(extractTableInfo(schemaInfo, connection.getMetaData(), routeSuffix));
            return schemaInfo;
        } catch (SQLException e) {
            throw new SchemaExtractException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    /**
     * extract schema tableMeta info
     *
     * @return a unmodifiable map
     */
    private Map<String, TableInfo> extractTableInfo(SchemaInfo schemaInfo, DatabaseMetaData metaData
            , @Nullable String routeSuffix)
            throws SQLException {
        final String tableNamePattern = routeSuffix == null ? "%" : "%" + routeSuffix;

        try (ResultSet resultSet = metaData.getTables(schemaInfo.catalog(), schemaInfo.name()
                , tableNamePattern, new String[]{"TABLE"})) {

            Map<String, TableInfo> tableInfoMap = new HashMap<>();

            for (TableInfo tableInfo; resultSet.next(); ) {
                tableInfo = new TableInfo(schemaInfo, resultSet.getString("TABLE_NAME"));

                tableInfo.physicalTable(true);
                tableInfo.comment(resultSet.getString("REMARKS"));
                // extract columnMap
                tableInfo.columnMap(extractColumnInfo(tableInfo, metaData));
                // extract unique indexMap
                tableInfo.indexMap(extractIndexInfo(tableInfo, metaData, true));
                // extract non unique indexMap
                tableInfo.indexMap().putAll(extractIndexInfo(tableInfo, metaData, false));
                // make indexMap list unmodifiable
                tableInfo.indexMap(Collections.unmodifiableMap(tableInfo.indexMap()));
                // make key lower case
                tableInfoMap.put(StringUtils.toLowerCase(tableInfo.name()), tableInfo);
            }

            return Collections.unmodifiableMap(tableInfoMap);
        }

    }

    /**
     * extract tableMeta column info
     *
     * @return a unmodifiable map
     */
    private Map<String, ColumnInfo> extractColumnInfo(TableInfo tableInfo, DatabaseMetaData metaData) throws SQLException {
        try (ResultSet resultSet = metaData.getColumns(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), "%")) {
            Map<String, ColumnInfo> columnInfoMap = new HashMap<>();

            for (ColumnInfo columnInfo; resultSet.next(); ) {
                columnInfo = new ColumnInfo(tableInfo, resultSet.getString("COLUMN_NAME"))
                        .jdbcType(JDBCType.valueOf(resultSet.getInt("DATA_TYPE")))
                        .sqlType(resultSet.getString("TYPE_NAME"))
                        .comment(resultSet.getString("REMARKS"))

                        .nullable(!"NO".equalsIgnoreCase(resultSet.getString("IS_NULLABLE")))
                        .defaultValue(resultSet.getString("COLUMN_DEF"))
                        .columnSize(resultSet.getInt("COLUMN_SIZE"))
                        .scale(resultSet.getInt("DECIMAL_DIGITS"))

                ;
                // make key lower case
                columnInfoMap.put(StringUtils.toLowerCase(columnInfo.name()), columnInfo);
            }
            return Collections.unmodifiableMap(columnInfoMap);

        }
    }

    /**
     * extract tableMeta indexMap info
     *
     * @return a modifiable List
     */
    private Map<String, IndexInfo> extractIndexInfo(TableInfo tableInfo, DatabaseMetaData metaData, boolean unique)
            throws SQLException {
        try (ResultSet resultSet = metaData.getIndexInfo(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), unique, false)) {

            Map<String, IndexInfo> map = new HashMap<>();
            IndexInfo indexInfo;
            String indexName;
            boolean asc;
            for (IndexColumnInfo columnInfo; resultSet.next(); ) {

                indexName = resultSet.getString("INDEX_NAME");
                indexInfo = map.get(indexName);
                if (indexInfo == null) {
                    indexInfo = new IndexInfo(tableInfo, indexName, !resultSet.getBoolean("NON_UNIQUE"));
                    map.put(indexName, indexInfo);
                }

                asc = "A".equalsIgnoreCase(resultSet.getString("ASC_OR_DESC"));
                columnInfo = new IndexColumnInfo(tableInfo, indexInfo
                        , StringUtils.toLowerCase(resultSet.getString("COLUMN_NAME")), asc);

                Map<String, IndexColumnInfo> columnInfoMap = indexInfo.columnMap();
                // make key lower case
                columnInfoMap.put(StringUtils.toLowerCase(columnInfo.name()), columnInfo);
            }

            Map<String, IndexInfo> indexInfoMap = new HashMap<>((int) (map.size() / 0.75f));
            for (IndexInfo index : map.values()) {
                index.columnMap(Collections.unmodifiableMap(index.columnMap()));
                // make key lower case
                indexInfoMap.put(StringUtils.toLowerCase(index.name()), index);
            }
            return indexInfoMap;

        }
    }

}
