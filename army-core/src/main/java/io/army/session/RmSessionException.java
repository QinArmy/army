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

import io.army.executor.DataAccessException;

/**
 * @see RmSession
 * @since 0.6.0
 */
public class RmSessionException extends DataAccessException {


    /**
     * The inclusive lower bound of the rollback codes.
     */
    public static final int XA_RBBASE = 100;

    /**
     * Indicates that the rollback was caused by an unspecified reason.
     */
    public static final int XA_RBROLLBACK = XA_RBBASE;

    /**
     * Indicates that the rollback was caused by a communication failure.
     */
    public static final int XA_RBCOMMFAIL = XA_RBBASE + 1;

    /**
     * A deadlock was detected.
     */
    public static final int XA_RBDEADLOCK = XA_RBBASE + 2;

    /**
     * A condition that violates the integrity of the resource was detected.
     */
    public static final int XA_RBINTEGRITY = XA_RBBASE + 3;

    /**
     * The resource manager rolled back the transaction branch for a reason
     * not on this list.
     */
    public static final int XA_RBOTHER = XA_RBBASE + 4;

    /**
     * A protocol error occurred in the resource manager.
     */
    public static final int XA_RBPROTO = XA_RBBASE + 5;

    /**
     * A transaction branch took too long.
     */
    public static final int XA_RBTIMEOUT = XA_RBBASE + 6;

    /**
     * May retry the transaction branch.
     */
    public static final int XA_RBTRANSIENT = XA_RBBASE + 7;

    /**
     * The inclusive upper bound of the rollback error code.
     */
    public static final int XA_RBEND = XA_RBTRANSIENT;

    /**
     * Resumption must occur where the suspension occurred.
     */
    public static final int XA_NOMIGRATE = 9;

    /**
     * The transaction branch may have been heuristically completed.
     */
    public static final int XA_HEURHAZ = 8;

    /**
     * The transaction branch has been heuristically committed.
     */
    public static final int XA_HEURCOM = 7;

    /**
     * The transaction branch has been heuristically rolled back.
     */
    public static final int XA_HEURRB = 6;

    /**
     * The transaction branch has been heuristically committed and
     * rolled back.
     */
    public static final int XA_HEURMIX = 5;

    /**
     * Routine returned with no effect and may be reissued.
     */
    public static final int XA_RETRY = 4;

    /**
     * The transaction branch was read-only and has been committed.
     */
    public static final int XA_RDONLY = 3;

    /**
     * There is an asynchronous operation already outstanding.
     */
    public static final int XAER_ASYNC = -2;

    /**
     * A resource manager error has occurred in the transaction branch.
     */
    public static final int XAER_RMERR = -3;

    /**
     * The XID is not valid.
     */
    public static final int XAER_NOTA = -4;

    /**
     * Invalid arguments were given.
     */
    public static final int XAER_INVAL = -5;

    /**
     * Routine was invoked in an improper context.
     */
    public static final int XAER_PROTO = -6;

    /**
     * Resource manager is unavailable.
     */
    public static final int XAER_RMFAIL = -7;

    /**
     * The XID already exists.
     */
    public static final int XAER_DUPID = -8;

    /**
     * The resource manager is doing work outside a global transaction.
     */
    public static final int XAER_OUTSIDE = -9;


    private final int xaCode;


    public RmSessionException(String message, int xaCode) {
        super(message);
        this.xaCode = xaCode;
    }

    public RmSessionException(String message, Throwable cause, int xaCode) {
        super(message, cause);
        this.xaCode = xaCode;
    }



    public final int getXaCode() {
        return this.xaCode;
    }


}
