package io.army.sync;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.session.CurrentRecord;
import io.army.session.SessionException;
import io.army.session._ArmySession;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.sync.executor.SyncStmtExecutor;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalSession}</li>
 *     <li>{@link ArmySyncRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implements of {@link SyncSession}.
 *
 * @since 1.0
 */
abstract class ArmySyncSession extends _ArmySession implements SyncSession {

    private static final AtomicIntegerFieldUpdater<ArmySyncSession> SESSION_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmySyncSession.class, "sessionClosed");


    final SyncStmtExecutor stmtExecutor;

    private volatile int sessionClosed;

    protected ArmySyncSession(ArmySyncSessionFactory.SyncSessionBuilder<?, ?> builder) {
        super(builder);
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return onlyRow(this.query(statement, resultClass, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return onlyRow(this.query(statement, resultClass, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return onlyRow(this.queryObject(statement, constructor, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return onlyRow(this.queryObject(statement, constructor, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return onlyRow(this.queryRecord(statement, function, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return onlyRow(this.queryRecord(statement, function, _Collections::arrayList, option));
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return this.query(statement, resultClass, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.query(statement, resultClass, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.query(s, resultClass, listConstructor, option));
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.queryObject(statement, constructor, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObject(statement, constructor, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.queryObject(s, constructor, listConstructor, option));
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.queryRecord(statement, function, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return this.queryRecord(statement, function, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQuery(statement, option, s -> this.stmtExecutor.queryRecord(s, function, listConstructor, option));
    }

    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.queryStream(statement, resultClass, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryStream(s, resultClass, option));
    }

    @Override
    public final <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryObjectStream(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryObjectStream(s, constructor, option));
    }

    @Override
    public final <R> Stream<R> queryRecordStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecordStream(statement, function, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryRecordStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryRecordStream(s, function, option));
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return this.update(statement, defaultOption());
    }

    @Override
    public final long update(SimpleDmlStatement statement, SyncStmtOption option) {
        if (statement instanceof _BatchStatement) {
            throw _Exceptions.unexpectedStatement(statement);
        }
        try {
            assertSession(statement);
            final long rows;
            if (statement instanceof InsertStatement) {
                rows = this.executeInsert((InsertStatement) statement, option);
            } else {
                rows = this.executeUpdate(statement, option);
            }
            return rows;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }


    }


    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, defaultOption());
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option) {
        return this.batchUpdate(statement, _Collections::arrayList, option);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, defaultOption());
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        return null;
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass) {
        return null;
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return null;
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return null;
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return null;
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass) {
        return null;
    }

    @Override
    public final <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor) {
        return null;
    }

    @Override
    public final <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function) {
        return null;
    }

    @Override
    public final boolean isClosed() {
        return this.sessionClosed != 0;
    }

    @Override
    public final void close() throws SessionException {
        if (SESSION_CLOSED.compareAndSet(this, 0, 1)) {
            this.stmtExecutor.close();
        }
    }


    /*-------------------below private methods -------------------*/

    private <R> List<R> executeQuery(final SimpleDqlStatement statement, final SyncStmtOption option,
                                     final Function<SimpleStmt, List<R>> exeFunc) {
        return Collections.emptyList();
    }

    private <R> Stream<R> executeQueryStream(final SimpleDqlStatement statement, final SyncStmtOption option,
                                             final Function<SimpleStmt, Stream<R>> exeFunc) {
        throw new UnsupportedOperationException();
    }

    private <R> List<R> executeBatchQuery(final BatchDqlStatement statement, final SyncStmtOption option,
                                          final Function<BatchStmt, List<R>> exeFunc) {
        return Collections.emptyList();
    }

    private <R> Stream<R> executeBatchQueryStream(final BatchDqlStatement statement, final SyncStmtOption option,
                                                  final Function<BatchStmt, Stream<R>> exeFunc) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #update(SimpleDmlStatement, SyncStmtOption)
     */
    private long executeInsert(InsertStatement statement, SyncStmtOption option) {
        return 0;
    }

    private long executeUpdate(SimpleDmlStatement statement, SyncStmtOption option) {
        return 0;
    }


    @Nullable
    private static <R> R onlyRow(final List<R> resultList) {
        final R result;
        switch (resultList.size()) {
            case 1:
                result = resultList.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw _Exceptions.nonUnique(resultList);
        }
        return result;
    }


    private SyncStmtOption defaultOption() {
        throw new UnsupportedOperationException();
    }


}
