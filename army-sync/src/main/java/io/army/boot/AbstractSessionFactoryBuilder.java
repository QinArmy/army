package io.army.boot;

import io.army.codec.FieldCodec;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.env.Environment;
import io.army.interceptor.DomainInterceptor;
import io.army.meta.SchemaMeta;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractSessionFactoryBuilder implements SessionFactoryBuilder {

    DataSource dataSource;

    Environment environment;

    String name;

    List<SessionFactoryInterceptor> interceptorList;

    Collection<DomainInterceptor> domainInterceptors;

    Collection<FieldCodec> fieldCodecs;

    AbstractSessionFactoryBuilder() {

    }


    @Override
    public final SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public final SessionFactoryBuilder domainInterceptor(Collection<DomainInterceptor> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public final SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public final SessionFactoryBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public final SessionFactoryBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
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
