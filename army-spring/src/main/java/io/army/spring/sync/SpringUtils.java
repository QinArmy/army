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

package io.army.spring.sync;

import io.army.session.Isolation;
import io.army.session.Session;
import io.army.session.SessionException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;

import javax.annotation.Nullable;

public abstract class SpringUtils {

    private SpringUtils() {
        throw new UnsupportedOperationException();
    }


    public static NestedRuntimeException wrapSessionError(SessionException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }


    public static IllegalTransactionStateException transactionNoSession() {
        return new IllegalTransactionStateException("current transaction no session.");
    }


    public static org.springframework.transaction.IllegalTransactionStateException unexpectedTransactionEnd(Session session) {
        String m = String.format("%s transaction have ended , please check you code.", session);
        return new IllegalTransactionStateException(m);
    }


    @Nullable
    public static Isolation toArmyIsolation(final int springIsolation) {
        final Isolation isolation;
        switch (springIsolation) {
            case TransactionDefinition.ISOLATION_REPEATABLE_READ:
                isolation = Isolation.REPEATABLE_READ;
                break;
            case TransactionDefinition.ISOLATION_READ_COMMITTED:
                isolation = Isolation.READ_COMMITTED;
                break;
            case TransactionDefinition.ISOLATION_SERIALIZABLE:
                isolation = Isolation.SERIALIZABLE;
                break;
            case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                isolation = Isolation.READ_UNCOMMITTED;
                break;
            case TransactionDefinition.ISOLATION_DEFAULT:
                isolation = null;
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown springIsolation[%s]", springIsolation));
        }
        return isolation;
    }

}
