package io.army.boot;

import io.army.*;
import io.army.criteria.*;
import io.army.dialect.SQLWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;
import io.army.util.Pair;
import io.army.util.Triple;
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


    private final InnerSessionFactory sessionFactory;

    private final Connection connection;

    private final ConnInitParam connInitParam;

    private final FieldValuesGenerator fieldValuesGenerator;

    private final boolean readonly;

    private final boolean currentSession;

    private Transaction transaction;


    SessionImpl(InnerSessionFactory sessionFactory, Connection connection,
                boolean currentSession) throws SessionException {
        this.sessionFactory = sessionFactory;
        this.connection = connection;
        this.currentSession = currentSession;
        this.readonly = sessionFactory.readonly();

        try {
            this.connInitParam = new ConnInitParam(
                    connection.getTransactionIsolation()
                    , connection.getAutoCommit()
                    , connection.getNetworkTimeout()
                    , this.connection.isReadOnly());
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "connection query occur error.");
        }

        this.fieldValuesGenerator = FieldValuesGenerator.build(this.sessionFactory);
    }

    @Override
    public SessionOptions options() {
        return null;
    }

    @Override
    public final boolean readonly() {
        return this.readonly
                || (this.transaction != null && this.transaction.readOnly());
    }


    @Override
    public void save(IDomain entity) {
        //1. create necessary value for domain ; 2. create batchInsert dml
        List<SQLWrapper> sqlList = sessionFactory.dialect().insert(entity);
        // 3. execute dml
        InsertSQLExecutor.build().insert(this, sqlList);
    }

    @Override
    public final <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return get(tableMeta, id, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible) {
        return null;
    }

    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return getByUnique(tableMeta, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
    }

    @Override
    public <T> T selectOne(Select select, Class<T> resultClass) {
        return null;
    }

    @Override
    public <T> T selectOne(Select select, Class<T> resultClass, Visible visible) {
        return null;
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass) {
        return null;
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass, Visible visible) {
        return null;
    }

    @Override
    public <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass) {
        return null;
    }

    @Override
    public <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass
            , Visible visible) {
        return null;
    }

    @Override
    public <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass, Visible visible) {
        return null;
    }

    @Override
    public <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass) {
        return null;
    }

    @Override
    public List<Integer> update(Update update) {
        return update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public List<Integer> update(Update update, Visible visible) {
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(update, visible);
        for (SQLWrapper wrapper : sqlWrapperList) {
            LOG.info("wrapper:{}", wrapper);
        }
        return Collections.emptyList();
    }

    @Override
    public void insert(Insert insert) {
    }

    @Override
    public void insert(Insert insert, Visible visible) {

    }

    @Override
    public void delete(Delete delete) {

    }

    @Override
    public void delete(Delete delete, Visible visible) {

    }


    @Override
    public final SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public void close() throws SessionException {
        try {
            if (this.currentSession) {
                sessionFactory.currentSessionContext().removeCurrentSession(this);
            }
            connection.close();
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.CLOSE_CONN_ERROR, e, "session close connection error.");
        }
    }

    @Override
    public final Transaction sessionTransaction() throws NoSessionTransactionException {
        if (this.transaction == null) {
            throw new NoSessionTransactionException("no session transaction.");
        }
        return this.transaction;
    }

    @Override
    public TransactionBuilder builder() throws SessionException {
        if (this.readonly) {
            throw new ReadOnlySessionException(ErrorCode.READ_ONLY_SESSION
                    , "read only session,can't create transaction.");
        }
        if (this.transaction != null) {
            throw new DuplicationSessionTransaction("duplication session transaction.");
        }
        return null;
    }


    @Override
    public final boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public void flush() throws SessionException {

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

    /*################################## blow static inner class ##################################*/


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
}
