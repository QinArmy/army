package io.army.boot;

import io.army.TmSession;
import io.army.TmSessionFactory;
import io.army.criteria.*;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.TmTransaction;

import java.util.List;

final class TmSessionImpl implements TmSession {


    private final InnerTmSessionFactory sessionFactory;

    private final TransactionOption option;

    private final boolean currentSession;

    private final TmTransaction tmTransaction;

    TmSessionImpl(InnerTmSessionFactory sessionFactory, TransactionOption option, boolean currentSession) {
        this.sessionFactory = sessionFactory;
        this.currentSession = currentSession;
        this.option = option;
    }

    @Override
    public TmSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public TmTransaction sessionTransaction() throws NoSessionTransactionException {
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
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return null;
    }

    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int update(Update update) {
        return 0;
    }

    @Override
    public int update(Update update, Visible visible) {
        return 0;
    }

    @Override
    public void updateOne(Update update) {

    }

    @Override
    public void updateOne(Update update, Visible visible) {

    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int[] batchUpdate(Update update) {
        return new int[0];
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        return new int[0];
    }

    @Override
    public long largeUpdate(Update update) {
        return 0;
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return 0;
    }

    @Override
    public long[] batchLargeUpdate(Update update) {
        return new long[0];
    }

    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        return new long[0];
    }

    @Override
    public void insert(Insert insert) {

    }

    @Override
    public void insert(Insert insert, Visible visible) {

    }

    @Override
    public int subQueryInsert(Insert insert) {
        return 0;
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public long subQueryLargeInsert(Insert insert) {
        return 0;
    }

    @Override
    public long subQueryLargeInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int delete(Delete delete) {
        return 0;
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int[] batchDelete(Delete delete) {
        return new int[0];
    }

    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        return new int[0];
    }

    @Override
    public long largeDelete(Delete delete) {
        return 0;
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public long[] batchLargeDelete(Delete delete) {
        return new long[0];
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        return new long[0];
    }

}
