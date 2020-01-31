package io.army.boot;

import io.army.SessionFactory;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.env.StandardEnvironment;
import io.army.meta.SchemaMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SessionFactoryBuilderImpl implements SessionFactoryBuilder {

    private DataSource dataSource;

    private SQLDialect sqlDialect;

    private String catalog;

    private String schema;

    private Environment environment;


    @Override
    public SessionFactory build() {

        Assert.notNull(dataSource, "dataSource required");
        Assert.notNull(environment, "environment required");

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(environment, dataSource, createSchema(), sqlDialect);
        // init session factory
        sessionFactory.initSessionFactory();
        return sessionFactory;
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

    @Override
    public SessionFactoryBuilder environment(StandardEnvironment environment) {
        this.environment = environment;
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
