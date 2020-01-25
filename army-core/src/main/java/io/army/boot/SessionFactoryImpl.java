package io.army.boot;

import io.army.*;
import io.army.asm.TableMetaLoader;
import io.army.dialect.Dialect;
import io.army.dialect.DialectNotMatchException;
import io.army.dialect.SQLDialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.Map;

class SessionFactoryImpl implements InnerSessionFactory {

    private final SessionFactoryOptions options;

    private final DataSource dataSource;

    private final Map<Class<?>, TableMeta<?>> classTableMetaMap;

    private final Dialect dialect;

    private boolean closed;


    SessionFactoryImpl(SessionFactoryOptions options, DataSource dataSource, @Nullable SQLDialect sqlDialect)
            throws ArmyRuntimeException {
        Assert.notNull(options, "options required");
        Assert.notNull(dataSource, "dataSource required");

        this.options = options;
        this.dataSource = dataSource;
        this.classTableMetaMap = SessionFactoryUtils.scanPackagesForMeta(this.options.packagesToScan());
        this.dialect = SessionFactoryUtils.createDialect(sqlDialect,dataSource,this);
    }


    @Override
    public SessionFactoryOptions options() {
        return options;
    }

    @Override
    public SessionBuilder sessionBuilder() {
        return new SessionBuilderImpl(this);
    }

    @Override
    public Session currentSession() {
        return null;
    }

    @Override
    public void close() throws ArmyRuntimeException {
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public Dialect dialect() {
        return dialect;
    }

    @Override
    public Map<Class<?>, TableMeta<?>> tableMetaMap() {
        return this.classTableMetaMap;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    void initSessionFactory()throws ArmyAccessException {
        // 初始化 schema

    }

    /*################################## blow private method ##################################*/

}
