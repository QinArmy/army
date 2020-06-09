package io.army.tx.sync;

import io.army.DataAccessException;
import io.army.Session;
import io.army.SessionException;
import io.army.SessionFactory;
import io.army.context.spi.SpringCurrentSessionContext;
import io.army.tx.Transaction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class ArmyTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(sessionFactory.currentSessionContextIsInstanceOf(SpringCurrentSessionContext.class)
                , String.format("CurrentSessionContext of SessionFactory isn't instance of %s"
                        , SpringCurrentSessionContext.class.getName()));


    }


    /*################################## blow AbstractPlatformTransactionManager template method ##################################*/

    @Override
    protected Object doGetTransaction() throws TransactionException {
        ArmyTransactionObject txObject = new ArmyTransactionObject();
        Session session = obtainCurrentSession(null);
        if (session != null) {
            txObject.reset(session);
        }
        return txObject;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;

        try {
            final SessionFactory sessionFactory = obtainSessionFactory();
            txObject.reset(createNewSession(sessionFactory));

            final Transaction tx = txObject.session.builder(
                    definition.isReadOnly(),
                    SpringTxUtils.convertTotArmyIsolation(definition.getIsolationLevel()),
                    determineTimeout(definition)
            )
                    .name(definition.getName())
                    .build();
            // start transaction by JDBC
            tx.start();
            TransactionSynchronizationManager.bindResource(sessionFactory, txObject.session);
        } catch (Throwable e) {
            throw new CannotCreateTransactionException("Could not open Army transaction", e);
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }

        try (Transaction tx = txObject.session.sessionTransaction()) {
            if (status.isDebug()) {
                logger.debug("Committing Army transaction on Session [" + txObject.session + "]");
            }
            if (!tx.readOnly()) {
                tx.commit();
                txObject.flush();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        } catch (Throwable e) {
            throw new TransactionUsageException(e.getMessage(), e);
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        try (Transaction tx = txObject.session.sessionTransaction()) {
            if (status.isDebug()) {
                logger.debug("Rolling Army transaction on Session [" + txObject.session + "]");
            }
            if (!tx.readOnly()) {
                tx.rollback();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        } catch (Throwable e) {
            throw new TransactionUsageException(e.getMessage(), e);
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        if (status.isDebug()) {
            logger.debug("Setting Army transaction on Session [" + txObject.session + "] rollback-only");
        }
        try {
            Transaction tx = txObject.session.sessionTransaction();
            if (!tx.readOnly()) {
                tx.markRollbackOnly();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        Assert.state(txObject.session != null, "No Army session.");

        SessionFactory sessionFactory = obtainSessionFactory();
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        try {
            Session session = txObject.session;
            if (session.hasTransaction()) {
                Transaction tx = session.sessionTransaction();
                if (tx.readOnly()) {
                    tx.close();
                }
            }
            session.close();
        } catch (DataAccessException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        TransactionSynchronizationManager.unbindResource(obtainSessionFactory());
        return txObject.suspend();
    }

    @Override
    protected void doResume(@Nullable Object transaction, Object suspendedResources) throws TransactionException {

        SessionFactory sessionFactory = obtainSessionFactory();
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        Session session = (Session) suspendedResources;
        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        return txObject.session != null && txObject.session.hasTransaction();
    }

    /*################################## blow custom method ##################################*/

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Obtain the SessionFactory for actual use.
     *
     * @return the SessionFactory (never {@code null})
     * @throws IllegalStateException in case of no SessionFactory set
     * @since 1.0
     */
    protected final SessionFactory obtainSessionFactory() {
        Assert.state(this.sessionFactory != null, "No SessionFactory set");
        return sessionFactory;
    }


    protected final Session obtainSession() {
        SessionFactory sessionFactory = obtainSessionFactory();
        Session session = obtainCurrentSession(sessionFactory);
        if (session == null) {
            session = createNewSession(sessionFactory);
        }
        return session;
    }

    @Nullable
    protected final Session obtainCurrentSession(@Nullable SessionFactory sessionFactory) {
        SessionFactory factory = sessionFactory;
        if (factory == null) {
            factory = obtainSessionFactory();
        }
        return (Session) TransactionSynchronizationManager.getResource(factory);
    }

    protected final Session createNewSession(@Nullable SessionFactory sessionFactory)
            throws DataAccessResourceFailureException {
        try {
            SessionFactory factory = sessionFactory;
            if (factory == null) {
                factory = obtainSessionFactory();
            }
            return factory.builder()
                    .build();
        } catch (SessionException e) {
            throw new DataAccessResourceFailureException(
                    "Could not obtain Army-managed Session for Spring-managed transaction", e);
        }

    }

    /*################################## blow static inner class ##################################*/

    private static final class ArmyTransactionObject extends AbstractTransactionObject {

        private ArmyTransactionObject() {
        }

        Session suspend() {
            Session session = this.session;
            Assert.state(session != null, "session is null,ArmyTransactionObject state error.");
            this.session = null;
            return session;
        }

        void reset(Session newSession) {
            Assert.state(this.session == null, "session not null,ArmyTransactionObject state error.");
            this.session = newSession;
        }
    }

}
