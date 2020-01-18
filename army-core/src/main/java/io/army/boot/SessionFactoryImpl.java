package io.army.boot;

import io.army.ArmyRuntimeException;
import io.army.Session;
import io.army.SessionBuilder;
import io.army.SessionFactoryOptions;
import io.army.asm.TableMetaLoader;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

class SessionFactoryImpl implements InnerSessionFactory {

    private final SessionFactoryOptions options;

    private final DataSource dataSource;

    private final Map<Class<?>, TableMeta<?>> classTableMetaMap;

    private boolean closed;

    SessionFactoryImpl(SessionFactoryOptions options, DataSource dataSource) {
        Assert.notNull(options, "options required");
        Assert.notNull(dataSource, "dataSource required");

        this.options = options;
        this.dataSource = dataSource;
        this.classTableMetaMap = scanPackagesForMeta();
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

    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }


    @Override
    public Map<Class<?>, TableMeta<?>> tableMetaMap() {
        return this.classTableMetaMap;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    void initSessionFactory() {
        // 初始化 schema
    }

    private Map<Class<?>, TableMeta<?>> scanPackagesForMeta() {
        return TableMetaLoader.create()
                .scanTableMeta(options.getPackagesToScan());
    }
}
