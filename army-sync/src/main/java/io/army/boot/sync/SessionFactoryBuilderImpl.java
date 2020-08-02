package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.boot.SessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactory;
import io.army.util.Assert;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

final class SessionFactoryBuilderImpl extends AbstractSyncSessionFactoryBuilder implements SessionFactoryBuilder {

    private DataSource dataSource;

    private int tableCount = -1;

    SessionFactoryBuilderImpl() {
    }


    @Override
    public SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public SessionFactoryBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public SessionFactoryBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public SessionFactoryBuilder factoryAdvice(List<SessionFactoryAdvice> factoryAdviceList) {
        this.factoryAdviceList = factoryAdviceList;
        return this;
    }

    @Override
    public SessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public SessionFactoryBuilder tableCount(int tableCount) {
        this.tableCount = tableCount;
        return this;
    }

    @Override
    public SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public final DataSource dataSource() {
        return this.dataSource;
    }

    public int tableCount() {
        return this.tableCount;
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {
        Assert.notNull(this.name, "name required");
        Assert.notNull(this.dataSource, "dataSource required");
        Assert.notNull(this.environment, "environment required");

        final List<SessionFactoryAdvice> factoryAdviceList = createFactoryInterceptorList();
        try {

            if (!factoryAdviceList.isEmpty()) {
                // invoke beforeInstance
                for (SessionFactoryAdvice sessionFactoryAdvice : factoryAdviceList) {
                    sessionFactoryAdvice.beforeInstance(this.environment);
                }
            }
            // instance
            SingleDatabaseSessionFactory sessionFactory = new SingleDatabaseSessionFactory(this);

            if (!factoryAdviceList.isEmpty()) {
                // invoke beforeInit
                for (SessionFactoryAdvice interceptor : factoryAdviceList) {
                    interceptor.beforeInit(sessionFactory);
                }
            }
            // init session factory
            sessionFactory.initSessionFactory();

            if (!factoryAdviceList.isEmpty()) {
                // invoke afterInit
                for (SessionFactoryAdvice interceptor : factoryAdviceList) {
                    interceptor.afterInit(sessionFactory);
                }
            }
            return sessionFactory;
        } catch (SessionFactoryException e) {
            throw e;
        } catch (Throwable e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "create session factory error.");
        }
    }



    /*################################## blow private method ##################################*/


}
