package io.army.boot;

import io.army.ShardingMode;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.meta.SchemaMeta;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractSessionFactoryBuilder implements SessionFactoryBuilder {

    DataSource dataSource;

    SQLDialect sqlDialect;

    String catalog;

    String schema;

    Environment environment;

    CurrentSessionContext currentSessionContext;

    List<SessionFactoryInterceptor> interceptorList;

    AbstractSessionFactoryBuilder() {

    }


    @Override
    public final SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public final SessionFactoryBuilder sqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
        return this;
    }

    @Override
    public final SessionFactoryBuilder catalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    @Override
    public final SessionFactoryBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public final SessionFactoryBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public SessionFactoryBuilder shardingMode(ShardingMode shardingMode) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public final SessionFactoryBuilder currentSessionContext(CurrentSessionContext currentSessionContext) {
        this.currentSessionContext = currentSessionContext;
        return this;
    }

    @Override
    public final SessionFactoryBuilder interceptor(SessionFactoryInterceptor interceptor) {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList<>(1);
        }
        this.interceptorList.add(interceptor);
        return this;
    }

    @Override
    public final SessionFactoryBuilder interceptorList(List<SessionFactoryInterceptor> interceptorList) {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList<>(interceptorList.size());
        }
        this.interceptorList.addAll(interceptorList);
        return this;
    }


    static SchemaMeta createSchema(String catalog, String schema) {
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
