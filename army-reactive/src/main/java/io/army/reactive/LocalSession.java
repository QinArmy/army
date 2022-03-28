package io.army.reactive;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.CannotCreateTransactionException;
import io.army.session.SessionException;
import io.army.tx.Isolation;
import io.army.tx.TransactionOptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * This class is a implementation of {@link Session}.
 */
final class LocalSession extends _AbstractReactiveSession implements Session {


    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    private final boolean readonly;

    private final AtomicReference<Transaction> sessionTransaction = new AtomicReference<>(null);

    LocalSession(LocalSessionFactory.LocalSessionBuilder builder) {
        this.readonly = builder.readOnly;
    }


    @Override
    public boolean isReadonlySession() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public boolean hasTransaction() {
        return false;
    }

    @Override
    public boolean isReadOnlyStatus() {
        return false;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return null;
    }

    @Override
    public SessionFactory sessionFactory() {
        return null;
    }

    @Override
    public <R extends IDomain> Mono<R> get(TableMeta<R> table, Object id) {
        return null;
    }

    @Override
    public <R extends IDomain> Mono<R> get(TableMeta<R> table, Object id, Visible visible) {
        return null;
    }

    @Override
    public <R extends IDomain> Mono<R> getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value) {
        return null;
    }

    @Override
    public <R extends IDomain> Mono<R> getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value, Visible visible) {
        return null;
    }

    @Override
    public <R> Flux<R> select(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public <R> Flux<Optional<R>> selectNullable(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public Flux<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor, Visible visible) {
        return null;
    }

    @Override
    public <T extends IDomain> Mono<Void> save(T domain, NullHandleMode mode, Visible visible) {
        return null;
    }

    @Override
    public Mono<Long> update(DmlStatement dml, Visible visible) {
        return null;
    }

    @Override
    public <R> Flux<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor, Visible visible) {
        return null;
    }

    @Override
    public <R> Flux<Optional<R>> returningNullableUpdate(DmlStatement dml, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public <T extends IDomain> Mono<Void> batchSave(List<T> domainList, NullHandleMode mode, Visible visible) {
        return null;
    }

    @Override
    public Flux<Long> batchUpdate(NarrowDmlStatement dml, Visible visible) {
        return null;
    }

    @Override
    public MultiResult multiStmt(List<Statement> statementList, Visible visible) {
        return null;
    }

    @Override
    public MultiResult call(CallableStatement callable) {
        return null;
    }

    @Override
    public Mono<Void> flush() throws SessionException {
        return null;
    }

    @Override
    public Mono<Void> close() throws SessionException {
        return null;
    }

    @Override
    public TransactionBuilder builder() throws SessionException {
        return null;
    }

    /*################################## blow private instance inner class ##################################*/

    static final class LocalTransactionBuilder extends TransactionOptions implements Session.TransactionBuilder {

        final LocalSession session;

        boolean readOnly;

        Isolation isolation;

        int timeout = -1;

        String name;

        private LocalTransactionBuilder(LocalSession session) {
            this.session = session;
        }

        @Override
        public Session.TransactionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public Session.TransactionBuilder isolation(Isolation isolation) {
            this.isolation = isolation;
            return this;
        }

        @Override
        public Session.TransactionBuilder timeout(int seconds) {
            this.timeout = seconds;
            return this;
        }

        @Override
        public Session.TransactionBuilder name(@Nullable String txName) {
            this.name = txName;
            return this;
        }

        @Override
        public Transaction build() throws CannotCreateTransactionException {
            if (this.isolation == null) {
                throw new CannotCreateTransactionException("not specified isolation.");
            }

            throw new UnsupportedOperationException();
        }
    }

}
