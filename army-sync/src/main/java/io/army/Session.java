package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;

import java.sql.Connection;
import java.util.List;

public interface Session extends GenericSession {

    void save(IDomain entity);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    <T extends IDomain> List<T> select(Select select);

    <T extends IDomain> List<T> select(Select select, Visible visible);

    /**
     * @param update will execute singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(Update update);

    List<Integer> update(Update update, Visible visible);

    void insert(Insert insert);

    int delete(Delete delete);

    Transaction sessionTransaction() throws NoSessionTransactionException;

    /**
     * <o>
     * <li>invoke {@link io.army.context.spi.CurrentSessionContext#removeCurrentSession(Session)},if need</li>
     * <li>invoke {@link Connection#close()}</li>
     * </o>
     *
     * @throws SessionException close session occur error.
     */
    @Override
    void close() throws SessionException;

    TransactionBuilder builder() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder readOnly(boolean readOnly);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder timeout(int seconds);

        TransactionBuilder name(@Nullable String txName);

        Transaction build();

    }
}
