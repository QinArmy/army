package io.army.boot;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.SessionFactoryException;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.SQLDialect;
import io.army.meta.SchemaMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

final class SessionFactoryBuilderImpl extends AbstractSessionFactoryBuilder {

    SessionFactoryBuilderImpl() {
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {

        Assert.notNull(this.dataSource, "dataSource required");
        Assert.notNull(this.environment, "environment required");

        SchemaMeta schemaMeta = createSchema(catalog, schema);
        SQLDialect actualSQLDialect = this.sqlDialect;
        if (actualSQLDialect == null) {
            actualSQLDialect = SQLDialect.MySQL57;
        }
        CurrentSessionContext actualCurrentSessionContext = this.currentSessionContext;

        if (actualCurrentSessionContext == null) {
            actualCurrentSessionContext = new DefaultCurrentSessionContext();
        }
        try {
            SessionFactoryImpl sessionFactory = new SessionFactoryImpl(environment, dataSource, schemaMeta
                    , actualCurrentSessionContext, actualSQLDialect);

            if (!CollectionUtils.isEmpty(this.interceptorList)) {
                for (SessionFactoryInterceptor interceptor : interceptorList) {
                    interceptor.beforeInit(sessionFactory);
                }
            }
            // init session factory
            sessionFactory.initSessionFactory();

            if (!CollectionUtils.isEmpty(this.interceptorList)) {
                for (SessionFactoryInterceptor interceptor : interceptorList) {
                    interceptor.afterInit(sessionFactory);
                }
            }
            return sessionFactory;
        } catch (Throwable e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "create session factory error.");
        }
    }



    /*################################## blow private method ##################################*/


}
