package io.army.jdbd;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
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
import io.army.reactive.ReactiveOption;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.*;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.type.ImmutableSpec;
import io.army.util._Collections;
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

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
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
                return Mono.just(mapToArmyInsertStates(jdbdStatesHolder.get()));
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
        return null;
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, ReactiveOption option) {
        return null;
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, ReactiveOption option) {
        return null;
    }

    @Override
    public final <R> Flux<R> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, ReactiveOption option) {
        // TODO for firebird
        return Flux.error(new UnsupportedOperationException("batchQueryRecord"));
    }

    @Override
    public final QueryResults batchQueryResults(BatchStmt stmt, ReactiveOption option) {
        return null;
    }

    @Override
    public final MultiResult multiStmt(MultiStmt stmt, ReactiveOption option) {
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

    abstract SqlType getColumnMeta(DataRow row, int indexBasedZero);

    @Nullable
    abstract Object get(DataRow row, int indexBasedZero, SqlType sqlType);

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
     * @see #query(SimpleStmt, Class, ReactiveOption)
     * @see #queryObject(SimpleStmt, Supplier, ReactiveOption)
     * @see #queryRecord(SimpleStmt, Function, ReactiveOption)
     */
    private <R> Flux<R> executeQuery(final SimpleStmt stmt, final Function<CurrentRow, R> func,
                                     final ReactiveOption option) throws JdbdException, TimeoutException {
        final BindStatement statement;
        statement = bindStatement(stmt, option);
        return Flux.from(statement.executeQuery(func, createStatesConsumer(option)));
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
        rowReader = new RecordRowReader<>(this, stmt.selectionList(), recordFunc);

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

        private final JdbdStmtExecutor<?, ?> executor;

        final List<? extends Selection> selectionList;

        private final SqlType[] sqlTypeArray;

        private final MappingType[] compatibleTypeArray;

        private final Class<R> resultClass;

        private RowReader(JdbdStmtExecutor<?, ?> executor, List<? extends Selection> selectionList,
                          @Nullable Class<R> resultClass) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = new SqlType[selectionList.size()];
            this.compatibleTypeArray = new MappingType[this.sqlTypeArray.length];

            this.resultClass = resultClass;
        }


        @Nullable
        final R readOneRow(final DataRow dataRow) {

            final JdbdStmtExecutor<?, ?> executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final SqlType[] sqlTypeArray = this.sqlTypeArray;
            final List<? extends Selection> selectionList = this.selectionList;

            final MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            final int columnCount;
            if (sqlTypeArray[0] == null) {
                columnCount = dataRow.getColumnCount();
                if (columnCount != sqlTypeArray.length) {
                    throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, sqlTypeArray.length);
                }
                for (int i = 0; i < columnCount; i++) {
                    sqlTypeArray[i] = executor.getColumnMeta(dataRow, i);
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


    }// RowReader

    private static final class SingleColumnRowReader<R> extends RowReader<R> {

        private R row;

        private SingleColumnRowReader(JdbdStmtExecutor<?, ?> executor, List<? extends Selection> selectionList,
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

    private static final class BeanReader<R> extends RowReader<R> {

        private final ObjectAccessor accessor;
        private final Constructor<R> constructor;


        private R row;

        private BeanReader(JdbdStmtExecutor<?, ?> executor, List<? extends Selection> selectionList,
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

        private ObjectRowReader(JdbdStmtExecutor<?, ?> executor, List<? extends Selection> selectionList,
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


        private SecondRowReader(JdbdStmtExecutor<?, ?> executor, TwoStmtQueryStmt stmt, List<R> rowList) {
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

    private static final class RecordRowReader<R> extends RowReader<R> implements CurrentRecord {

        private final Function<CurrentRecord, R> function;

        private final Object[] valueArray;

        private final Map<String, Integer> aliasToIndexMap;
        private int currentIndex;


        private RecordRowReader(JdbdStmtExecutor<?, ?> executor, List<? extends Selection> selectionList,
                                Function<CurrentRecord, R> function) {
            super(executor, selectionList, null);
            this.function = function;
            this.valueArray = new Object[selectionList.size()];

            if (this.valueArray.length < 6) {
                this.aliasToIndexMap = null;
            } else {
                this.aliasToIndexMap = createAliasToIndexMap(selectionList);
            }
        }

        @Override
        public int getColumnCount() {
            return this.valueArray.length;
        }

        @Override
        public String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return this.selectionList.get(indexBasedZero).label();
        }

        @Override
        public int getColumnIndex(final @Nullable String columnLabel) throws IllegalArgumentException {
            if (columnLabel == null) {
                throw new NullPointerException("columnLabel is null");
            }
            int index = -1;
            final Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
            if (aliasToIndexMap == null) {
                final List<? extends Selection> selectionList = this.selectionList;
                for (int i = valueArray.length - 1; i > -1; i--) {  // If alias duplication,then override.
                    if (columnLabel.equals(selectionList.get(i).label())) {
                        index = i;
                        break;
                    }
                }
            } else {
                index = aliasToIndexMap.getOrDefault(columnLabel, -1);
            }
            if (index < 0) {
                throw _Exceptions.unknownSelectionAlias(columnLabel);
            }
            return index;
        }

        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[checkIndex(indexBasedZero)];
        }

        @Override
        public Object getNonNull(final int indexBasedZero) {
            final Object value;
            value = this.valueArray[checkIndex(indexBasedZero)];
            if (value == null) {
                throw ExecutorSupport.currentRecordColumnIsNull(indexBasedZero, this.selectionList.get(indexBasedZero));
            }
            return value;
        }

        @Override
        public Object getOrDefault(final int indexBasedZero, @Nullable Object defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            Object value;
            value = this.valueArray[checkIndex(indexBasedZero)];
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public Object getOrSupplier(final int indexBasedZero, Supplier<?> supplier) {
            Object value;
            value = this.valueArray[checkIndex(indexBasedZero)];
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }

        @Override
        public <T> T get(int indexBasedZero, Class<T> columnClass) {
            //TODO
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getNonNull(final int indexBasedZero, Class<T> columnClass) {
            final T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, this.selectionList.get(indexBasedZero));
            }
            return value;
        }

        @Override
        public <T> T getOrDefault(final int indexBasedZero, Class<T> columnClass, @Nullable T defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public <T> T getOrSupplier(final int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) {
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }

        @Override
        public Object get(String selectionLabel) {
            return get(getColumnIndex(selectionLabel));
        }

        @Override
        public Object getNonNull(String selectionLabel) {
            return getNonNull(getColumnIndex(selectionLabel));
        }

        @Override
        public Object getOrDefault(String selectionLabel, Object defaultValue) {
            return getOrDefault(getColumnIndex(selectionLabel), defaultValue);
        }

        @Override
        public Object getOrSupplier(String selectionLabel, Supplier<?> supplier) {
            return getOrSupplier(getColumnIndex(selectionLabel), supplier);
        }

        @Override
        public <T> T get(String selectionLabel, Class<T> columnClass) {
            return get(getColumnIndex(selectionLabel), columnClass);
        }

        @Override
        public <T> T getNonNull(String selectionLabel, Class<T> columnClass) {
            return getNonNull(getColumnIndex(selectionLabel), columnClass);
        }

        @Override
        public <T> T getOrDefault(String selectionLabel, Class<T> columnClass, T defaultValue) {
            return getOrDefault(getColumnIndex(selectionLabel), columnClass, defaultValue);
        }

        @Override
        public <T> T getOrSupplier(String selectionLabel, Class<T> columnClass, Supplier<T> supplier) {
            return getOrSupplier(getColumnIndex(selectionLabel), columnClass, supplier);
        }

        /*-------------------below RowReader methods -------------------*/

        @Override
        ObjectAccessor createRow() {
            this.currentIndex = 0;
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            final int currentIndex = this.currentIndex++;
            assert indexBasedZero == currentIndex;
            this.valueArray[indexBasedZero] = value;
        }

        @Override
        R endOneRow() {
            assert this.currentIndex == this.valueArray.length;
            final R row;
            row = this.function.apply(this);
            if (row instanceof CurrentRecord) {
                throw _Exceptions.recordMapFuncReturnError(this.function);
            }
            return row;
        }


        private int checkIndex(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.valueArray.length) {
                String m = String.format("index[%s] not in [0,)", this.valueArray.length);
                throw new IllegalArgumentException(m);
            }
            return indexBasedZero;
        }


    }//RecordRowReader


}
