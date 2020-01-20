package io.army.schema.migration;

import io.army.ErrorCode;

import java.sql.*;
import java.util.*;

class SchemaExtractorImpl implements SchemaExtractor {



    SchemaExtractorImpl() {
    }

    @Override
    public SchemaInfo extractor(Connection connection) {
        try (Connection conn = connection) {
            SchemaInfo schemaInfo = new SchemaInfo(conn.getCatalog(), conn.getSchema());
            // extract schema's table info
            schemaInfo.tableMap(extractTableInfo(schemaInfo,conn.getMetaData()));
            return schemaInfo;
        } catch (SQLException e) {
           throw new SchemaExtractException(ErrorCode.NONE,e,e.getMessage());
        }
    }

    /**
     * extract schema table info
     * @return a unmodifiable map
     */
    private Map<String,TableInfo> extractTableInfo(SchemaInfo schemaInfo, DatabaseMetaData metaData) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(schemaInfo.catalog(), schemaInfo.name(), "%", null)) {
            Map<String,TableInfo> tableInfoMap = new HashMap<>();

            for (TableInfo tableInfo; resultSet.next(); ) {
                tableInfo = new TableInfo(schemaInfo, resultSet.getString("TABLE_NAME"));

                tableInfo.physicalTable("TABLE".equalsIgnoreCase(resultSet.getString("TABLE_TYPE")));
                tableInfo.comment(resultSet.getString("REMARKS"));
                // extract columnMap
                tableInfo.columnMap(extractColumnInfo(tableInfo, metaData));
                // extract unique indexMap
                tableInfo.indexMap(extractIndexInfo(tableInfo, metaData,true));
                // extract non unique indexMap
                tableInfo.indexMap().putAll(extractIndexInfo(tableInfo, metaData,false));
                // make indexMap list unmodifiable
                tableInfo.indexMap(Collections.unmodifiableMap(tableInfo.indexMap()));

                tableInfoMap.put(tableInfo.name(),tableInfo);
            }

            return Collections.unmodifiableMap(tableInfoMap);
        }

    }

    /**
     * extract table column info
     * @return a unmodifiable map
     */
    private Map<String,ColumnInfo> extractColumnInfo(TableInfo tableInfo, DatabaseMetaData metaData) throws SQLException {
        try (ResultSet resultSet = metaData.getColumns(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), "%")) {
            Map<String,ColumnInfo>  columnInfoMap = new HashMap<>();

            for (ColumnInfo columnInfo; resultSet.next(); ) {
                columnInfo = new ColumnInfo(tableInfo, resultSet.getString("COLUMN_NAME"))
                        .jdbcType(JDBCType.valueOf(resultSet.getInt("DATA_TYPE")))
                        .sqlType(resultSet.getString("TYPE_NAME"))
                        .comment(resultSet.getString("REMARKS"))

                        .nonNull("NO".equalsIgnoreCase(resultSet.getString("IS_NULLABLE")))
                        .defaultValue(resultSet.getString("COLUMN_DEF"))
                        .precision(resultSet.getInt("COLUMN_SIZE"))
                        .scale(resultSet.getInt("DECIMAL_DIGITS"))

                ;
                columnInfoMap.put(columnInfo.name(),columnInfo);
            }
            return Collections.unmodifiableMap(columnInfoMap);

        }
    }

    /**
     * extract table indexMap info
     * @return a modifiable List
     */
    private Map<String,IndexInfo> extractIndexInfo(TableInfo tableInfo, DatabaseMetaData metaData, boolean unique) throws SQLException {
        try (ResultSet resultSet = metaData.getIndexInfo(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), unique, false)) {

            Map<String, IndexInfo> map = new HashMap<>();
            IndexInfo indexInfo;
            String indexName;
            boolean asc;
            Map<String,IndexColumnInfo> columnInfoMap;
            for (IndexColumnInfo columnInfo; resultSet.next(); ) {

                indexName = resultSet.getString("INDEX_NAME");
                indexInfo = map.get(indexName);
                if (indexInfo == null) {
                    indexInfo = new IndexInfo(tableInfo, indexName, !resultSet.getBoolean("NON_UNIQUE"));
                    map.put(indexName, indexInfo);
                }

                asc = "A".equalsIgnoreCase(resultSet.getString("ASC_OR_DESC"));
                columnInfo = new IndexColumnInfo(tableInfo, indexInfo, resultSet.getString("COLUMN_NAME"), asc);

                columnInfoMap = indexInfo.columnMap();
                if (columnInfoMap == null) {
                    columnInfoMap = new HashMap<>();
                    indexInfo.columnMap(columnInfoMap);
                }
                columnInfoMap.put(columnInfo.name(),columnInfo);
            }

            Map<String,IndexInfo> indexInfoMap = new HashMap<>((int)(map.size() / 0.75f));
            for (IndexInfo index: map.values()) {
                index.columnMap(Collections.unmodifiableMap(index.columnMap()));
                  indexInfoMap.put(index.name(),index);
            }
            return indexInfoMap;

        }
    }

}