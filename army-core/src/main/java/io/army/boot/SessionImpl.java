package io.army.boot;

import io.army.Session;
import io.army.SessionFactory;
import io.army.SessionOptions;
import io.army.beans.BeanWrapper;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.generator.FieldValuesGenerator;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class SessionImpl implements Session {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);


    private static class ConnInitParam {

        private final int isolation;

        private final boolean autoCommit;

        private final int timeout;

        private final boolean readonly;

        ConnInitParam(int isolation, boolean autoCommit, int timeout, boolean readonly) {
            this.isolation = isolation;
            this.autoCommit = autoCommit;
            this.timeout = timeout;
            this.readonly = readonly;
        }
    }

    private final SessionFactory sessionFactory;

    private final Connection connection;

    private final ConnInitParam connInitParam;

    private final FieldValuesGenerator fieldValuesGenerator;

    private boolean readonly;


    SessionImpl(SessionFactory sessionFactory, Connection connection) throws SQLException {
        this.sessionFactory = sessionFactory;
        this.connection = connection;
        this.readonly = connection.isReadOnly();

        this.connInitParam = new ConnInitParam(
                connection.getTransactionIsolation()
                , connection.getAutoCommit()
                , connection.getNetworkTimeout()
                , this.readonly);
        this.fieldValuesGenerator = FieldValuesGenerator.build(this);
    }

    @Override
    public SessionOptions options() {
        return null;
    }

    @Override
    public boolean readonly() {
        return readonly;
    }


    @Override
    public void save(IDomain entity) {
        TableMeta<?> tableMeta = sessionFactory.tableMetaMap().get(entity.getClass());
        // 1.

       BeanWrapper beanWrapper =  fieldValuesGenerator.createValues(tableMeta,entity);
        // 2.
       List<SQLWrapper> sqlList =  sessionFactory.dialect().insert(tableMeta,beanWrapper.getReadonlyWrapper());
        for (SQLWrapper wrapper : sqlList) {
            LOG.info("sql wrapper:{}",wrapper);
        }
    }

    @Override
    public SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public boolean closed() {
       return false;
    }

    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
