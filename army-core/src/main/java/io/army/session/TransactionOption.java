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


import javax.annotation.Nullable;

/**
 * @see TransactionInfo
 * @since 0.6.0
 */
public interface TransactionOption extends TransactionSpec {

    /**
     * <p>This transaction isolation.
     *
     * @return <ul>
     * <li>null : use default isolation</li>
     * <li>non-null : use specified isolation for this transaction,but does not affect subsequent transactions</li>
     * </ul>
     */
    @Nullable
    Isolation isolation();


    static TransactionOption option() {
        return ArmyTransactionOption.option(null, false);
    }

    static TransactionOption option(@Nullable Isolation isolation) {
        return ArmyTransactionOption.option(isolation, false);
    }

    static TransactionOption option(@Nullable Isolation isolation, boolean readOnly) {
        return ArmyTransactionOption.option(isolation, readOnly);
    }

    static Builder builder() {
        return ArmyTransactionOption.builder();
    }

    interface Builder {

        /**
         * set transaction option.
         *
         * @param option transaction option key,for example :
         *               <ul>
         *                    <li>{@link Option#ISOLATION}</li>
         *                    <li>{@link Option#READ_ONLY}</li>
         *                    <li>{@link Option#NAME} ,transaction name</li>
         *                    <li>{@code  Option#WITH_CONSISTENT_SNAPSHOT}</li>
         *                    <li>{@code Option#DEFERRABLE}</li>
         *                    <li>{@link Option#WAIT}</li>
         *                    <li>{@link Option#LOCK_TIMEOUT_MILLIS}</li>
         *               </ul>
         */
        <T> Builder option(Option<T> option, @Nullable T value);

        /**
         * @throws IllegalArgumentException throw when <ul>
         *                                  <li>{@link Option#IN_TRANSACTION} exists</li>
         *                                  </ul>
         */
        TransactionOption build() throws IllegalArgumentException;


    } // Builder

}
