package io.army.boot;

import io.army.*;
import io.army.codec.FieldCodec;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.interceptor.DomainInterceptor;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class SessionFactoryImpl extends AbstractGenericSessionFactory implements InnerSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryImpl.class);

    private final DataSource dataSource;

    private final SQLDialect actualSQLDialect;

    private final Dialect dialect;

    private final ProxySession proxySession;

    private final CurrentSessionContext currentSessionContext;

    private final Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap;

    private final InsertSQLExecutor insertSQLExecutor = InsertSQLExecutor.build(this);

    private boolean closed;


    SessionFactoryImpl(String name, Environment env, DataSource dataSource
            , Collection<DomainInterceptor> domainInterceptors, Collection<FieldCodec> fieldCodecs)
            throws SessionFactoryException {
        super(name, env, fieldCodecs);

        Assert.notNull(dataSource, "dataSource required");

        this.dataSource = dataSource;
        this.currentSessionContext = SyncSessionFactoryUtils.buildCurrentSessionContext(this, this.env);
        this.proxySession = new ProxySessionImpl(this, this.currentSessionContext);
        this.domainInterceptorMap = SyncSessionFactoryUtils.createDomainInterceptorMap(domainInterceptors);

        Pair<Dialect, SQLDialect> pair = SyncSessionFactoryUtils.createDialect(dataSource, this);
        this.dialect = pair.getFirst();
        this.actualSQLDialect = pair.getSecond();
    }


    @Override
    public void close() throws SessionFactoryException {
        this.closed = true;
    }

    @Override
    public Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap() {
        return this.domainInterceptorMap;
    }

    @Override
    public List<DomainInterceptor> domainInterceptorList(TableMeta<?> tableMeta) {
        return this.domainInterceptorMap.getOrDefault(tableMeta, Collections.emptyList());
    }

    @Override
    public ProxySession proxySession() {
        return this.proxySession;
    }

    @Override
    public SessionBuilder builder() {
        return new SessionBuilderImpl();
    }

    @Override
    public boolean hasCurrentSession() {
        return this.currentSessionContext.hasCurrentSession();
    }

    @Override
    public boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass) {
        return currentSessionContextClass.isInstance(this.currentSessionContext);
    }

    @Override
    public CurrentSessionContext currentSessionContext() {
        return this.currentSessionContext;
    }

    @Override
    public InsertSQLExecutor insertSQLExecutor() {
        return insertSQLExecutor;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public Dialect dialect() {
        return dialect;
    }

    @Override
    public boolean supportZoneId() {
        return this.dialect.supportZoneId();
    }

    @Override
    public SQLDialect actualSQLDialect() {
        return this.actualSQLDialect;
    }

    @Override
    public DataSource dataSource() {
        return this.dataSource;
    }


    @Override
    public String toString() {
        return "SessionFactory[" + this.name + "]";
    }

    void initSessionFactory() throws ArmyAccessException {
        // 1.  migration meta
        new DefaultSessionFactoryInitializer(this).onStartup();

    }

    /*################################## blow instance inner class  ##################################*/

    private final class SessionBuilderImpl implements SessionBuilder {

        private boolean currentSession;

        @Override
        public SessionBuilder currentSession() {
            this.currentSession = true;
            return this;
        }

        @Override
        public Session build() throws SessionException {
            final boolean current = this.currentSession;
            try {
                final Session session = new SessionImpl(SessionFactoryImpl.this
                        , SessionFactoryImpl.this.dataSource.getConnection()
                        , current);
                if (current) {
                    SessionFactoryImpl.this.currentSessionContext.currentSession(session);
                }
                return session;
            } catch (SQLException e) {
                throw new CreateSessionException(ErrorCode.CANNOT_GET_CONN, e
                        , "Could not create Army-managed session,because can't get connection.");
            } catch (IllegalStateException e) {
                if (current) {
                    throw new CreateSessionException(ErrorCode.DUPLICATION_CURRENT_SESSION, e
                            , "Could not create Army-managed session,because duplication current session.");
                } else {
                    throw new CreateSessionException(ErrorCode.ACCESS_ERROR, e
                            , "Could not create Army-managed session.");
                }

            }
        }
    }


}
