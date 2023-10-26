package io.army.jdbd;

import io.army.function.IntBiFunction;
import io.army.mapping.MappingType;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.Option;
import io.army.sqltype.SqlType;
import io.jdbd.meta.DataType;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import io.jdbd.session.TransactionOption;

import java.util.function.Function;

abstract class PostgreStmtExecutor<S extends DatabaseSession> extends JdbdStmtExecutor<S> {

    static ReactiveLocalStmtExecutor localExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
        throw new UnsupportedOperationException();
    }

    static ReactiveRmStmtExecutor rmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
        throw new UnsupportedOperationException();
    }


    public PostgreStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        super(factory, session);
    }

    @Override
    Function<Option<?>, ?> readJdbdTransactionOptions(TransactionOption jdbdOption) {
        return null;
    }

    @Override
    Function<io.jdbd.session.Option<?>, ?> readArmyTransactionOptions(io.army.tx.TransactionOption jdbdOption) {
        return null;
    }

    @Override
    Function<io.jdbd.session.Option<?>, ?> readArmySetSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    Function<io.jdbd.session.Option<?>, ?> readArmyReleaseSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    Function<io.jdbd.session.Option<?>, ?> readArmyRollbackSavePointOptions(Function<Option<?>, ?> optionFunc) {
        return null;
    }

    @Override
    IntBiFunction<Option<?>, ?> readJdbdRowMetaOptions(ResultRowMeta rowMeta) {
        return null;
    }

    @Override
    DataType mapToJdbdDataType(MappingType mappingType, SqlType sqlType) {
        return null;
    }

    @Override
    SqlType getColumnMeta(DataRow row, int indexBasedZero) {
        return null;
    }

    @Override
    Object get(DataRow row, int indexBasedZero, SqlType sqlType) {
        return null;
    }
}
