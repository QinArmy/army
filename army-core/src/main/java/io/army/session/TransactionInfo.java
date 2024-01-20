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
import java.util.Set;
import java.util.function.Function;

/**
 * <p>This interface representing the transaction info of session.
 *
 * @since 0.6.0
 */
public interface TransactionInfo extends TransactionOption {


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
    @Override
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


    Set<Option<?>> optionSet();


    static InfoBuilder builder(boolean inTransaction, Isolation isolation, boolean readOnly) {
        return ArmyTransactionInfo.builder(inTransaction, isolation, readOnly);
    }


    @Deprecated
    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> optionFunc) {
        return ArmyTransactionInfo.create(inTransaction, isolation, readOnly, optionFunc);
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

    @Deprecated
    static <T> TransactionInfo replaceOption(TransactionInfo info, Option<T> option, T value) {
        return ArmyTransactionInfo.replaceOption(info, option, value);
    }

    static TransactionInfo forRollbackOnly(TransactionInfo info) {
        return ArmyTransactionInfo.forRollbackOnly(info);
    }

    interface InfoBuilder {

        <T> InfoBuilder option(Option<T> option, @Nonnull T value);


        TransactionInfo build();

    }


}
