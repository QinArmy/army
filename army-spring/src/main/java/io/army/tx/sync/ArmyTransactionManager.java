package io.army.tx.sync;

import io.army.SessionException;
import io.army.sync.Session;
import io.army.sync.SessionFactory;
import io.army.tx.Transaction;
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

    private final SessionFactory sessionFactory;

    private final boolean supportSavePoints;

    private String beanName;


    public ArmyTransactionManager(SessionFactory sessionFactory) {
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


    /*################################## blow AbstractPlatformTransactionManager template method ##################################*/

    @Override
    protected final Object doGetTransaction() throws TransactionException {
        final ArmyTransactionObject txObject = new ArmyTransactionObject();
        final Session session;
        session = (Session) TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.setSession(session);
        }
        return txObject;
    }

    @Override
    protected final boolean isExistingTransaction(final Object transaction) throws TransactionException {
        final Session session = ((ArmyTransactionObject) transaction).session;
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
            final Session session;
            session = this.sessionFactory.builder()
                    .name(txName)
                    .build();

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
                    .start();

            //5. bind session
            TransactionSynchronizationManager.bindResource(this.sessionFactory, txObject.setSession(session));

        } catch (io.army.session.DataAccessException e) {
            throw new CannotCreateTransactionException("Could not open Army transaction", e);
        }
    }

    @Override
    protected final void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final Session session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        try {
            final Transaction tx;
            tx = session.sessionTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Committing Army transaction on %s", session));
            }
            if (!tx.readOnly()) {
                // commit transaction
                session.flush();
                tx.commit();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        } catch (io.army.SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }

    @Override
    protected final void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final Session session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }
        try {
            final Transaction tx;
            tx = session.sessionTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Rolling Army transaction on %s", session));
            }
            if (!tx.readOnly()) {
                //rollback transaction and clear changed cache.
                tx.rollback();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        }
    }

    @Override
    protected final void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final Session session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        try {
            final Transaction tx;
            tx = session.sessionTransaction();
            if (status.isDebug()) {
                logger.debug(String.format("Setting Army transaction on %s rollback-only", session));
            }
            if (!tx.readOnly()) {
                tx.markRollbackOnly();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        }
    }

    @Override
    protected final void doResume(final @Nullable Object transaction, final Object suspendedResources)
            throws TransactionException {

        final SessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        final Session session = (Session) suspendedResources;
        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected final void doCleanupAfterCompletion(final Object transaction) {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        final Session session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        final SessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        try {
            session.close();
        } catch (io.army.SessionException e) {
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

        private Session session;

        private ArmyTransactionObject() {
        }


        private Session setSession(final Session session) {
            if (this.session != null) {
                throw new IllegalStateException("session non-null.");
            }
            this.session = session;
            return session;
        }

        private Session suspend() {
            final Session session = this.session;
            if (session == null) {
                throw new IllegalStateException("no session , couldn't suspend.");
            }
            this.session = null;
            return session;
        }


        /*################################## blow SavepointManager method ##################################*/

        @Override
        public Object createSavepoint() throws TransactionException {
            final Session session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                return session.sessionTransaction().createSavePoint();
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.convertTransactionException(e);
            }
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            final Session session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.sessionTransaction().rollbackToSavePoint(savepoint);
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.convertTransactionException(e);
            }
        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {
            final Session session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.sessionTransaction().releaseSavePoint(savepoint);
            } catch (io.army.tx.TransactionException e) {
                throw SpringUtils.convertTransactionException(e);
            }
        }


        @Override
        public boolean isRollbackOnly() {
            final Session session = this.session;
            return session != null
                    && session.hasTransaction()
                    && session.sessionTransaction().rollbackOnly();
        }

        @Override
        public void flush() {
            final Session session = this.session;
            if (session != null) {
                try {
                    session.flush();
                } catch (SessionException e) {
                    throw SpringUtils.convertSessionException(e);
                }
            }
        }

    }//ArmyTransactionObject


}
