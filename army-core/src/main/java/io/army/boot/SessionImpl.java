package io.army.boot;

import io.army.SessionFactory;
import io.army.SessionOptions;
import io.army.beans.BeanWrapper;
import io.army.criteria.UpdateAble;
import io.army.criteria.Visible;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

class SessionImpl implements InnerSession {

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
        // 1. create necessary value for domain
        BeanWrapper beanWrapper = fieldValuesGenerator.createValues(tableMeta, entity);
        // 2. create insert dml
        List<SQLWrapper> sqlList = sessionFactory.dialect().insert(tableMeta, beanWrapper.getReadonlyWrapper());
        // 3. execute dml
        InsertSQLExecutor.build().executeInsert(this, sqlList, beanWrapper);
    }

    @Override
    public List<Integer> update(UpdateAble updateAble) {
        return update(updateAble, Visible.ONLY_VISIBLE);
    }

    @Override
    public List<Integer> update(UpdateAble updateAble, Visible visible) {
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(updateAble, visible);
        for (SQLWrapper wrapper : sqlWrapperList) {
            LOG.info("wrapper:{}", wrapper);
        }
        return Collections.emptyList();
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

    /*################################## blow InnerSession method ##################################*/

    @Override
    public PreparedStatement createStatement(String sql, boolean generatedKey)
            throws SQLException {
        int type;
        if (generatedKey) {
            type = Statement.RETURN_GENERATED_KEYS;
        } else {
            type = Statement.NO_GENERATED_KEYS;
        }
        return connection.prepareStatement(sql, type);
    }
}
