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


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>This interface representing the transaction info of session.
 * <p>The developer of {@link io.army.session.executor.StmtExecutor} can create the instance of this interface by following :
 * <ul>
 *     <li>{@link #notInTransaction(Isolation, boolean)}</li>
 *     <li>{@link #builder(boolean, Isolation, boolean)}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface TransactionInfo extends TransactionSpec {


    /**
     * <p>{@link io.army.session.Session}'s transaction isolation level.
     * <ul>
     *     <li>{@link #inTransaction()} is true : the isolation representing current transaction isolation</li>
     *     <li>Session level transaction isolation</li>
     * </ul>
     *
     * @return non-null
     */
    @Nonnull
    Isolation isolation();

    /**
     * <p>Session whether in transaction block or not.
     * <p><strong>NOTE</strong> : for XA transaction {@link XaStates#PREPARED} always return false.
     *
     * @return true : {@link Session} in transaction block.
     */
    boolean inTransaction();

    /**
     * @return true when
     * <ol>
     *     <li>{@link #inTransaction()} is true</li>
     *     <li>database server demand client rollback(eg: PostgreSQL) or {@link Session#isRollbackOnly()}</li>
     * </ol>
     */
    boolean isRollbackOnly();

    /**
     * <p>
     * Application developer can get
     *     <ul>
     *         <li>{@link XaStates} with {@link Option#XA_STATES}</li>
     *         <li>{@link Xid} with {@link Option#XID}</li>
     *         <li>{@code flag} of last phase with {@link Option#XA_FLAGS}</li>
     *     </ul>
     *     when this instance is returned by {@link RmSession}.
     * <br/>
     */
    @Override
    <T> T valueOf(Option<T> option);


    static InfoBuilder builder(boolean inTransaction, Isolation isolation, boolean readOnly) {
        return ArmyTransactionInfo.infoBuilder(inTransaction, isolation, readOnly);
    }


    /**
     * <p>Get a {@link TransactionInfo} instance that {@link TransactionInfo#inTransaction()} is false
     * and option is empty.
     */
    static TransactionInfo notInTransaction(Isolation isolation, boolean readOnly) {
        return ArmyTransactionInfo.noInTransaction(isolation, readOnly);
    }

    static TransactionInfo pseudoLocal(final TransactionOption option) {
        return ArmyTransactionInfo.pseudoLocal(option);
    }

    /**
     * <p>Create pseudo transaction info for XA transaction start method.
     */
    static TransactionInfo pseudoStart(final Xid xid, final int flags, final TransactionOption option) {
        return ArmyTransactionInfo.pseudoStart(xid, flags, option);
    }


    /**
     * <p>Create pseudo transaction info for XA transaction end method.
     */
    static TransactionInfo pseudoEnd(final TransactionInfo info, final int flags) {
        return ArmyTransactionInfo.pseudoEnd(info, flags);
    }

    /**
     * @throws IllegalArgumentException throw when
     *                                  <ul>
     *                                      <li>info is unknown implementation</li>
     *                                      <li>info's {@link TransactionInfo#inTransaction()} is false and info's {@link TransactionInfo#isolation()} isn't {@link Isolation#PSEUDO}</li>
     *                                  </ul>
     */
    static TransactionInfo forRollbackOnly(TransactionInfo info) {
        return ArmyTransactionInfo.forRollbackOnly(info);
    }

    /**
     * @throws IllegalArgumentException throw when
     *                                  <ul>
     *                                      <li>info is unknown implementation</li>
     *                                      <li>info's {@link TransactionInfo#inTransaction()} is false </li>
     *                                  </ul>
     */
    static TransactionInfo forChain(TransactionInfo info) {
        return ArmyTransactionInfo.forChain(info);
    }

    static TransactionInfo forXaEnd(int flags, TransactionInfo info) {
        return ArmyTransactionInfo.forXaEnd(flags, info);
    }

    static TransactionInfo forXaJoinEnded(int flags, TransactionInfo info) {
        return ArmyTransactionInfo.forXaJoinEnded(flags, info);
    }

    interface InfoBuilder {

        <T> InfoBuilder option(Option<T> option, @Nullable T value);

        /**
         * @throws IllegalArgumentException throw when not in transaction.
         */
        InfoBuilder option(TransactionOption option);

        /**
         * @throws IllegalArgumentException throw when not in transaction.
         */
        InfoBuilder option(Xid xid, int flags, XaStates xaStates, TransactionOption option);


        /**
         * <p>Create a new {@link TransactionInfo} instance.
         * <p><strong>NOTE</strong>: if satisfy following :
         * <ul>
         *     <li>in transaction is true</li>
         *     <li>not found {@link Option#START_MILLIS}</li>
         * </ul>
         * then this method always auto add {@link Option#START_MILLIS}.
         *
         * @throws IllegalStateException throw when in transaction and not found {@link Option#DEFAULT_ISOLATION}.
         */
        TransactionInfo build();

    }


}
