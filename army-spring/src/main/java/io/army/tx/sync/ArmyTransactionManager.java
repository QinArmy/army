package io.army.tx.sync;

import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.SessionException;
import io.army.sync.*;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
public class ArmyTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean
        , BeanNameAware {

    private final LocalSessionFactory sessionFactory;

    private final boolean supportSavePoints;

    private String beanName;

    private boolean wrapSession = true;


    public ArmyTransactionManager(LocalSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
        this.supportSavePoints = sessionFactory.supportSavePoints();
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() {
        // register transaction manager for read-write splitting
        //TransactionDefinitionHolder.registerTransactionManager(this.beanName, this.useSavepointForNestedTransaction());
    }


    public final void setWrapSession(boolean wrapSession) {
        this.wrapSession = wrapSession;
    }

    public final boolean isWrapSession() {
        return this.wrapSession;
    }

    /*################################## blow AbstractPlatformTransactionManager template method ##################################*/

    @Override
    protected final Object doGetTransaction() throws TransactionException {
        final ArmyTransactionObject txObject = new ArmyTransactionObject();
        final LocalSession session;
        session = (LocalSession) TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.setSession(session);
        }
        return txObject;
    }

    @Override
    protected final boolean isExistingTransaction(final Object transaction) throws TransactionException {
        final LocalSession session = ((ArmyTransactionObject) transaction).session;
        return session != null && session.hasTransaction();
    }

    @Override
    protected final Object doSuspend(final Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        if (txObject.session == null) {
            throw transactionNoSession();
        }
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
        return txObject.suspend();
    }

    @Override
    protected final void doBegin(final Object transaction, final TransactionDefinition definition)
            throws TransactionException {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;

        try {
            //1.get transaction name
            final String txName;
            txName = definition.getName();

            //2. create session
            final LocalSession session;
            session = this.sessionFactory.builder()
                    .name(txName)
                    .build();
            // bind to txObject
            txObject.setSession(session);

            //3. get timeout seconds
            int timeoutSeconds;
            timeoutSeconds = definition.getTimeout();
            if (timeoutSeconds == TransactionDefinition.TIMEOUT_DEFAULT) {
                timeoutSeconds = getDefaultTimeout();
            }

            //4. create and start transaction
            session.builder()
                    .isolation(SpringUtils.toArmyIsolation(definition.getIsolationLevel()))
                    .readonly(definition.isReadOnly())
                    .timeout(timeoutSeconds)
                    .build()
                    .start();//start transaction

            //5. bind current session
            final SyncSession currentSession;
            if (this.wrapSession) {
                currentSession = new CurrentSession(session);
            } else {
                currentSession = session;
            }
            TransactionSynchronizationManager.bindResource(this.sessionFactory, currentSession);

        } catch (io.army.tx.CannotCreateTransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (io.army.session.DataAccessException e) {
            throw new CannotCreateTransactionException("Could not open Army transaction", e);
        }
    }

    @Override
    protected final void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final LocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        try {
            final LocalTransaction tx;
            tx = session.currentTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Committing Army transaction on %s", session));
            }
            if (!tx.readOnly()) {
                session.flush();
            }
            tx.commit();
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }

    @Override
    protected final void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final LocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }
        try {
            final LocalTransaction tx;
            tx = session.currentTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Rolling Army transaction on %s", session));
            }
            tx.rollback();
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }

    @Override
    protected final void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final LocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        try {
            final LocalTransaction tx;
            tx = session.currentTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Setting Army transaction on %s rollback-only", session));
            }
            if (!tx.readOnly()) {
                tx.markRollbackOnly();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }

    @Override
    protected final void doResume(final @Nullable Object transaction, final Object suspendedResources)
            throws TransactionException {

        final LocalSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        final LocalSession session = (LocalSession) suspendedResources;
        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected final void doCleanupAfterCompletion(final Object transaction) {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        final LocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        final LocalSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        try {
            session.close();
        } catch (SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }


    @Override
    protected final boolean useSavepointForNestedTransaction() {
        return this.supportSavePoints;
    }




    /*################################## blow setter method ##################################*/


    private static TransactionUsageException transactionNoSession() {
        return new TransactionUsageException("current transaction no session.");
    }

    /*################################## blow static inner class ##################################*/

    private static final class ArmyTransactionObject implements SavepointManager, SmartTransactionObject {

        private LocalSession session;

        private ArmyTransactionObject() {
        }


        private void setSession(final LocalSession session) {
            if (this.session != null) {
                throw new IllegalStateException("session non-null.");
            }
            this.session = session;
        }

        private LocalSession suspend() {
            final LocalSession session = this.session;
            if (session == null) {
                throw new IllegalStateException("no session , couldn't suspend.");
            }
            this.session = null;
            return session;
        }


        /*################################## blow SavepointManager method ##################################*/

        @Override
        public Object createSavepoint() throws TransactionException {
            final LocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                return session.currentTransaction().createSavePoint();
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            final LocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.currentTransaction().rollbackToSavePoint(savepoint);
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {
            final LocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.currentTransaction().releaseSavePoint(savepoint);
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }


        @Override
        public boolean isRollbackOnly() {
            final LocalSession session = this.session;
            return session != null
                    && session.hasTransaction()
                    && session.currentTransaction().rollbackOnly();
        }

        @Override
        public void flush() {
            final LocalSession session = this.session;
            if (session != null) {
                try {
                    session.flush();
                } catch (SessionException e) {
                    throw SpringUtils.convertSessionException(e);
                }
            }
        }

    }//ArmyTransactionObject


    private static final class CurrentSession extends _AbstractSyncSession {

        private final LocalSession session;


        private CurrentSession(LocalSession session) {
            this.session = session;
        }


        @Override
        public LocalSessionFactory sessionFactory() {
            return this.session.sessionFactory();
        }

        @Override
        public String name() {
            return this.session.name();
        }

        @Override
        public boolean isReadonlySession() {
            return this.session.isReadonlySession();
        }

        @Override
        public boolean isReadOnlyStatus() {
            return this.session.isReadOnlyStatus();
        }

        @Override
        public boolean closed() {
            return this.session.closed();
        }

        @Override
        public boolean hasTransaction() {
            return this.session.hasTransaction();
        }

        @Override
        public void flush() throws SessionException {
            this.session.flush();
        }

        @Override
        public <T> TableMeta<T> tableMeta(Class<T> domainClass) {
            return this.session.tableMeta(domainClass);
        }


        @Override
        public <R> R get(TableMeta<R> table, Object id, Visible visible) {
            return this.session.get(table, id, visible);
        }

        @Override
        public <R> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value
                , Visible visible) {
            return this.session.getByUnique(table, field, value, visible);
        }

        @Override
        public <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
            return this.session.query(statement, resultClass, listConstructor, visible);
        }

        @Override
        public List<Map<String, Object>> queryAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor
                , Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
            return this.session.queryAsMap(statement, mapConstructor, listConstructor, visible);
        }

        @Override
        public <T> void save(T domain, boolean preferLiteral, NullHandleMode mode, Visible visible) {
            this.session.save(domain, preferLiteral, mode, visible);
        }

        @Override
        public long update(DmlStatement dml, Visible visible) {
            return this.session.update(dml, visible);
        }

        @Override
        public <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Supplier<List<R>> listConstructor
                , Visible visible) {
            return this.session.returningUpdate(dml, resultClass, listConstructor, visible);
        }

        @Override
        public List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor
                , Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
            return this.session.returningUpdateAsMap(dml, mapConstructor, listConstructor, visible);
        }

        @Override
        public <T> void batchSave(List<T> domainList, boolean preferLiteral, NullHandleMode mode, Visible visible) {
            this.session.batchSave(domainList, preferLiteral, mode, visible);
        }

        @Override
        public List<Long> batchUpdate(NarrowDmlStatement dml, Visible visible) {
            return this.session.batchUpdate(dml, visible);
        }

        @Override
        public MultiResult multiStmt(List<Statement> statementList, Visible visible) {
            return this.session.multiStmt(statementList, visible);
        }

        @Override
        public MultiResult call(CallableStatement callable) {
            return this.session.call(callable);
        }

        @Override
        public String toString() {
            return String.format("Wrapper[%s] of %s", System.identityHashCode(this), this.session);
        }


    }// CurrentSession


}
