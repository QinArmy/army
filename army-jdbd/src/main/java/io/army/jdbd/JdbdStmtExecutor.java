package io.army.jdbd;

import io.army.ArmyException;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.reactive.MultiResult;
import io.army.reactive.QueryResults;
import io.army.reactive.StatementOption;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.*;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;
import io.jdbd.JdbdException;
import io.jdbd.meta.DataType;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.DataRow;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.SavePoint;
import io.jdbd.statement.BindStatement;
import io.jdbd.statement.ParametrizedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class JdbdStmtExecutor<E extends StmtExecutor, S extends DatabaseSession> extends ExecutorSupport
        implements StmtExecutor {

    final JdbdStmtExecutorFactory factory;

    final S session;

    JdbdStmtExecutor(JdbdStmtExecutorFactory factory, S session) {
        this.factory = factory;
        this.session = session;
    }


    @Override
    public final long sessionIdentifier() throws DataAccessException {
        try {
            return this.session.sessionIdentifier();
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }

    @Override
    public final boolean inTransaction() throws DataAccessException {
        try {
            return this.session.inTransaction();
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }

    @Override
    public final Mono<TransactionStatus> transactionStatus() {
        return Mono.from(this.session.transactionStatus())
                .map(this::mapToArmyTransactionStatus)
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }


    @SuppressWarnings("unchecked")
    @Override
    public final Mono<E> setTransactionCharacteristics(TransactionOption option) {
        return Mono.from(this.session.setTransactionCharacteristics(mapToJdbdTransactionOption(option)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return Mono.from(this.session.setSavePoint(readArmySetSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Mono<E> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.releaseSavePoint((SavePoint) savepoint, readArmyReleaseSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Mono<? extends StmtExecutor> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.rollbackToSavePoint((SavePoint) savepoint, readArmyRollbackSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .thenReturn((E) this);
    }

    @Override
    public final Mono<ResultStates> insert(final SimpleStmt stmt, final StatementOption option) {

        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean returningId;
        returningId = selectionList.size() == 1 && selectionList.get(0) instanceof PrimaryFieldMeta;

        final AtomicReference<io.jdbd.result.ResultStates> jdbdStatesHolder;
        final Function<CurrentRow, Boolean> extractIdFunc;
        final Supplier<Mono<ResultStates>> monoSupplier;


        if (returningId) {
            final GeneratedKeyStmt keyStmt = (GeneratedKeyStmt) stmt;
            final MappingType type = keyStmt.idField().mappingType();
            final SqlType sqlType = type.map(this.factory.serverMeta);
            jdbdStatesHolder = new AtomicReference<>(null);

            final int rowSize = keyStmt.rowSize();
            final int[] rowIndexHolder = new int[]{0};

            extractIdFunc = row -> {
                Object idValue;
                idValue = get(row, 0, type, sqlType);
                final int rowIndex = rowIndexHolder[0]++;
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, keyStmt.idField());
                }
                idValue = type.afterGet(sqlType, this.factory.mappingEnv, idValue);
                keyStmt.setGeneratedIdValue(rowIndex, idValue);
                if (row.rowNumber() != rowIndexHolder[0]) {
                    String m = String.format("jdbd row index error,expected %s but %s", rowIndexHolder[0], row.rowNumber());
                    throw new DataAccessException(m);
                }
                return Boolean.TRUE;
            };

            monoSupplier = () -> {
                if (rowSize != rowIndexHolder[0]) {
                    return Mono.error(_Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, rowIndexHolder[0]));
                }
                return Mono.just(mapToArmyInsertStates(jdbdStatesHolder.get()));
            };
        } else {
            extractIdFunc = null;
            jdbdStatesHolder = null;
            monoSupplier = null;
        }


        final BindStatement statement;
        statement = this.session.bindStatement(stmt.sqlText(), option.isPreferServerPrepare());

        Throwable error = null;
        if (option.isSupportTimeout()) {
            try {
                statement.setTimeout(option.restMillSeconds());
            } catch (Throwable e) {
                error = e;
            }
        }

        final List<SQLParam> paramGroup = stmt.paramGroup();
        final Mono<ResultStates> mono;
        if (error != null) {
            mono = Mono.error(error);
        } else if (paramGroup.size() > 0 && (error = bindParameter(statement, paramGroup)) != null) {
            mono = Mono.error(error);
        } else if (returningId) {
            mono = Flux.from(statement.executeQuery(extractIdFunc, jdbdStatesHolder::set))
                    .then(Mono.defer(monoSupplier));
        } else if (stmt instanceof GeneratedKeyStmt) {
            mono = Mono.from(statement.executeUpdate())
                    .map(states -> handleInsertStates(states, (GeneratedKeyStmt) stmt));
        } else {
            mono = Mono.from(statement.executeUpdate())
                    .map(this::mapToArmyResultStates);
        }
        return mono.onErrorMap(JdbdStmtExecutor::wrapError);

    }


    @Override
    public final Mono<ResultStates> update(final SimpleStmt stmt, final StatementOption option) {

        final BindStatement statement;
        statement = this.session.bindStatement(stmt.sqlText(), option.isPreferServerPrepare());

        Throwable error = null;
        if (option.isSupportTimeout()) {
            try {
                statement.setTimeout(option.restMillSeconds());
            } catch (Throwable e) {
                error = e;
            }
        }

        final List<SQLParam> paramGroup = stmt.paramGroup();
        final Mono<io.jdbd.result.ResultStates> mono;
        if (error != null) {
            mono = Mono.error(error);
        } else if (paramGroup.size() > 0 && (error = bindParameter(statement, paramGroup)) != null) {
            mono = Mono.error(error);
        } else {
            mono = Mono.from(statement.executeUpdate());
        }
        return mono.map(this::mapToArmyResultStates)
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @Override
    public Flux<ResultStates> batchUpdate(BatchStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> query(SimpleStmt stmt, Class<R> resultClass, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, StatementOption option) {
        return null;
    }

    @Override
    public <R> Mono<Integer> secondQuery(TwoStmtQueryStmt stmt, List<R> resultList, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQuery(BatchStmt stmt, Class<R> resultClass, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, StatementOption option) {
        return null;
    }

    @Override
    public <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, StatementOption option) {
        return null;
    }

    @Override
    public <R> Mono<Integer> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, StatementOption option) {
        return null;
    }

    @Override
    public QueryResults batchQuery(BatchStmt stmt, StatementOption option) {
        return null;
    }

    @Override
    public MultiResult multiStmt(MultiStmt stmt, StatementOption option) {
        return null;
    }


    @Override
    public final boolean isClosed() {
        return this.session.isClosed();
    }

    @Override
    public final <T> Mono<T> close() {
        final Mono<T> mono;
        mono = Mono.from(this.session.close());
        return mono.onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    /*-------------------below package instance methods-------------------*/

    abstract Function<Option<?>, ?> readJdbdTransactionOptions(io.jdbd.session.TransactionOption jdbdOption);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyTransactionOptions(TransactionOption jdbdOption);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmySetSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyReleaseSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyRollbackSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract DataType mapToJdbdDataType(MappingType mappingType, SqlType sqlType);

    @Nullable
    abstract Object get(DataRow row, int index, MappingType type, SqlType sqlType);

    /*-------------------below private instance methods-------------------*/

    /**
     * @see #transactionStatus()
     */
    private TransactionStatus mapToArmyTransactionStatus(io.jdbd.session.TransactionStatus jdbdStatus) {
        final Function<Option<?>, ?> map;
        map = readJdbdTransactionOptions(jdbdStatus);
        return null;
    }

    private io.jdbd.session.TransactionOption mapToJdbdTransactionOption(TransactionOption armyOption) {
        return null;
    }

    private ResultStates mapToArmyResultStates(io.jdbd.result.ResultStates jdbdStates) {
        return null;
    }

    /**
     * just for return id insert
     */
    private ResultStates mapToArmyInsertStates(io.jdbd.result.ResultStates jdbdStates) {
        return null;
    }


    /**
     * @see #insert(SimpleStmt, StatementOption)
     */
    private ResultStates handleInsertStates(final io.jdbd.result.ResultStates jdbdStates, final GeneratedKeyStmt stmt) {
        final int rowSize = stmt.rowSize();

        if (jdbdStates.affectedRows() != rowSize) {
            throw _Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, jdbdStates.affectedRows());
        } else if (!jdbdStates.isSupportInsertId()) {
            String m = String.format("error ,%s don't support lastInsertId() method", jdbdStates.getClass().getName());
            throw new DataAccessException(m);
        }

        final PrimaryFieldMeta<?> idField = stmt.idField();
        final MappingType type = idField.mappingType();
        final SqlType sqlType = type.map(this.factory.serverMeta);
        final MappingEnv env = this.factory.mappingEnv;

        final int lastRowIndex = rowSize - 1;

        long lastInsertedId = jdbdStates.lastInsertedId();
        BigInteger bigId = null;
        if (lastInsertedId < 0 || (lastInsertedId + rowSize) < 0) {
            bigId = new BigInteger(Long.toUnsignedString(lastInsertedId));
        }

        Object idValue;
        for (int i = 0; i < rowSize; i++) {
            if (bigId == null) {
                idValue = lastInsertedId++;
            } else {
                idValue = bigId;
                if (i < lastRowIndex) {
                    bigId = bigId.add(BigInteger.ONE);
                }
            }

            idValue = type.afterGet(sqlType, env, idValue);
            stmt.setGeneratedIdValue(i, idValue);
        }

        return mapToArmyResultStates(jdbdStates);
    }


    @Nullable
    private Throwable bindParameter(final ParametrizedStatement statement, final List<SQLParam> paramList) {
        Throwable error = null;
        try {

            final ServerMeta serverMeta = this.factory.serverMeta;
            final MappingEnv mappingEnv = this.factory.mappingEnv;
            final boolean truncatedTimeType = this.factory.truncatedTimeType;

            final int paramSize = paramList.size();

            SQLParam sqlParam;
            Object value;
            MappingType mappingType;
            TypeMeta typeMeta;
            SqlType sqlType;
            DataType dataType;
            for (int i = 0, paramIndex = 0; i < paramSize; i++) {
                sqlParam = paramList.get(i);
                typeMeta = sqlParam.typeMeta();

                if (typeMeta instanceof MappingType) {
                    mappingType = (MappingType) typeMeta;
                } else {
                    mappingType = typeMeta.mappingType();
                }

                sqlType = mappingType.map(serverMeta);
                dataType = mapToJdbdDataType(mappingType, sqlType);

                if (sqlParam instanceof SingleParam) {
                    value = ((SingleParam) sqlParam).value();
                    if (value != null) {
                        //TODO field codec
                        value = mappingType.beforeBind(sqlType, mappingEnv, value);
                    }
                    if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                        value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                    }
                    statement.bind(paramIndex++, dataType, value);
                    continue;
                }

                if (!(sqlParam instanceof MultiParam)) {
                    throw _Exceptions.unexpectedSqlParam(sqlParam);
                }

                for (final Object element : ((MultiParam) sqlParam).valueList()) {
                    value = element;
                    if (value != null) {
                        //TODO field codec
                        value = mappingType.beforeBind(sqlType, mappingEnv, element);
                    }

                    if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                        value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                    }

                    statement.bind(paramIndex++, dataType, value);

                }// inner for loop


            }// for loop
        } catch (Throwable e) {
            error = e;
        }
        return error;
    }


    /*-------------------below package static methods -------------------*/

    static ArmyException wrapError(final Throwable error) {
        final ArmyException e;
        if (error instanceof JdbdException) {
            e = new DataAccessException(error);
        } else if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else {
            e = _Exceptions.unknownError(error.getMessage(), error);
        }
        return e;
    }


    /*-------------------below private static methods -------------------*/


}
