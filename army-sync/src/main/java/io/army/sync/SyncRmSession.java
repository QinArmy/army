package io.army.sync;

import io.army.session.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking RM(Resource Manager) {@link SyncSession} in XA transaction.
 *
 * <p>This interface extends {@link RmSession} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 *
 * <p>The instance of this interface is created by {@link SyncRmSessionFactory.SessionBuilder#build()}.
 *
 * @see SyncRmSessionFactory
 * @since 1.0
 */
public interface SyncRmSession extends SyncSession, RmSession {

    @Override
    SyncRmSessionFactory sessionFactory();

    TransactionInfo start(Xid xid);

    TransactionInfo start(Xid xid, int flags);

    TransactionInfo start(Xid xid, int flags, TransactionOption option);

    TransactionInfo end(Xid xid);

    TransactionInfo end(Xid xid, int flags);

    TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    int prepare(Xid xid);

    int prepare(Xid xid, Function<Option<?>, ?> optionFunc);

    void commit(Xid xid);

    void commit(Xid xid, int flags);

    void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    void rollback(Xid xid);

    void rollback(Xid xid, Function<Option<?>, ?> optionFunc);

    void forget(Xid xid);

    /**
     * @throws SessionException throw when
     *                          <ul>
     *                              <li>{@link #isSupportForget()} return false</li>
     *                          </ul>
     * @see #isSupportForget()
     */
    void forget(Xid xid, Function<Option<?>, ?> optionFunc);

    List<Xid> recover(int flags);

    List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc);

    Stream<Xid> recoverStream(int flags);

    Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc);


}
