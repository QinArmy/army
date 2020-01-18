package io.army.schema.extract;

import io.army.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

class SchemaExtractorImpl implements SchemaExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaExtractorImpl.class);

    SchemaExtractorImpl() {
    }

    @Override
    public SchemaInfo extractor(Connection connection) {
        try (Connection conn = connection) {
            SchemaInfoImpl schemaInfo = new SchemaInfoImpl(conn.getCatalog(), conn.getSchema());
            // extract schema's table info
            schemaInfo.setTables(extractTableInfo(schemaInfo,conn.getMetaData()));
            return schemaInfo;
        } catch (SQLException e) {
           throw new SchemaExtractException(ErrorCode.NONE,e,e.getMessage());
        }
    }

    /**
     * extract schema table info
     * @return a unmodifiable List
     */
    private List<TableInfo> extractTableInfo(SchemaInfoImpl schemaInfo, DatabaseMetaData metaData) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(schemaInfo.catalog(), schemaInfo.name(), "%", null)) {
            List<TableInfo> tableInfoList = new ArrayList<>();

            for (TableInfoImpl tableInfo; resultSet.next(); ) {
                tableInfo = new TableInfoImpl(schemaInfo, resultSet.getString("TABLE_NAME"));

                tableInfo.setPhysicalTable("TABLE".equalsIgnoreCase(resultSet.getString("TABLE_TYPE")));
                tableInfo.setComment(resultSet.getString("REMARKS"));
                // extract columns
                tableInfo.setColumns(extractColumnInfo(tableInfo, metaData));
                // extract unique indexes
                tableInfo.setIndexes(extractIndexInfo(tableInfo, metaData,true));
                // extract non unique indexes
                tableInfo.indexes().addAll(extractIndexInfo(tableInfo, metaData,false));
                // make indexes list unmodifiable
                tableInfo.setIndexes(Collections.unmodifiableList(tableInfo.indexes()));

                tableInfoList.add(tableInfo);
            }

            return Collections.unmodifiableList(tableInfoList);
        }

    }

    /**
     * extract table column info
     * @return a unmodifiable List
     */
    private List<ColumnInfo> extractColumnInfo(TableInfo tableInfo, DatabaseMetaData metaData) throws SQLException {
        try (ResultSet resultSet = metaData.getColumns(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), "%")) {
            List<ColumnInfo> columnInfoList = new ArrayList<>();

            for (ColumnInfoImpl columnInfo; resultSet.next(); ) {
                columnInfo = new ColumnInfoImpl(tableInfo, resultSet.getString("COLUMN_NAME"))
                        .setJdbcType(JDBCType.valueOf(resultSet.getInt("DATA_TYPE")))
                        .setSqlType(resultSet.getString("TYPE_NAME"))
                        .setComment(resultSet.getString("REMARKS"))

                        .setNotNull("NO".equalsIgnoreCase(resultSet.getString("IS_NULLABLE")))
                        .setDefaultValue(resultSet.getString("COLUMN_DEF"))
                        .setPrecision(resultSet.getInt("COLUMN_SIZE"))
                        .setScale(resultSet.getInt("DECIMAL_DIGITS"))

                ;
                columnInfoList.add(columnInfo);
            }
            return Collections.unmodifiableList(columnInfoList);

        }
    }

    /**
     * extract table indexes info
     * @return a modifiable List
     */
    private List<IndexInfo> extractIndexInfo(TableInfo tableInfo, DatabaseMetaData metaData, boolean unique) throws SQLException {
        try (ResultSet resultSet = metaData.getIndexInfo(tableInfo.schema().catalog(),
                tableInfo.schema().name(), tableInfo.name(), unique, false)) {

            Map<String, IndexInfoImpl> map = new HashMap<>();
            IndexInfoImpl indexInfo;
            String indexName;
            boolean asc;
            List<IndexColumnInfo> columnInfoList;
            for (IndexColumnInfo columnInfo; resultSet.next(); ) {

                indexName = resultSet.getString("INDEX_NAME");
                indexInfo = map.get(indexName);
                if (indexInfo == null) {
                    indexInfo = new IndexInfoImpl(tableInfo, indexName, !resultSet.getBoolean("NON_UNIQUE"));
                    map.put(indexName, indexInfo);
                }

                asc = "A".equalsIgnoreCase(resultSet.getString("ASC_OR_DESC"));
                columnInfo = new IndexColumnInfoImpl(tableInfo, indexInfo, resultSet.getString("COLUMN_NAME"), asc);

                columnInfoList = indexInfo.columns();
                if (columnInfoList == null) {
                    columnInfoList = new ArrayList<>(5);
                    indexInfo.columns(columnInfoList);
                }
                columnInfoList.add(columnInfo);
            }

            List<IndexInfo> indexInfoList = new ArrayList<>(map.size());
            for (IndexInfoImpl index: map.values()) {
                index.columns(Collections.unmodifiableList(index.columns()));
                indexInfoList.add(index);
            }
            return indexInfoList;

        }
    }

}
