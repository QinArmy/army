package io.army.tx.sync;

import io.army.DataAccessException;
import io.army.SessionException;
import io.army.sync.TmSession;
import io.army.sync.TmSessionFactory;
import io.army.tx.TmTransaction;
import org.springframework.beans.factory.BeanNameAware;
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

public class ArmyTmTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean
        , BeanNameAware {


    private final TmSessionFactory tmSessionFactory;

    private String beanName;


    public ArmyTmTransactionManager(TmSessionFactory tmSessionFactory) {
        Assert.notNull(tmSessionFactory, "tmSessionFactory required");
        this.tmSessionFactory = tmSessionFactory;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() {
        // register transaction manager for read-write splitting
        TransactionDefinitionHolder.registerTransactionManager(this.beanName, this.useSavepointForNestedTransaction());
    }


    @Override
    protected final Object doGetTransaction() throws TransactionException {
        ArmyTmTransactionObject txObject = new ArmyTmTransactionObject();
        TmSession session = (TmSession) TransactionSynchronizationManager.getResource(this.tmSessionFactory);
        if (session != null) {
            txObject.setSession(session);
        }
        return txObject;
    }

    @Override
    protected final boolean isExistingTransaction(Object transaction) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) transaction;
        return txObject.session != null && txObject.session.hasTransaction();
    }

    @Override
    protected final Object doSuspend(Object transaction) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) transaction;
        if (txObject.session == null) {
            throw new IllegalTransactionStateException("transaction no army session.");
        }
        TransactionSynchronizationManager.unbindResource(this.tmSessionFactory);
        return txObject.suspend();
    }

    @Override
    protected final void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) transaction;

        try {
            txObject.setSession(createNewSession(definition));
            final TmSession tmSession = txObject.session;
            final TmTransaction tx = tmSession.sessionTransaction();
            // start transaction by JDBC
            tx.start();
            TransactionSynchronizationManager.bindResource(this.tmSessionFactory, tmSession);
        } catch (Throwable e) {
            throw new CannotCreateTransactionException("Could not open Army transaction", e);
        }
    }

    @Override
    protected final void doCommit(DefaultTransactionStatus status) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) status.getTransaction();
        final TmSession tmSession = txObject.session;
        if (tmSession == null) {
            throw new IllegalTransactionStateException("transaction no army TmSession.");
        }

        try {
            final TmTransaction tx = tmSession.sessionTransaction();
            if (status.isDebug()) {
                logger.debug("Committing Army transaction on " + tmSession);
            }
            if (!tx.readOnly()) {
                // commit transaction
                txObject.flush();
                tx.commit();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        } catch (SessionException e) {
            throw SpringUtils.convertSessionException(e);
        }
    }

    @Override
    protected final void doRollback(DefaultTransactionStatus status) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) status.getTransaction();
        final TmSession tmSession = txObject.session;
        if (tmSession == null) {
            throw new IllegalTransactionStateException("transaction no army TmSession.");
        }
        try {
            final TmTransaction tx = tmSession.sessionTransaction();
            if (status.isDebug()) {
                logger.debug("Rolling Army transaction on  " + tmSession);
            }
            if (!tx.readOnly()) {
                tx.rollback();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        }
    }

    @Override
    protected final void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) status.getTransaction();
        final TmSession tmSession = txObject.session;
        if (tmSession == null) {
            throw new IllegalTransactionStateException("transaction no army TmSession.");
        }
        if (status.isDebug()) {
            logger.debug("Setting Army transaction on " + tmSession + " rollback-only");
        }
        try {
            final TmTransaction tx = tmSession.sessionTransaction();
            if (!tx.readOnly()) {
                tx.markRollbackOnly();
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringUtils.convertTransactionException(e);
        }
    }

    @Override
    protected final void doResume(@Nullable Object transaction, Object suspendedResources) throws TransactionException {
        final TmSessionFactory tmSessionFactory = this.tmSessionFactory;
        if (TransactionSynchronizationManager.hasResource(tmSessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(tmSessionFactory);
        }
        TmSession tmSession = (TmSession) suspendedResources;
        TransactionSynchronizationManager.bindResource(tmSessionFactory, tmSession);
    }


    @Override
    protected final void doCleanupAfterCompletion(Object transaction) {
        ArmyTmTransactionObject txObject = (ArmyTmTransactionObject) transaction;
        final TmSession tmSession = txObject.session;
        Assert.state(tmSession != null, "No Army session.");

        final TmSessionFactory tmSessionFactory = this.tmSessionFactory;
        if (TransactionSynchronizationManager.hasResource(tmSessionFactory)) {
            TransactionSynchronizationManager.unbindResource(tmSessionFactory);
        }
        try {
            tmSession.sessionTransaction().close();
            tmSession.close();
        } catch (DataAccessException e) {
            throw SpringUtils.convertArmyAccessException(e);
        }
    }

    @Override
    protected final boolean useSavepointForNestedTransaction() {
        // always false
        return false;
    }

    @Override
    protected final void prepareForCommit(DefaultTransactionStatus status) {
        status.flush();
    }

    /*################################## blow private method ##################################*/

    private TmSession createNewSession(TransactionDefinition definition)
            throws DataAccessResourceFailureException {
        try {
            return this.tmSessionFactory.builder()

                    .transactionName(definition.getName())
                    .isolation(SpringUtils.convertTotArmyIsolation(definition.getIsolationLevel()))
                    .readOnly(definition.isReadOnly())
                    .timeout(determineTimeout(definition))

                    .build();
        } catch (SessionException e) {
            throw new DataAccessResourceFailureException(
                    "Could not obtain Army-managed Session for Spring-managed transaction", e);
        }

    }

    private static final class ArmyTmTransactionObject extends AbstractTransactionObject<TmSession> {

        private ArmyTmTransactionObject() {
        }

        @Override
        public boolean isRollbackOnly() {
            return this.session.sessionTransaction().rollbackOnly();
        }
    }
}
