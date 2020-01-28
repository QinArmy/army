package io.army.boot;

import io.army.SessionFactory;
import io.army.SessionFactoryOptions;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.SchemaMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SessionFactoryBuilderImpl implements SessionFactoryBuilder {

    private ZoneId zoneId;

    private boolean showSql;

    private boolean formatSql;

    private boolean readonly;

    private List<String> packagesToScan;

    private DataSource dataSource;

    private SQLDialect sqlDialect;

    private String catalog;

    private String schema;


    @Override
    public SessionFactory build() {
        ZoneId factoryZoneId = zoneId;
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        SessionFactoryOptions options = new SessionFactoryOptionsImpl(readonly, factoryZoneId, packagesToScan)
                .showSql(showSql)
                .formatSql(formatSql);

        Assert.notNull(dataSource, "dataSource required");

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(options, dataSource, createSchema(), sqlDialect);
        // init session factory
        sessionFactory.initSessionFactory();
        return sessionFactory;
    }

    @Override
    public SessionFactoryBuilderImpl zoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }


    @Override
    public SessionFactoryBuilderImpl showSql(boolean showSql) {
        this.showSql = showSql;
        return this;
    }

    @Override
    public SessionFactoryBuilderImpl formatSql(boolean formatSql) {
        this.formatSql = formatSql;
        return this;
    }

    public SessionFactoryBuilderImpl readonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }


    @Override
    public SessionFactoryBuilderImpl packagesToScan(String... packagesToScan) {
        this.packagesToScan = Collections.unmodifiableList(Arrays.asList(packagesToScan));
        return this;
    }

    @Override
    public SessionFactoryBuilder catalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    @Override
    public SessionFactoryBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public SessionFactoryBuilder sqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
        return this;
    }

    /*################################## blow private method ##################################*/

    private SchemaMeta createSchema() {
        String actualCatalog = catalog, actualSchema = schema;
        if (!StringUtils.hasText(actualCatalog)) {
            actualCatalog = "";
        }
        if (!StringUtils.hasText(actualSchema)) {
            actualSchema = "";
        }
        return SchemaMetaFactory.getSchema(actualCatalog, actualSchema);
    }

}
