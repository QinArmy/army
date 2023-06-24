package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.proxy._SessionCache;
import io.army.session.*;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.*;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class SyncLocalSession extends _ArmySyncSession implements LocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLocalSession.class);

    final SyncLocalSessionFactory factory;

    final StmtExecutor stmtExecutor;

    private final _SessionCache sessionCache;

    private SyncLocalTransaction transaction;


    private boolean closed;

    SyncLocalSession(final SyncLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        this.factory = builder.factory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
        this.sessionCache = this.factory.sessionCacheFactory.createCache();
    }


    @Override
    public LocalSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.factory.getTable(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        return table;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final LocalTransaction tx = this.transaction;
        return this.readonly || (tx != null && tx.readOnly());
    }


    @Override
    public <R> List<R> query(final SimpleDqlStatement statement, final Class<R> resultClass,
                             final Supplier<List<R>> listConstructor, final Visible visible) {

        //1.assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {

            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, false, visible);

            final List<R> resultList;
            final int timeout = this.getTxTimeout();
            if (stmt instanceof SimpleStmt) {
                resultList = this.stmtExecutor.query((SimpleStmt) stmt, timeout, resultClass, listConstructor);
            } else if (stmt instanceof PairStmt) {
                final PairStmt pairStmt = (PairStmt) stmt;
                SimpleStmt simpleStmt;
                simpleStmt = pairStmt.firstStmt();
                resultList = this.stmtExecutor.query(simpleStmt, timeout, resultClass, listConstructor);

            } else {
                throw _Exceptions.unexpectedStmt(stmt);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public List<Map<String, Object>> queryMap(final SimpleDqlStatement statement,
                                              final Supplier<Map<String, Object>> mapConstructor,
                                              final Supplier<List<Map<String, Object>>> listConstructor,
                                              final Visible visible) {
        //1.assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {

            final SimpleStmt stmt;
            stmt = (SimpleStmt) this.parseDqlStatement(statement, false, visible);
            this.printSqlIfNeed(stmt);

            final List<Map<String, Object>> resultList;
            resultList = this.stmtExecutor.queryAsMap(stmt, this.getTxTimeout(), mapConstructor, listConstructor);

            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public long update(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchStatement) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        final long affectedRows;
        if (statement instanceof InsertStatement) {
            affectedRows = this.insert((InsertStatement) statement, visible);
        } else {
            affectedRows = this.simpleUpdate(statement, visible);
        }
        return affectedRows;
    }


    @Override
    public List<Long> batchUpdate(final BatchDmlStatement statement, final IntFunction<List<Long>> listConstructor,
                                  final boolean useMultiStmt, final Visible visible) {
        if (!(statement instanceof _BatchStatement)) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        //1. assert session status
        assertSession(true, visible);
        try {
            //2. parse statement to stmt
            final Stmt stmt;
            if (statement instanceof UpdateStatement) {
                stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, visible);
            } else if (statement instanceof DeleteStatement) {
                stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, visible);
            } else {
                throw _Exceptions.unexpectedStatement(statement);
            }
            this.printSqlIfNeed(stmt);

            //3. execute stmt
            final int timeout;
            timeout = this.getTxTimeout();
            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);
            final List<Long> resultList;
            if (useMultiStmt) {
                resultList = this.stmtExecutor.multiStmtBatchUpdate((BatchStmt) stmt, timeout, listConstructor,
                        domainTable
                );
            } else if (!(stmt instanceof PairBatchStmt)) {
                resultList = this.stmtExecutor.batchUpdate((BatchStmt) stmt, timeout, listConstructor, domainTable, null);
            } else if (!this.hasTransaction()) {
                throw updateChildNoTransaction();
            } else {
                final long startTime;
                startTime = System.currentTimeMillis();
                // firstStmt update child, because army update child and update parent
                resultList = this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).firstStmt(), timeout,
                        listConstructor, domainTable, null
                );
                assert domainTable instanceof ChildTableMeta; // fail, bug.

                // secondStmt update parent, because army update child and update parent
                this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).secondStmt(),
                        restSecond((ChildTableMeta<?>) domainTable, startTime, timeout), listConstructor, domainTable,
                        resultList
                );
            }

            return resultList;
        } catch (ChildUpdateException e) {
            final SyncLocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public <R> List<R> batchQuery(final BatchDqlStatement statement, final Class<R> resultClass,
                                  final @Nullable R terminator, final Supplier<List<R>> listConstructor,
                                  final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, useMultiStmt, visible);
            final List<R> resultList;
            if (useMultiStmt) {
                resultList = this.stmtExecutor.multiStmtBatchQuery((BatchStmt) stmt, this.getTxTimeout(), resultClass,
                        terminator, listConstructor);
            } else if (stmt instanceof BatchStmt) {
                resultList = this.stmtExecutor.batchQuery((BatchStmt) stmt, this.getTxTimeout(), resultClass,
                        terminator, listConstructor);
            } else {//TODO firebird
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @Override
    public List<Map<String, Object>> batchQueryAsMap(final BatchDqlStatement statement,
                                                     final Supplier<Map<String, Object>> mapConstructor,
                                                     final @Nullable Map<String, Object> terminator,
                                                     final Supplier<List<Map<String, Object>>> listConstructor,
                                                     final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, useMultiStmt, visible);
            final List<Map<String, Object>> resultList;
            if (useMultiStmt) {
                resultList = this.stmtExecutor.multiStmtBatchQueryAsMap((BatchStmt) stmt, this.getTxTimeout(),
                        mapConstructor, terminator, listConstructor);
            } else if (stmt instanceof BatchStmt) {
                resultList = this.stmtExecutor.batchQueryAsMap((BatchStmt) stmt, this.getTxTimeout(), mapConstructor,
                        terminator, listConstructor);
            } else {//TODO firebird
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @Override
    public <R> Stream<R> batchQueryStream(final BatchDqlStatement statement, final Class<R> resultClass,
                                          final @Nullable R terminator, final StreamOptions options,
                                          final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, useMultiStmt, visible);
            final Stream<R> stream;
            if (useMultiStmt) {
                stream = this.stmtExecutor.multiStmtBatchQueryStream((BatchStmt) stmt, this.getTxTimeout(), resultClass,
                        terminator, options);
            } else if (stmt instanceof BatchStmt) {
                stream = this.stmtExecutor.batchQueryStream((BatchStmt) stmt, this.getTxTimeout(), resultClass,
                        terminator, options);
            } else {
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(stream);
            }
            return stream;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @Override
    public Stream<Map<String, Object>> batchQueryMapStream(final BatchDqlStatement statement,
                                                           final Supplier<Map<String, Object>> mapConstructor,
                                                           final @Nullable Map<String, Object> terminator,
                                                           final StreamOptions options, final boolean useMultiStmt,
                                                           final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, useMultiStmt, visible);
            final Stream<Map<String, Object>> stream;
            if (useMultiStmt) {
                stream = this.stmtExecutor.multiStmtBatchQueryMapStream((BatchStmt) stmt, this.getTxTimeout(),
                        mapConstructor, terminator, options);
            } else if (stmt instanceof BatchStmt) {
                stream = this.stmtExecutor.batchQueryMapStream((BatchStmt) stmt, this.getTxTimeout(), mapConstructor,
                        terminator, options);
            } else {
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(stream);
            }
            return stream;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @Override
    public <R> Stream<R> queryStream(final SimpleDqlStatement statement, final Class<R> resultClass,
                                     final StreamOptions options, final Visible visible) {
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, false, visible);
            if (!(stmt instanceof SimpleStmt)) {
                throw streamApiDontSupportTowStatement();
            }
            return this.stmtExecutor.queryStream((SimpleStmt) stmt, this.getTxTimeout(), resultClass, options);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", SimpleDqlStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public Stream<Map<String, Object>> queryMapStream(final SimpleDqlStatement statement,
                                                      final Supplier<Map<String, Object>> mapConstructor,
                                                      final StreamOptions options, final Visible visible) {
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, false, visible);
            if (!(stmt instanceof SimpleStmt)) {
                throw streamApiDontSupportTowStatement();
            }
            return this.stmtExecutor.queryMapStream((SimpleStmt) stmt, this.getTxTimeout(), mapConstructor, options);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", SimpleDqlStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }

    }

    @Override
    public MultiResult multiStmt(final MultiResultStatement statement, final @Nullable StreamOptions options,
                                 final Visible visible) {
        return null;
    }

    @Override
    public MultiStream multiStmtStream(final MultiResultStatement statement, final @Nullable StreamOptions options,
                                       final Visible visible) {
        return null;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() throws SessionException {
        if (this.closed) {
            return;
        }
        try {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                String m = String.format("%s %s not end.", this, tx);
                throw new TransactionNotCloseException(m);
            }
            this.stmtExecutor.close();
            this.sessionCache.clearOnSessionCLose();
            this.closed = true;
        } catch (SessionException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SessionCloseFailureException(e, "session close connection error.");
        }
    }

    @Override
    public LocalTransaction currentTransaction() throws NoSessionTransactionException {
        final LocalTransaction transaction = this.transaction;
        if (transaction == null) {
            throw new NoSessionTransactionException("no session transaction.");
        }
        return transaction;
    }

    @Override
    public TransactionBuilder builder() {
        if (this.transaction != null) {
            throw duplicationTransaction(this);
        }
        return new LocalTransactionBuilder(this);
    }

    @Override
    public boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    protected String transactionName() {
        final SyncLocalTransaction tx = this.transaction;
        return tx == null ? null : tx.name();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /*################################## blow package method ##################################*/

    void clearChangedCache(final SyncLocalTransaction transaction) {
        if (this.transaction != transaction) {
            String m = String.format("%s and %s not match.", transaction, this.transaction);
            throw new IllegalArgumentException(m);
        }
        this.sessionCache.clearChangedOnRollback();
    }

    void endTransaction(final SyncLocalTransaction transaction) {
        if (this.transaction != transaction) {
            throw new IllegalArgumentException("transaction not match.");
        }
        final TransactionStatus status = transaction.status();
        switch (status) {
            case ROLLED_BACK:
            case COMMITTED:
            case FAILED_ROLLBACK:
                this.transaction = null;
                break;
            default:
                throw new IllegalArgumentException(String.format("transaction status[%s] error.", status));
        }

    }


    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long insert(final InsertStatement statement, final Visible visible) {
        //1. assert session status
        assertSession(true, visible);
        assertSessionForChildInsert(statement);

        try {

            //2. parse statement to stmt
            if (statement instanceof _Insert._QueryInsert && !this.allowQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
            final SyncLocalSessionFactory factory = this.factory;
            final Stmt stmt;
            stmt = factory.dialectParser.insert(statement, visible);

            this.printSqlIfNeed(stmt);

            //3. execute stmt

            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.insert((SimpleStmt) stmt, this.getTxTimeout());
            } else {
                affectedRows = this.insertPairStmt((ChildTableMeta<?>) ((_Insert) statement).table(),
                        (PairStmt) stmt, this.getTxTimeout());
            }

            //4. validate value insert affected rows
            if (!(statement instanceof _Insert._QueryInsert)
                    && affectedRows != ((_Insert) statement).insertRowCount()) {
                String m = String.format("value list size is %s,but affected %s rows."
                        , ((_Insert._DomainInsert) statement).domainList().size(), affectedRows);
                throw new DataAccessException(m);
            } else if (affectedRows < 1) {
                String m = String.format("%s insert rows is %s .", ((_Insert) statement).table(), affectedRows);
                throw new DataAccessException(m);
            }
            return affectedRows;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", InsertStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #insert(InsertStatement, Visible)
     */
    private long insertPairStmt(final ChildTableMeta<?> domainTable, final PairStmt stmt, final int timeout) {
        final long startTime = System.currentTimeMillis();

        final long insertRows;
        insertRows = this.stmtExecutor.insert(stmt.firstStmt(), timeout);

        final int restSeconds;
        restSeconds = _ArmySession.restSecond(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.insert(stmt.secondStmt(), restSeconds);

            if (childRows != insertRows) {
                throw _Exceptions.parentChildRowsNotMatch(domainTable, insertRows, childRows);
            }
            return insertRows;
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Exception e) {
            throw new ChildUpdateException("Parent insert completion,but child insert occur error.", e);
        }

    }

    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long simpleUpdate(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchStatement) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        final LocalTransaction tx = this.transaction;
        try {
            //1. assert session status
            assertSession(true, visible);
            //2. parse statement to stmt
            final Stmt stmt;
            if (statement instanceof Update) {
                stmt = this.factory.dialectParser.update((Update) statement, false, visible);
            } else if (statement instanceof Delete) {
                stmt = this.factory.dialectParser.delete((Delete) statement, false, visible);
            } else {
                stmt = this.factory.dialectParser.dialectDml(statement, visible);
            }

            this.printSqlIfNeed(stmt);
            //3. execute stmt
            final int timeout = tx == null ? 0 : tx.nextTimeout();
            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.update((SimpleStmt) stmt, timeout);
            } else {
                affectedRows = this.updatePairStmt((ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table(),
                        (PairStmt) stmt, timeout);
            }

            //4. assert optimistic lock
            if (affectedRows < 1 && stmt.hasOptimistic()) {
                throw _Exceptions.optimisticLock(affectedRows);
            }
            return affectedRows;
        } catch (ChildUpdateException e) {
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #simpleUpdate(SimpleDmlStatement, Visible)
     */
    private long updatePairStmt(final ChildTableMeta<?> domainTable, final PairStmt stmt, final int timeout) {
        final long startTime;
        startTime = System.currentTimeMillis();

        final long affectedRows;
        affectedRows = this.stmtExecutor.update(stmt.firstStmt(), timeout);

        final int restSeconds;
        restSeconds = _ArmySession.restSecond(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.update(stmt.secondStmt(), restSeconds);

            if (childRows != affectedRows) {
                throw _Exceptions.parentChildRowsNotMatch(domainTable, affectedRows, childRows);
            }
            return affectedRows;
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Exception e) {
            String m = String.format("%s parent update completion,but child insert occur error.", domainTable);
            throw new ChildUpdateException(m, e);
        }

    }


    /**
     * @see #query(SimpleDqlStatement, Class, Supplier, Visible)
     * @see #queryMap(SimpleDqlStatement, Supplier, Supplier, Visible)
     * @see #queryStream(SimpleDqlStatement, Class, StreamOptions, Visible)
     * @see #queryMapStream(SimpleDqlStatement, Supplier, StreamOptions, Visible)
     */
    private Stmt parseDqlStatement(final DqlStatement statement, final boolean useMultiStmt,
                                   final Visible visible) {
        final Stmt stmt;
        if (statement instanceof SelectStatement) {
            stmt = this.factory.dialectParser.select((SelectStatement) statement, useMultiStmt, visible);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, visible);
        } else if (statement instanceof InsertStatement) {
            stmt = this.factory.dialectParser.insert((InsertStatement) statement, visible);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("current api don't support child dml statement.");
        } else if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, visible);
        } else {
            stmt = this.factory.dialectParser.dialectDml((DmlStatement) statement, visible);
        }
        this.printSqlIfNeed(stmt);
        return stmt;
    }


    private void assertSession(final boolean dmlStmt, final Visible visible) throws SessionException {
        if (this.closed) {
            throw _Exceptions.sessionClosed(this);
        }

        if (dmlStmt) {
            if (this.readonly) {
                throw _Exceptions.readOnlySession(this);
            }
            final LocalTransaction tx = this.transaction;
            if (tx != null && tx.readOnly()) {
                throw _Exceptions.readOnlyTransaction(this);
            }
        }

        if (!this.visible.isSupport(visible)) {
            throw _Exceptions.dontSupportNonVisible(this, visible);
        }

    }

    private int getTxTimeout() {
        final LocalTransaction tx = this.transaction;
        return tx == null ? 0 : tx.nextTimeout();
    }

    /**
     * @see #insert(InsertStatement, Visible)
     */
    private void assertSessionForChildInsert(final InsertStatement statement) {
        final TableMeta<?> domainTable;
        domainTable = ((_Insert) statement).table();

        if (domainTable instanceof ChildTableMeta && this.transaction == null) {
            throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) domainTable);
        }
    }


    private static DuplicationSessionTransaction duplicationTransaction(SyncLocalSession session) {
        String m = String.format("%s duplication transaction.", session);
        throw new DuplicationSessionTransaction(m);
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/



    /*################################## blow static inner class ##################################*/


    static final class LocalTransactionBuilder extends TransactionOptions implements LocalSession.TransactionBuilder {

        final SyncLocalSession session;


        private LocalTransactionBuilder(SyncLocalSession session) {
            this.session = session;
        }

        @Override
        public TransactionBuilder name(@Nullable String txName) {
            this.name = txName;
            return this;
        }

        @Override
        public TransactionBuilder isolation(Isolation isolation) {
            this.isolation = isolation;
            return this;
        }

        @Override
        public TransactionBuilder readonly(boolean readOnly) {
            this.readonly = readOnly;
            return this;
        }

        @Override
        public TransactionBuilder timeout(int timeoutSeconds) {
            this.timeout = timeoutSeconds;
            return this;
        }

        @Override
        public LocalTransaction build() throws SessionException {
            if (this.isolation == null) {
                String m = String.format("No specified %s,couldn't create transaction.", Isolation.class.getName());
                throw new CannotCreateTransactionException(m);
            }
            final SyncLocalSession session = this.session;
            if (!this.readonly && session.readonly) {
                String m = String.format("Session[%s] is readonly,couldn't create non-readonly transaction."
                        , this.session.name);
                throw new CannotCreateTransactionException(m);
            }
            if (session.transaction != null) {
                throw duplicationTransaction(session);
            }
            final SyncLocalTransaction transaction;
            transaction = new SyncLocalTransaction(this);
            session.transaction = transaction;
            return transaction;
        }

    }//LocalTransactionBuilder


}
