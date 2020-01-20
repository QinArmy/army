package io.army.schema.migration;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.schema.util.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class ExtractorTests {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractorTests.class);

    @Test
    public void getCatalogs() throws Exception {
        DruidDataSource dataSource = DataSourceUtils.createDataSource("army_p2p", "net_loan", "net_loan123");
        SchemaInfo schemaInfo = SchemaExtractor.newInstance().extractor(dataSource.getConnection());
        LOG.info("schema:{}", schemaInfo);

        for (TableInfo table : schemaInfo.tableMap().values()) {
            LOG.info("table:{}",table);
            for (ColumnInfo column : table.columnMap().values()) {
                LOG.info("column:{}",column);
            }
            for (IndexInfo index : table.indexMap().values()) {
                LOG.info("indexMap:{}",index);
            }
        }

        LOG.info("closing datasource ");
        dataSource.close();
    }

    @Test
    public void simple(){
        System.out.println((1 << 16) - 1);
    }
}
