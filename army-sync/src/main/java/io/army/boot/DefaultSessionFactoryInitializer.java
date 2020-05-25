package io.army.boot;

import io.army.DataAccessException;
import io.army.ErrorCode;
import io.army.boot.migratioin.Meta2Schema;
import io.army.boot.migratioin.SchemaExtractor;
import io.army.boot.migratioin.SyncSchemaExtractorFactory;
import io.army.util.ClassUtils;
import io.army.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

final class DefaultSessionFactoryInitializer implements SessionFactoryInitializer {

    private final InnerSessionFactory sessionFactory;

    DefaultSessionFactoryInitializer(InnerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void onStartup() {
        try {
            Map<String, List<String>> tableSqlMap;
            // 1. create schemaExtractor instance
            SchemaExtractor schemaExtractor = SyncSchemaExtractorFactory.build(
                    sessionFactory.dataSource().getConnection());
            // 2. create DDL sql
            tableSqlMap = Meta2Schema.build().migrate(sessionFactory.tableMetaMap().values(), schemaExtractor
                    , sessionFactory.dialect());

            // 3. execute dml
            executeDDL(tableSqlMap);
        } catch (SQLException e) {
            throw new DataAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    private void executeDDL(Map<String, List<String>> tableSqlMap) {
        if (tableSqlMap.isEmpty()) {
            return;
        }
        // try to obtain primary datasource
        DataSource dataSource = obtainDataSource(sessionFactory.dataSource());
        try (Connection connection = dataSource.getConnection()) {
            DDLSQLExecutor ddlsqlExecutor = new BatchDDLSQLExecutor(connection);
            ddlsqlExecutor.executeDDL(tableSqlMap);
        } catch (SQLException e) {
            throw new DataAccessException(ErrorCode.ACCESS_ERROR, e, "SessionFactoryInitializer failure.");
        }
    }

    private DataSource obtainDataSource(DataSource dataSource) {
        String className = "org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource";

        DataSource primary = dataSource;
        try {
            if (ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader())) {
                Class<?> routingDataSourceClass = Class.forName(className);
                if (routingDataSourceClass.isInstance(dataSource)) {
                    Method method = ReflectionUtils.findMethod(dataSource.getClass(), "getPrimaryDataSource");
                    if (method != null) {
                        primary = (DataSource) method.invoke(dataSource);
                    }
                }
                if (primary == null) {
                    primary = dataSource;
                }
            }
        } catch (Exception e) {
            // no -op,primary = dataSource
        }
        return primary;

    }
}
