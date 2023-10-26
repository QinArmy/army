package io.army.sync;

import io.army.session.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking RM(Resource Manager) session in XA transaction.
 *
 * <p>This interface extends {@link RmSession} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 *
 * @see SyncRmSessionFactory
 * @since 1.0
 */
public interface SyncRmSession extends SyncSession, RmSession {

    @Override
    SyncRmSessionFactory sessionFactory();

    TransactionInfo start(Xid xid, int flags, TransactionOption option);

    SyncRmSession end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    int prepare(Xid xid, Function<Option<?>, ?> optionFunc);

    SyncRmSession commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    SyncRmSession rollback(Xid xid, Function<Option<?>, ?> optionFunc);

    SyncRmSession forget(Xid xid, Function<Option<?>, ?> optionFunc);

    List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc);

    Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc);


}
