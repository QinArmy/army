package io.army.tx.sync;

import io.army.DataAccessException;
import io.army.Session;
import io.army.SessionException;
import io.army.SessionFactory;
import io.army.context.spi.SpringCurrentSessionContext;
import io.army.tx.ArmyTransactionRollbackOnlyException;
import io.army.tx.Transaction;
import io.army.tx.TransactionRollbackOnlyException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class ArmyTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

    private final SessionFactory sessionFactory;

    private boolean resetConnection = false;


    public ArmyTransactionManager(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
        setRollbackOnCommitFailure(true);
    }

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
        Session session = obtainCurrentSession();
        if (session != null) {
            txObject.reset(session);
        }
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        return txObject.session != null && txObject.session.hasTransaction();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;

        try {
            final SessionFactory sessionFactory = this.sessionFactory;
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

        try {
            Transaction tx = txObject.session.sessionTransaction();
            if (status.isDebug()) {
                logger.debug("Committing Army transaction on Session [" + txObject.session + "]");
            }
            if (tx.rollbackOnly()) {
                if (isRollbackOnCommitFailure()) {
                    throw new ArmyTransactionRollbackOnlyException(
                            "Army transaction rollback only,please check child update.");
                } else {
                    throw new TransactionRollbackOnlyException(
                            "Army transaction rollback only,please check child update.");
                }
            }
            if (!tx.readOnly()) {
                // commit transaction
                txObject.flush();
                tx.commit();
            }
        } catch (TransactionRollbackOnlyException | ArmyTransactionRollbackOnlyException e) {
            throw e;
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        } catch (SessionException e) {
            throw SpringTxUtils.convertSessionException(e);
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        try {
            Transaction tx = txObject.session.sessionTransaction();
            if (status.isDebug()) {
                logger.debug("Rolling Army transaction on Session [" + txObject.session + "]");
            }
            if (!tx.readOnly()) {
                tx.rollback();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
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
    protected Object doSuspend(Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
        return txObject.suspend();
    }

    @Override
    protected void doResume(@Nullable Object transaction, Object suspendedResources) throws TransactionException {

        SessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        Session session = (Session) suspendedResources;
        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        Assert.state(txObject.session != null, "No Army session.");

        SessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        try {
            Session session = txObject.session;
            if (session.hasTransaction()) {
                session.sessionTransaction().close();
            }
            session.close();
        } catch (DataAccessException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    /*################################## blow setter method ##################################*/

    public boolean isResetConnection() {
        return resetConnection;
    }

    public ArmyTransactionManager setResetConnection(boolean resetConnection) {
        this.resetConnection = resetConnection;
        return this;
    }


    @Nullable
    protected final Session obtainCurrentSession() {
        return (Session) TransactionSynchronizationManager.getResource(this.sessionFactory);
    }

    protected final Session createNewSession(@Nullable SessionFactory sessionFactory)
            throws DataAccessResourceFailureException {
        try {
            SessionFactory factory = sessionFactory;
            if (factory == null) {
                factory = this.sessionFactory;
            }
            return factory.builder()
                    .resetConnection(this.resetConnection)
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
            return this.session;
        }

        void reset(Session newSession) {
            Assert.state(this.session == null, "session not null,ArmyTransactionObject state error.");
            this.session = newSession;
        }
    }

}
