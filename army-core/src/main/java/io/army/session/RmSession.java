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


/**
 * <p>This interface representing RM(Resource Manager) {@link Session} in XA transaction.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code io.army.sync.SyncRmSession}</li>
 *     <li>{@code io.army.reactive.ReactiveRmSession}</li>
 * </ul>
 * <p>This interface extends {@link Session} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 * @since 0.6.0
 */
public sealed interface RmSession extends Session, Session.XaTransactionSupportSpec permits PackageSession.PackageRmSession {


    /**
     * Use TM_NO_FLAGS to indicate no flags value is selected.
     */
    byte TM_NO_FLAGS = 0;

    /**
     * Caller is joining existing transaction branch.
     */
    int TM_JOIN = 1 << 21;

    /**
     * Ends a recovery scan.
     */
    int TM_END_RSCAN = 1 << 23;

    /**
     * Starts a recovery scan.
     */
    int TM_START_RSCAN = 1 << 24;

    /**
     * Caller is suspending (not ending) its association with
     * a transaction branch.
     */
    int TM_SUSPEND = 1 << 25;

    /**
     * Disassociates caller from a transaction branch.
     */
    int TM_SUCCESS = 1 << 26;

    /**
     * Caller is resuming association with a suspended
     * transaction branch.
     */
    int TM_RESUME = 1 << 27;

    /**
     * <p>Disassociates the caller and marks the transaction branch rollback-only.
     */
    int TM_FAIL = 1 << 29;

    /**
     * Caller is using one-phase optimization.
     */
    int TM_ONE_PHASE = 1 << 30;

    /**
     * The transaction branch has been read-only and has been committed.
     */
    byte XA_RDONLY = 3;

    /**
     * The transaction work has been prepared normally.
     */
    byte XA_OK = 0;


}
