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


import io.army.criteria.Visible;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * <p>This interface representing database session.
 * <p>This interface is direct base interface of following :
 * <ul>
 *     <li>{@link LocalSession}</li>
 *     <li>{@link RmSession}</li>
 *     <li>{@code io.army.sync.SyncSession}</li>
 *     <li>{@code io.army.reactive.ReactiveSession}</li>
 * </ul>
 *
 * @see SessionFactory
 */
public interface Session extends CloseableSpec, OptionSpec {


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    String name();

    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     * <ul>
     *     <li>server process id</li>
     *     <li>server thread id</li>
     *     <li>other identifier</li>
     * </ul>
     * <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * *
     *
     * @throws SessionException throw when session have closed.
     */
    long sessionIdentifier() throws SessionException;

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    SessionFactory sessionFactory();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReadonlySession();

    /**
     * @return session in transaction block.
     * @throws SessionException throw when session have closed
     */
    boolean inTransaction() throws SessionException;

    /**
     * <p>Test session is whether rollback only or not.
     * <p> How to mark {@link Session}'s rollback only status ?
     * <ul>
     *     <li>local transaction  :
     *          <ol>
     *              <li>{@link #markRollbackOnly()}</li>
     *              <li>throw {@link ChildUpdateException} when execute dml</li>
     *          </ol>
     *     </li>
     *     <li>XA transaction :
     *          <ol>
     *              <li>{@link #markRollbackOnly()}</li>
     *              <li>pass {@link RmSession#TM_FAIL} flag to {@link RmSession}'s end() method</li>
     *              <li>throw {@link ChildUpdateException} when execute dml</li>
     *          </ol>
     *     </li>
     * </ul>
     * <p> How to clear {@link Session}'s rollback only status ?
     * <ul>
     *     <li>local transaction  :
     *          <ol>
     *              <li>rollback transaction</li>
     *              <li>start new transaction</li>
     *          </ol>
     *     </li>
     *     <li>XA transaction :
     *          <ol>
     *              <li>prepare current transaction</li>
     *              <li>one phase rollback transaction</li>
     *              <li>start new transaction</li>
     *          </ol>
     *     </li>
     * </ul>
     * <p><strong>NOTE</strong> : This method don't check session whether closed or not.
     *
     * @return true : session is rollback only.
     * @see #markRollbackOnly()
     * @see RmSession#TM_FAIL
     */
    boolean isRollbackOnly();

    /**
     * <p>Mark session rollback only
     * <p>More info ,see {@link #isRollbackOnly()}
     *
     * @throws SessionException throw when session have closed.
     * @see #isRollbackOnly()
     */
    void markRollbackOnly();

    /**
     * <p>Test session whether hold one  {@link TransactionInfo} instance or not, the instance is current transaction info of this session.
     * <p><strong>NOTE</strong> :
     * <ol>
     *     <li>This method don't check whether session closed or not</li>
     *     <li>This method don't invoke {@link TransactionInfo#inTransaction()} method</li>
     * </ol>
     * <pre>The implementation of this method like following
     *         <code><br/>
     *   &#64;Override
     *   public boolean hasTransactionInfo() {
     *       return this.transactionInfo != null;
     *   }
     *         </code>
     * </pre>
     *
     * @return true : session hold one  {@link TransactionInfo} instance.
     */
    boolean hasTransactionInfo();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReadOnlyStatus();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReactive();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isSync();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    Visible visible();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isQueryInsertAllowed();


    boolean inPseudoTransaction();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @throws IllegalArgumentException throw,when not found {@link TableMeta}.
     */
    <T> TableMeta<T> tableMeta(Class<T> domainClass);

    /**
     * @param key The key of the attribute to return
     * @return The attribute
     */
    @Nullable
    Object getAttribute(Object key);

    /**
     * Set a custom attribute.
     *
     * @param key   The attribute name
     * @param value The attribute value
     */
    void setAttribute(Object key, Object value);

    /**
     * @return all the attributes names,a unmodified set.
     */
    Set<Object> getAttributeKeys();

    /**
     * Remove the attribute
     *
     * @param key The attribute key
     * @return the attribute value if found, null otherwise
     */
    @Nullable
    Object removeAttribute(Object key);

    int attributeSize();

    /**
     * @return a unmodified set.
     */
    Set<Map.Entry<Object, Object>> attributeEntrySet();

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #name()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


    /**
     * <p>This interface is base interface of following :
     * <ul>
     *     <li>{@link RmSession}</li>
     *     <li>RM {@link io.army.session.executor.StmtExecutor}</li>
     * </ul>
     * /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 0.6.0
     */
    interface XaTransactionSupportSpec {

        boolean isSupportForget();

        /**
         * @return the sub set of {@code  #start(Xid, int, TransactionOption)} support flags(bit set).
         */
        int startSupportFlags();

        /**
         * @return the sub set of {@code #end(Xid, int, Function)} support flags(bit set).
         */
        int endSupportFlags();

        /**
         * @return the sub set of {@code #commit(Xid, int, Function)} support flags(bit set).
         */
        int commitSupportFlags();

        /**
         * @return the sub set of {@code #recover(int, Function)} support flags(bit set).
         */
        int recoverSupportFlags();


        /**
         * @throws SessionException throw when underlying database session have closed.
         */
        boolean isSameRm(XaTransactionSupportSpec s) throws SessionException;

    }


}
