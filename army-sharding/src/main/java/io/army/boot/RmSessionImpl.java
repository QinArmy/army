package io.army.boot;

import io.army.SessionException;
import io.army.codec.StatementType;
import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.XATransaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a implementation of {@linkplain RmSession}.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class RmSessionImpl extends AbstractGenericSyncSession implements RmSession, InnerSession {

    private final InnerRmSessionFactory sessionFactory;

    RmSessionImpl(InnerRmSessionFactory sessionFactory, Connection connection)
            throws SessionException {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final RmSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public XATransaction sessionTransaction() throws NoSessionTransactionException {
        return null;
    }


    @Override
    public boolean readonly() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public boolean hasTransaction() {
        return false;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public long largeSubQueryInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public int update(Update update, Visible visible) {
        return 0;
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return null;
    }


    @Override
    public <V extends Number> Map<Integer, V> batchUpdate(Update update, @Nullable Set<Integer> domainIndexSet
            , Class<V> valueType, Visible visible) {
        return null;
    }

    @Override
    public void valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible) {

    }

    @Override
    public <V extends Number> Map<Integer, V> batchDelete(Delete delete, @Nullable Set<Integer> domainIndexSet
            , Class<V> valueType, Visible visible) {
        return null;
    }


    /*################################## blow InnerSession method ##################################*/

    @Override
    public PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement createStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public InnerCodecContext codecContext() {
        return null;
    }

    @Override
    public void codecContextStatementType(@Nullable StatementType statementType) {

    }

    @Override
    public Dialect dialect() {
        return null;
    }
}
