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


package io.army.jdbc;

import io.army.mapping.MappingType;
import io.army.session.*;
import io.army.sqltype.DataType;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.function.Function;

abstract class SQLiteExecutor extends JdbcExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SQLiteExecutor.class);


    static SyncLocalStmtExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new LocalExecutor(factory, conn, sessionName);
    }

    static SyncRmStmtExecutor rmExecutor(JdbcExecutorFactory factory, final Object connObj, String sessionName) {
        throw new UnsupportedOperationException("SQLite don't support XA transaction");
    }

    /**
     * private constructor
     */
    private SQLiteExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        super(factory, conn, sessionName);
    }


    @Override
    public final TransactionInfo sessionTransactionCharacteristics(final Function<Option<?>, ?> optionFunc)
            throws DataAccessException {

        try {
            final Isolation isolation;
            switch (this.conn.getTransactionIsolation()) {
                case Connection.TRANSACTION_SERIALIZABLE:
                    isolation = Isolation.SERIALIZABLE;
                    break;
                case Connection.TRANSACTION_READ_COMMITTED:
                    isolation = Isolation.READ_COMMITTED;
                    break;
                case Connection.TRANSACTION_REPEATABLE_READ:
                    isolation = Isolation.REPEATABLE_READ;
                    break;
                case Connection.TRANSACTION_READ_UNCOMMITTED:
                    isolation = Isolation.READ_UNCOMMITTED;
                    break;
                default:
                    throw new IllegalStateException("unknown isolation");

            }
            return TransactionInfo.notInTransaction(isolation, this.conn.isReadOnly());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @Override
    public final void setTransactionCharacteristics(final TransactionOption option) throws DataAccessException {
        final Isolation isolation;
        isolation = option.isolation();

        final int isolationLevel;

        if (isolation == null) {
            isolationLevel = Connection.TRANSACTION_NONE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            isolationLevel = Connection.TRANSACTION_REPEATABLE_READ;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            isolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
        } else {
            throw _Exceptions.unknownIsolation(isolation);
        }

        try {
            this.conn.setReadOnly(option.isReadOnly());
            if (isolation != null) {
                this.conn.setTransactionIsolation(isolationLevel);
            }
        } catch (Exception e) {
            throw handleException(e);
        }


    }

    @Override
    final void bind(PreparedStatement stmt, final int indexBasedOne, MappingType type, DataType dataType, final Object value)
            throws SQLException {

    }

    @Override
    final DataType getDataType(ResultSetMetaData meta, int indexBasedOne) throws SQLException {
        return null;
    }

    @Nullable
    @Override
    final Object get(ResultSet resultSet, int indexBasedOne, MappingType type, DataType dataType) throws SQLException {
        return null;
    }


    @Override
    final Isolation readIsolation(String level) {
        return null;
    }


    @Override
    final Logger getLogger() {
        return LOG;
    }


    private static final class LocalExecutor extends SQLiteExecutor implements SyncLocalStmtExecutor {


        private LocalExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        @Override
        public TransactionInfo startTransaction(TransactionOption option, HandleMode mode) {
            return null;
        }

        @Nullable
        @Override
        public TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Nullable
        @Override
        public TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
            return null;
        }


        @Nullable
        @Override
        TransactionInfo obtainTransaction() {
            return null;
        }


    } // LocalExecutor


}
