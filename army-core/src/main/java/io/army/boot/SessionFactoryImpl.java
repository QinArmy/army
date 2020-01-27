package io.army.boot;

import io.army.*;
import io.army.asm.TableMetaLoader;
import io.army.boot.migratioin.Meta2Schema;
import io.army.dialect.Dialect;
import io.army.dialect.DialectNotMatchException;
import io.army.dialect.SQLDialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class SessionFactoryImpl implements InnerSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryImpl.class);

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

    @Override
    public String toString() {
        return new StringJoiner(", ", SessionFactoryImpl.class.getSimpleName() + "[", "]")
                .add("options=" + options)
                .add("classTableMetaMap=" + classTableMetaMap.entrySet())
                .add("dialect=" + dialect)
                .add("closed=" + closed)
                .toString();
    }

    void initSessionFactory()throws ArmyAccessException {
        // migration meta
        try {
            // 1. generate sql
            Map<String, List<String>> tableSqlMap;
           tableSqlMap =  Meta2Schema.build().migrate(classTableMetaMap.values(),dataSource.getConnection(),dialect);
            // 2. execute sql
            if(LOG.isDebugEnabled()){
                printMigrationSql(tableSqlMap);
            }

        } catch (SQLException e) {
           throw new ArmyAccessException(ErrorCode.ACCESS_ERROR,e,e.getMessage());
        }

    }

    /*################################## blow private method ##################################*/

   private void printMigrationSql( Map<String, List<String>> tableSqlMap){
       for (Map.Entry<String, List<String>> e : tableSqlMap.entrySet()) {
           LOG.debug("\ntable:{}\n",e.getKey());
           for (String sql : e.getValue()) {
               LOG.debug("{}",sql);
           }
           LOG.debug("\n");
       }
   }
}
