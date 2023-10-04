package io.army.jdbd;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.function.IntBiFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.reactive.QueryResults;
import io.army.reactive.ReactiveOption;
import io.army.reactive.executor.ReactiveExecutorSupport;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.*;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import io.army.type.ImmutableSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;
import io.jdbd.JdbdException;
import io.jdbd.meta.DataType;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.SavePoint;
import io.jdbd.statement.BindStatement;
import io.jdbd.statement.ParametrizedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;


/**
 * <p>This class is a abstract implementation of {@link StmtExecutor} with jdbd spi.
 * <p>This class is base class of following jdbd executor:
 * <ul>
 *     <li>{@link MySQLStmtExecutor}</li>
 *     <li>{@link PostgreStmtExecutor}</li>
 * </ul>
 * <p>Following is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @param <S> the java type of {@link DatabaseSession}
 * @see <a href="https://github.com/QinArmy/jdbd">jdbd-spi</a>
 */
abstract class JdbdStmtExecutor<S extends DatabaseSession> extends ReactiveExecutorSupport
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
    public final Mono<TransactionInfo> transactionStatus() {
        return Mono.from(this.session.transactionStatus())
                .map(this::mapToArmyTransactionStatus)
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @Override
    public final Mono<Void> setTransactionCharacteristics(TransactionOption option) {
        return Mono.from(this.session.setTransactionCharacteristics(mapToJdbdTransactionOption(option)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .then();
    }

    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return Mono.from(this.session.setSavePoint(readArmySetSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    @Override
    public final Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.releaseSavePoint((SavePoint) savepoint, readArmyReleaseSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .then();
    }


    @Override
    public final Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.rollbackToSavePoint((SavePoint) savepoint, readArmyRollbackSavePointOptions(optionFunc)))
                .onErrorMap(JdbdStmtExecutor::wrapError)
                .then();
    }

    @Override
    public final Mono<ResultStates> insert(final SimpleStmt stmt, final ReactiveOption option) {

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
                idValue = get(row, 0, sqlType);
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
                return Mono.just(mapToArmyResultStates(jdbdStatesHolder.get()));
            };
        } else {
            extractIdFunc = null;
            jdbdStatesHolder = null;
            monoSupplier = null;
        }


        final BindStatement statement;
        try {
            statement = bindStatement(stmt, option);
        } catch (Throwable e) {
            return Mono.error(wrapError(e));
        }

        final Mono<ResultStates> mono;
        if (returningId) {
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
    public final Mono<ResultStates> update(final SimpleStmt stmt, final ReactiveOption option) {
        Mono<ResultStates> mono;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            mono = Mono.from(statement.executeUpdate())
                    .map(this::mapToArmyResultStates)
                    .onErrorMap(JdbdStmtExecutor::wrapError);
        } catch (Throwable e) {
            mono = Mono.error(wrapError(e));
        }
        return mono;
    }

    @Override
    public final Flux<ResultStates> batchUpdate(final BatchStmt stmt, final ReactiveOption option) {
        Flux<ResultStates> flux;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            flux = Flux.from(statement.executeBatchUpdate())
                    .map(this::mapToArmyResultStates)
                    .onErrorMap(JdbdStmtExecutor::wrapError);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> query(final SimpleStmt stmt, final Class<R> resultClass, final ReactiveOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapBeanFunc(stmt, resultClass), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<Optional<R>> queryOptional(SimpleStmt stmt, final Class<R> resultClass, ReactiveOption option) {
        Flux<Optional<R>> flux;
        try {
            final List<? extends Selection> selectionList;
            selectionList = stmt.selectionList();
            if (selectionList.size() != 1) {
                return Flux.error(new IllegalArgumentException("queryOptional method support only single selection"));
            }

            final OptionalSingleColumnRowReader<R> rowReader;
            rowReader = new OptionalSingleColumnRowReader<>(this, selectionList, resultClass);

            final Function<CurrentRow, Optional<R>> function;
            if (stmt instanceof GeneratedKeyStmt) {
                function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
            } else {
                function = rowReader::readOneRow;
            }
            flux = executeQuery(stmt, function, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, ReactiveOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapObjectFunc(stmt, constructor), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, ReactiveOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapRecordFunc(stmt, function), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> secondQuery(TwoStmtQueryStmt stmt, List<R> resultList, ReactiveOption option) {
        Flux<R> flux;
        try {
            final SecondRowReader<R> rowReader;
            rowReader = new SecondRowReader<>(this, stmt, resultList);

            flux = executeQuery(stmt, rowReader::readOneRow, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchStmt stmt, Class<R> resultClass, ReactiveOption option) {
        Flux<R> flux;
        try {
            final List<? extends Selection> selectionList = stmt.selectionList();
            final RowReader<R> rowReader;
            if (selectionList.size() > 1) {
                // TODO fix me for firebird
                rowReader = new BeanReader<>(this, selectionList, resultClass);
            } else {
                rowReader = new SingleColumnRowReader<>(this, selectionList, resultClass);
            }
            // NOTE : batchQuery method can use same RowReader instance
            flux = executeBatchQuery(stmt, rowReader::readOneRow, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, ReactiveOption option) {
        Flux<R> flux;
        try {
            final RowReader<R> rowReader;
            rowReader = new ObjectRowReader<>(this, stmt.selectionList(), constructor, stmt instanceof TwoStmtModeQuerySpec);
            flux = executeBatchQuery(stmt, rowReader::readOneRow, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, ReactiveOption option) {
        Flux<R> flux;
        try {
            final RowReader<R> rowReader;
            rowReader = new CurrentRecordRowReader<>(this, stmt.selectionList(), function);
            flux = executeBatchQuery(stmt, rowReader::readOneRow, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapError(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, ReactiveOption option) {
        // TODO for firebird
        return Flux.error(new DataAccessException("currently,don't support"));
    }

    @Override
    public final QueryResults batchQueryResults(BatchStmt stmt, ReactiveOption option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Flux<ResultItem> execute(final GenericSimpleStmt stmt, final ReactiveOption option) {
//        Flux<ResultItem> flux;
//        try {
//            final BindStatement statement;
//            statement = bindStatement(stmt, option);
//            if (stmt instanceof BatchStmt) {
//                flux = Flux.from(statement.executeBatchAsFlux());
//            } else {
//                 flux = Flux.from(statement.executeAsFlux());
//            }
//        } catch (Throwable e) {
//            flux = Flux.error(wrapError(e));
//        }
//        return flux;
        throw new UnsupportedOperationException();
    }

    @Override
    public final Flux<ResultItem> executeMultiStmt(List<GenericSimpleStmt> stmtList, ReactiveOption option) {
        // TODO
        throw new UnsupportedOperationException();
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

    abstract Function<io.jdbd.session.Option<?>, ?> readArmySetSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyReleaseSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract Function<io.jdbd.session.Option<?>, ?> readArmyRollbackSavePointOptions(Function<Option<?>, ?> optionFunc);

    abstract IntBiFunction<Option<?>, ?> readJdbdRowMetaOptions(ResultRowMeta rowMeta);

    abstract Function<Option<?>, io.jdbd.session.Option<?>> mapToJdbdOptionFunc();

    /**
     * @see #transactionStatus()
     */
    abstract TransactionInfo mapToArmyTransactionStatus(io.jdbd.session.TransactionStatus jdbdStatus)


    abstract io.jdbd.session.TransactionOption mapToJdbdTransactionOption(TransactionOption armyOption);

    @Nullable
    abstract io.jdbd.session.Option<?> mapToJdbdOption(Option<?> option);

    @Nullable
    abstract Option<?> mapToArmyOption(io.jdbd.session.Option<?> option);


    abstract DataType mapToJdbdDataType(MappingType mappingType, SqlType sqlType);

    abstract SqlType getColumnMeta(DataRow row, int indexBasedZero);

    @Nullable
    abstract Object get(DataRow row, int indexBasedZero, SqlType sqlType);

    /*-------------------below private instance methods-------------------*/



    private ResultStates mapToArmyResultStates(io.jdbd.result.ResultStates jdbdStates) {
        return new ArmyResultStates(jdbdStates, this::mapToJdbdOption);
    }


    /**
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private <R> Flux<R> executeQuery(final SimpleStmt stmt, final Function<CurrentRow, R> func,
                                     final ReactiveOption option) throws JdbdException, TimeoutException {
        return Flux.from(bindStatement(stmt, option).executeQuery(func, createStatesConsumer(option)))
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }

    /**
     * @see #batchQuery(BatchStmt, Class, ReactiveOption)
     * @see #batchQueryObject(BatchStmt, Supplier, ReactiveOption)
     * @see #batchQueryRecord(BatchStmt, Function, ReactiveOption)
     */
    private <R> Flux<R> executeBatchQuery(final BatchStmt stmt, final Function<CurrentRow, R> func,
                                          final ReactiveOption option) throws JdbdException, TimeoutException {
        return Flux.from(bindStatement(stmt, option).executeBatchQueryAsFlux(func, createStatesConsumer(option)))
                .onErrorMap(JdbdStmtExecutor::wrapError);
    }


    /**
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private <R> Function<CurrentRow, R> mapBeanFunc(final SimpleStmt stmt, final Class<R> resultClass) {
        final List<? extends Selection> selectionList;
        selectionList = stmt.selectionList();

        final RowReader<R> rowReader;
        if (selectionList.size() > 1
                || !(stmt instanceof TwoStmtModeQuerySpec)
                || ((TwoStmtModeQuerySpec) stmt).maxColumnSize() > 1) {
            rowReader = new BeanReader<>(this, selectionList, resultClass);
        } else {
            rowReader = new SingleColumnRowReader<>(this, selectionList, resultClass);
        }

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }

    /**
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private <R> Function<CurrentRow, R> mapObjectFunc(final SimpleStmt stmt, final Supplier<R> constructor) {

        final RowReader<R> rowReader;
        rowReader = new ObjectRowReader<>(this, stmt.selectionList(), constructor, stmt instanceof TwoStmtModeQuerySpec);

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }

    /**
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private <R> Function<CurrentRow, R> mapRecordFunc(final SimpleStmt stmt, final Function<CurrentRecord, R> recordFunc) {
        final RowReader<R> rowReader;
        rowReader = new CurrentRecordRowReader<>(this, stmt.selectionList(), recordFunc);

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }


    /**
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private Consumer<io.jdbd.result.ResultStates> createStatesConsumer(final ReactiveOption option) {
        final Consumer<ResultStates> armyConsumer;
        armyConsumer = option.stateConsumer();
        if (armyConsumer == ResultStates.IGNORE_STATES) {
            return io.jdbd.result.ResultStates.IGNORE_STATES;
        }
        return states -> {
            final ResultStates armyStates;
            armyStates = mapToArmyResultStates(states);
            try {
                armyConsumer.accept(armyStates);
            } catch (Exception e) {
                String m = String.format("%s %s throw error, %s", ResultStates.class.getName(),
                        armyConsumer, e.getMessage());
                throw new ArmyException(m);
            }
        };
    }

    /**
     * @see #mapBeanFunc(SimpleStmt, Class)
     */
    private <R> Function<CurrentRow, R> returnIdQueryRowFunc(final GeneratedKeyStmt keyStmt,
                                                             final RowReader<R> rowReader) {


        final int indexBasedZero = keyStmt.idSelectionIndex();
        final MappingType type = keyStmt.idField().mappingType();
        final SqlType sqlType = type.map(this.factory.serverMeta);

        final int[] rowIndexHolder = new int[]{0};
        return dataRow -> {
            final int rowIndex = rowIndexHolder[0]++;
            if (dataRow.rowNumber() != rowIndexHolder[0]) {
                throw jdbdRowNumberNotMatch(rowIndex, dataRow.rowNumber());
            }
            Object idValue;
            idValue = get(dataRow, indexBasedZero, sqlType);

            if (idValue == null) {
                throw _Exceptions.idValueIsNull(rowIndex, keyStmt.idField());
            }
            idValue = type.afterGet(sqlType, this.factory.mappingEnv, idValue);
            keyStmt.setGeneratedIdValue(rowIndex, idValue);

            return rowReader.readOneRow(dataRow);
        };
    }


    /**
     * @see #insert(SimpleStmt, ReactiveOption)
     */
    private ResultStates handleInsertStates(final io.jdbd.result.ResultStates jdbdStates,
                                            final GeneratedKeyStmt stmt) {
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


    private BindStatement bindStatement(final GenericSimpleStmt stmt, final ReactiveOption option)
            throws TimeoutException, JdbdException {
        final BindStatement statement;
        statement = this.session.bindStatement(stmt.sqlText(), option.isPreferServerPrepare());
        if (option.isSupportTimeout()) {
            statement.setTimeout(option.restMillSeconds());
        }

        final int fetchSize = option.fetchSize();
        if (fetchSize > 0) {
            statement.setFetchSize(fetchSize);
        }

        if (stmt instanceof BatchStmt) {
            final List<List<SQLParam>> groupList = ((BatchStmt) stmt).groupList();
            final int groupSize = groupList.size();
            for (int i = 0; i < groupSize; i++) {
                bindParameter(statement, groupList.get(i));
                statement.addBatch();
            }
        } else if (stmt instanceof SimpleStmt) {
            bindParameter(statement, ((SimpleStmt) stmt).paramGroup());
        } else {
            throw _Exceptions.unexpectedStmt(stmt);
        }
        return statement;
    }


    @Nullable
    private void bindParameter(final ParametrizedStatement statement, final List<SQLParam> paramList) {

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

    private static DataAccessException jdbdRowNumberNotMatch(int rowIndex, long jdbdRowNumber) {
        String m = String.format("jdbd row index error,expected %s but %s", rowIndex, jdbdRowNumber);
        return new DataAccessException(m);
    }

    /*-------------------below static class -------------------*/

    private static abstract class RowReader<R> {

        final JdbdStmtExecutor<?> executor;

        final List<? extends Selection> selectionList;

        private final SqlType[] sqlTypeArray;

        private final MappingType[] compatibleTypeArray;

        private final Class<?> resultClass;

        private RowReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                          @Nullable Class<?> resultClass) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = new SqlType[selectionList.size()];
            this.compatibleTypeArray = new MappingType[this.sqlTypeArray.length];

            this.resultClass = resultClass;
        }


        @Nullable
        final R readOneRow(final DataRow dataRow) {

            final JdbdStmtExecutor<?> executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final SqlType[] sqlTypeArray = this.sqlTypeArray;
            final List<? extends Selection> selectionList = this.selectionList;

            final MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            final int columnCount;
            if (sqlTypeArray[0] == null
                    || (this instanceof CurrentRecordRowReader
                    && ((CurrentRecordRowReader<?>) this).resultNo < dataRow.getResultNo())) {
                columnCount = dataRow.getColumnCount();
                if (columnCount != sqlTypeArray.length) {
                    throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, sqlTypeArray.length);
                }
                for (int i = 0; i < columnCount; i++) {
                    sqlTypeArray[i] = executor.getColumnMeta(dataRow, i);
                }
                if (this instanceof CurrentRecordRowReader) {
                    ((CurrentRecordRowReader<?>) this).acceptRowMeta(dataRow.getRowMeta(), this::getSqlType);
                }
            } else {
                columnCount = sqlTypeArray.length;
            }

            final ObjectAccessor accessor;
            accessor = createRow();

            MappingType type;
            Selection selection;
            Object columnValue;
            SqlType sqlType;
            String fieldName;

            for (int i = 0; i < columnCount; i++) {
                sqlType = sqlTypeArray[i];
                columnValue = executor.get(dataRow, i, sqlType);

                selection = selectionList.get(i);
                fieldName = selection.label();

                if (columnValue == null) {
                    acceptColumn(i, fieldName, null);
                    continue;
                }

                if ((type = compatibleTypeArray[i]) == null) {
                    type = compatibleTypeFrom(selection, this.resultClass, accessor, fieldName);
                    compatibleTypeArray[i] = type;
                }

                columnValue = type.afterGet(sqlType, env, columnValue);
                //TODO field codec
                acceptColumn(i, fieldName, columnValue);

            } // for loop

            return endOneRow();
        }


        abstract ObjectAccessor createRow();

        abstract void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value);

        @Nullable
        abstract R endOneRow();


        private SqlType getSqlType(int index) {
            return this.sqlTypeArray[index];
        }


    }// RowReader

    private static final class SingleColumnRowReader<R> extends RowReader<R> {

        private R row;

        private SingleColumnRowReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                                      Class<R> resultClass) {
            super(executor, selectionList, resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = null;
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @SuppressWarnings("unchecked")
        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            assert indexBasedZero == 0;
            this.row = (R) value;
        }

        @Override
        R endOneRow() {
            final R row = this.row;
            this.row = null;
            return row;
        }

    }// SingleColumnRowReader

    private static final class OptionalSingleColumnRowReader<R> extends RowReader<Optional<R>> {

        private R row;

        private OptionalSingleColumnRowReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                                              Class<R> resultClass) {
            super(executor, selectionList, resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = null;
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @SuppressWarnings("unchecked")
        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            assert indexBasedZero == 0;
            this.row = (R) value;
        }

        @Override
        Optional<R> endOneRow() {
            final R row = this.row;
            this.row = null;
            return Optional.ofNullable(row);
        }


    }// OptionalSingleColumnRowReader

    private static final class BeanReader<R> extends RowReader<R> {

        private final ObjectAccessor accessor;
        private final Constructor<R> constructor;


        private R row;

        private BeanReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                           Class<R> resultClass) {
            super(executor, selectionList, resultClass);
            this.accessor = ObjectAccessorFactory.forBean(resultClass);
            this.constructor = ObjectAccessorFactory.getConstructor(resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = ObjectAccessorFactory.createBean(this.constructor);
            return this.accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }

        @Override
        R endOneRow() {
            final R row = this.row;
            this.row = null;
            return row;
        }

    }// BeanReader


    private static final class ObjectRowReader<R> extends RowReader<R> {

        private final Supplier<R> constructor;

        private final boolean twoStmtMode;

        private R row;

        private ObjectAccessor accessor;

        private ObjectRowReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                                Supplier<R> constructor, boolean twoStmtMode) {
            super(executor, selectionList, null);
            this.constructor = constructor;
            this.twoStmtMode = twoStmtMode;
        }

        @Override
        ObjectAccessor createRow() {
            final R row;
            row = this.constructor.get();
            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }

            final ObjectAccessor accessor;
            accessor = ObjectAccessorFactory.fromInstance(row);

            this.row = row;
            this.accessor = accessor;
            return accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.row;

            if (!this.twoStmtMode && row instanceof Map && row instanceof ImmutableSpec) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }
            this.row = null;
            return row;
        }


    }// ObjectReader

    private static final class SecondRowReader<R> extends RowReader<R> {

        private final List<R> rowList;

        private final int rowSize;

        private final boolean singleColumn;

        private ObjectAccessor accessor;

        private R row;

        /**
         * from -1 not 0
         */
        private int rowIndex = -1;


        private SecondRowReader(JdbdStmtExecutor<?> executor, TwoStmtQueryStmt stmt, List<R> rowList) {
            super(executor, stmt.selectionList(), rowResultClass(rowList.get(0)));
            this.rowList = rowList;
            this.rowSize = rowList.size();
            this.singleColumn = stmt.maxColumnSize() == 1;
        }

        @Override
        ObjectAccessor createRow() {
            final int rowIndex = ++this.rowIndex;
            if (rowIndex >= this.rowSize) {
                throw secondQueryRowCountNotMatch(this.rowSize, rowIndex + 1);
            }

            final R row;
            this.row = row = this.rowList.get(rowIndex);

            final ObjectAccessor accessor;
            if (this.singleColumn) {
                accessor = ObjectAccessorFactory.PSEUDO_ACCESSOR;
            } else {
                accessor = ObjectAccessorFactory.fromInstance(row);
            }
            this.accessor = accessor;
            return accessor;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            if (!this.singleColumn) {
                this.accessor.set(this.row, fieldName, value);
            } else if (Objects.equals(value, this.row)) {
                assert indexBasedZero == 0;
            } else {
                String m = String.format("error , single column row[rowIndexBasedZero : %s ,indexBasedZero : %s , selection label : %s] and first query not match.",
                        this.rowIndex, indexBasedZero, fieldName);
                throw new DataAccessException(m);
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.row;
            if (row instanceof Map && row instanceof ImmutableSpec) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }

            this.row = null;
            return row;
        }

    }// SecondRowReader

    private static final class CurrentRecordRowReader<R> extends RowReader<R> {

        private final Function<CurrentRecord, R> function;

        private JdbdCurrentRecord currentRecord;

        private int currentIndex;

        private int resultNo = 0;

        private CurrentRecordRowReader(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                                       Function<CurrentRecord, R> function) {
            super(executor, selectionList, null);
            this.function = function;
        }

        @Override
        ObjectAccessor createRow() {
            this.currentIndex = 0;
            final JdbdCurrentRecord record = this.currentRecord;
            assert record != null;
            record.rowCount++;
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            final int currentIndex = this.currentIndex++;
            assert indexBasedZero == currentIndex;
            final JdbdCurrentRecord record = this.currentRecord;
            assert record != null;
            record.valueArray[indexBasedZero] = value;
        }

        @Override
        R endOneRow() {
            final JdbdCurrentRecord record = this.currentRecord;
            assert record != null;
            assert this.currentIndex == record.valueArray.length;

            final R row;
            row = this.function.apply(record);
            if (row instanceof CurrentRecord) {
                throw _Exceptions.recordMapFuncReturnError(this.function);
            }
            return row;
        }

        private void acceptRowMeta(final ResultRowMeta rowMeta, final IntFunction<SqlType> sqlTypeFunc) {
            final IntBiFunction<Option<?>, ?> optionFunc;
            optionFunc = this.executor.readJdbdRowMetaOptions(rowMeta);

            final ArmyResultRecordMeta recordMeta;
            recordMeta = new ArmyResultRecordMeta(rowMeta.getResultNo(), this.selectionList, sqlTypeFunc, optionFunc);

            this.resultNo = rowMeta.getResultNo();
            this.currentRecord = new JdbdCurrentRecord(recordMeta);

        }


    }//CurrentRecordRowReader

    private static final class JdbdCurrentRecord extends ArmyCurrentRecord {

        private final Object[] valueArray;

        private long rowCount = 0;

        private JdbdCurrentRecord(ArmyResultRecordMeta meta) {
            super(meta);
            this.valueArray = new Object[meta.getColumnCount()];
        }

        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[this.meta.checkIndex(indexBasedZero)];
        }

        @Override
        public long rowNumber() {
            return this.rowCount;
        }

        @Override
        protected Object[] copyValueArray() {
            final Object[] copy = new Object[this.valueArray.length];
            System.arraycopy(this.valueArray, 0, copy, 0, copy.length);
            return copy;
        }


    }// JdbdCurrentRecord

    private static abstract class JdbdBatchQueryResults extends ArmyReactiveMultiResultSpec {

        private final JdbdStmtExecutor<?> executor;

        private final List<? extends Selection> selectionList;

        private final io.jdbd.result.QueryResults jdbdResults;


        private JdbdBatchQueryResults(JdbdStmtExecutor<?> executor, List<? extends Selection> selectionList,
                                      io.jdbd.result.QueryResults jdbdResults) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.jdbdResults = jdbdResults;
        }

        @Override
        public final <R> Flux<R> nextQuery(Class<R> resultClass, Consumer<ResultStates> consumer) {
            final RowReader<R> reader;
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList.size() == 1) {
                reader = new SingleColumnRowReader<>(this.executor, selectionList, resultClass);
            } else {
                reader = new BeanReader<>(this.executor, selectionList, resultClass);
            }
            return Flux.from(this.jdbdResults.nextQuery(reader::readOneRow, getJdbdStatesConsumer(consumer)));
        }

        @Override
        public final <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass, Consumer<ResultStates> consumer) {
            return Flux.empty();
        }

        @Override
        public final <R> Flux<R> nextQueryObject(Supplier<R> constructor, Consumer<ResultStates> consumer) {
            final RowReader<R> reader;
            reader = new ObjectRowReader<>(this.executor, this.selectionList, constructor, false);
            return Flux.from(this.jdbdResults.nextQuery(reader::readOneRow, getJdbdStatesConsumer(consumer)));
        }

        @Override
        public final <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function, Consumer<ResultStates> consumer) {
            //TODO
            return Flux.empty();
        }

        @Override
        public final Flux<ResultItem> nextQueryAsFlux() {
            return Flux.empty();
        }

        Consumer<io.jdbd.result.ResultStates> getJdbdStatesConsumer(Consumer<ResultStates> armyConsumer) {
            throw new UnsupportedOperationException();
        }


    }// JdbdMultiResultSpec

    private static final class ArmyResultStates implements ResultStates {

        private final io.jdbd.result.ResultStates jdbdStates;

        private final Function<Option<?>, io.jdbd.session.Option<?>> optionFunc;

        private Warning warning;


        private ArmyResultStates(io.jdbd.result.ResultStates jdbdStates,
                                 Function<Option<?>, io.jdbd.session.Option<?>> optionFunc) {
            this.jdbdStates = jdbdStates;
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = this.optionFunc.apply(option);
            final Object value;
            if (jdbdOption == null) {
                value = null;
            } else {
                value = this.jdbdStates.valueOf(jdbdOption);
            }
            return (T) value;
        }

        @Override
        public int getResultNo() {
            return this.jdbdStates.getResultNo();
        }

        @Override
        public boolean inTransaction() {
            try {
                return this.jdbdStates.inTransaction();
            } catch (Throwable e) {
                throw wrapError(e);
            }
        }

        @Override
        public String message() {
            return this.jdbdStates.message();
        }

        @Override
        public boolean hasMoreResult() {
            return this.jdbdStates.hasMoreResult();
        }

        @Override
        public boolean hasMoreFetch() {
            return this.jdbdStates.hasMoreFetch();
        }

        @Override
        public Warning warning() {
            Warning w = this.warning;
            if (w != null) {
                return w;
            }
            final io.jdbd.result.Warning jdbdWarning;
            jdbdWarning = this.jdbdStates.warning();
            if (jdbdWarning != null) {
                this.warning = w = new ArmyWarning(jdbdWarning, this.optionFunc);
            }
            return w;
        }

        @Override
        public long affectedRows() {
            return this.jdbdStates.affectedRows();
        }

        @Override
        public boolean hasColumn() {
            return this.jdbdStates.hasColumn();
        }

        @Override
        public long rowCount() {
            return this.jdbdStates.rowCount();
        }


    }// ArmyResultStates


    private static final class ArmyWarning implements Warning {

        private final io.jdbd.result.Warning jdbdWarning;


        private final Function<Option<?>, io.jdbd.session.Option<?>> optionFunc;

        private ArmyWarning(io.jdbd.result.Warning jdbdWarning,
                            Function<Option<?>, io.jdbd.session.Option<?>> optionFunc) {
            this.jdbdWarning = jdbdWarning;
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = this.optionFunc.apply(option);
            final Object value;
            if (jdbdOption == null) {
                value = null;
            } else {
                value = this.jdbdWarning.valueOf(jdbdOption);
            }
            return (T) value;
        }

        @Override
        public String message() {
            return this.jdbdWarning.message();
        }

    }// ArmyWarning


}
