package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.sync.SessionFactoryAdvice;
import io.army.sync.TmSessionFactory;
import io.army.util.Assert;

import javax.sql.XADataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class TmSessionFactionBuilderImpl extends AbstractSyncSessionFactoryBuilder implements TmSessionFactionBuilder {

    private List<XADataSource> dataSourceList;

    private Map<Integer, Database> sqlDialectMap;

    private int tableCountPerDatabase = -1;

    TmSessionFactionBuilderImpl() {
    }


    @Override
    public final TmSessionFactionBuilder dataSource(List<XADataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
        return this;
    }

    @Override
    public final TmSessionFactionBuilder sqlDialectMap(Map<Integer, Database> sqlDialectMap) {
        this.sqlDialectMap = sqlDialectMap;
        return this;
    }

    @Override
    public TmSessionFactionBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public TmSessionFactionBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public TmSessionFactionBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public TmSessionFactionBuilder factoryAdvice(List<SessionFactoryAdvice> factoryAdviceList) {
        this.factoryAdviceList = factoryAdviceList;
        return this;
    }

    @Override
    public TmSessionFactionBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public TmSessionFactionBuilder tableCountPerDatabase(int tableCountPerDatabase) {
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    @Nullable
    public final List<XADataSource> dataSourceList() {
        return dataSourceList;
    }

    @Nullable
    public final Map<Integer, Database> sqlDialectMap() {
        return sqlDialectMap;
    }

    public final int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Override
    public TmSessionFactory build() throws SessionFactoryException {
        Assert.hasText(this.name, "name required");
        Assert.notEmpty(this.dataSourceList, "dataSource list required");
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
            final TmSessionFactoryImpl sessionFactory = new TmSessionFactoryImpl(this);

            if (!factoryAdviceList.isEmpty()) {
                // invoke beforeInit
                for (SessionFactoryAdvice interceptor : factoryAdviceList) {
                    interceptor.beforeInit(sessionFactory);
                }
            }
            // init session factory
            sessionFactory.initTmSessionFactory();

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
                    , e, "create TmSessionFactory[%s] error.", this.name);
        }
    }
}
