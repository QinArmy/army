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

package io.army.transaction;

import io.army.option.Option;
import io.army.session.RmSession;
import io.army.util._StringUtils;


/**
 * <p>
 * This enum representing An XA transaction progresses states.
 * <br/>
 * <p>
 * Application developer can get {@link XaStates} by {@link TransactionInfo#valueOf(Option)} <br/>
 * when {@link RmSession} in XA transaction block. see {@link TransactionInfo#valueOf(Option)}
 * <p>
 * <br/>
 *
 * @see Option#XA_STATES
 * @see RmSession
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-states.html">XA Transaction States</a>
 * @since 0.6.0
 */
public enum XaStates {


    /**
     * <p>
     * This instance representing XA transaction started after {@code  RmSession#start(Xid, int, TransactionOption)} method.
     * <br/>
     */
    ACTIVE,

    /**
     * <p>
     * This instance representing XA transaction IDLE after {@code RmSession#end(Xid, int, Function)} method.
     * <br/>
     * <p>
     * This states support one-phase commit.
     * <br/>
     */
    IDLE,

    /**
     * <p>
     * This instance representing XA transaction PREPARED after {@code RmSession#prepare(Xid, Function)} method.
     * <br/>
     */
    PREPARED;


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
