/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.session;

import io.army.option.Option;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import io.army.transaction.Xid;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking RM(Resource Manager) {@link SyncSession} in XA transaction.
 * <p>This interface extends {@link RmSession} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 * <p>The instance of this interface is created by {@link SyncSessionFactory.RmSessionBuilder#build()}.
 * <p>Application developer can control XA transaction by following methods :
 * <ol>
 *     <li>{@link #start(Xid, int, TransactionOption)}</li>
 *     <li>{@link #end(Xid, int, Function)}</li>
 *     <li>{@link #prepare(Xid, Function)}</li>
 *     <li>{@link #commit(Xid, int, Function)}</li>
 *     <li>{@link #rollback(Xid, Function)}</li>
 *     <li>{@link #forget(Xid, Function)}</li>
 *     <li>{@link #recoverList(int, Function)}</li>
 *     <li>{@link #recover(int, Function, StreamOption)}</li>
 *     <li>{@link #isSupportForget()}</li>
 * </ol>
 * and following methods :
 * <ul>
 *     <li>{@link #inTransaction()}</li>
 *     <li>{@link #hasTransactionInfo()}</li>
 *     <li>{@link #isRollbackOnly()}</li>
 *     <li>{@link #transactionInfo()}</li>
 *     <li>{@link #setTransactionCharacteristics(TransactionOption)}</li>
 *     <li>{@link #startSupportFlags()}</li>
 *     <li>{@link #endSupportFlags()}</li>
 *     <li>{@link #recoverSupportFlags()}</li>
 *     <li>{@link #isSameRm(XaTransactionSupportSpec)}</li>
 * </ul>
 * @see SyncSessionFactory
 * @since 0.6.0
 */
public interface SyncRmSession extends SyncSession, RmSession {

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

    List<Xid> recoverList(int flags);

    List<Xid> recoverList(int flags, Function<Option<?>, ?> optionFunc);

    Stream<Xid> recover(int flags);

    Stream<Xid> recover(int flags, Function<Option<?>, ?> optionFunc, StreamOption option);


}
