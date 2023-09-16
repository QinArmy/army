package io.army.tx.sync;

import io.army.session.SessionException;
import io.army.sync.LocalSession;
import io.army.sync.LocalSessionFactory;
import io.army.sync.LocalTransaction;
import io.army.sync.SyncSession;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

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
        this.supportSavePoints = sessionFactory.isSupportSavePoints();
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
        return session != null && session.inTransaction();
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
                throw new UnsupportedOperationException();
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
                    && session.inTransaction()
                    && session.currentTransaction().rollbackOnly();
        }

        @Override
        public void flush() {
//            final LocalSession session = this.session;
//            if (session != null) {
//
//            }
        }

    }//ArmyTransactionObject




}
