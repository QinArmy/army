package io.army.boot.migratioin;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.dialect.SQLDialect;
import io.army.schema.util.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;

public class ExtractorTests {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractorTests.class);

    private static DruidDataSource createDataSource() {
        return DataSourceUtils.createDataSource("army", "army", "army1234");
    }

    @Test
    public void getCatalogs() throws Exception {
        DruidDataSource dataSource = createDataSource();
        SchemaInfo schemaInfo = SchemaExtractor.newInstance().extractor(dataSource.getConnection());
        LOG.info("schema:{}", schemaInfo);

        for (TableInfo table : schemaInfo.tableMap().values()) {
            LOG.info("table:{}", table);
            for (ColumnInfo column : table.columnMap().values()) {
                LOG.info("column:{}", column);
            }
            for (IndexInfo index : table.indexMap().values()) {
                LOG.info("indexMap:{}", index);
            }
        }

        LOG.info("closing datasource ");
        dataSource.close();
    }

    @Test
    public void dataTypeMeta() throws Exception {
        try (DruidDataSource ds = createDataSource(); Connection conn = ds.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getColumns("army", null, "data_type", "%")) {
                for (; resultSet.next(); ) {
                    LOG.info("name:{},JDBC:{},sqlType:{},columnSize:{},default:{}"
                            , resultSet.getString("COLUMN_NAME")
                            , JDBCType.valueOf(resultSet.getInt("DATA_TYPE"))
                            , resultSet.getString("TYPE_NAME")
                            , resultSet.getString("COLUMN_SIZE")
                            , resultSet.getString("COLUMN_DEF")
                    );

                }
            }
        }
    }

    @Test
    public void simple() {
        System.out.println((1 << 16) - 1);
    }
}
