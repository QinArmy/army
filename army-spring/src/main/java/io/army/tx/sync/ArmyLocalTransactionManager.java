package io.army.tx.sync;

import io.army.session.HandleMode;
import io.army.session.Option;
import io.army.session.SessionException;
import io.army.session.TransactionOption;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * @since 1.0
 */
public final class ArmyLocalTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

    static ArmyLocalTransactionManager create(SyncSessionFactory sessionFactory) {
        return new ArmyLocalTransactionManager(sessionFactory);
    }

    private final SyncSessionFactory sessionFactory;

    private final boolean supportSavePoints;

    private boolean useReadOnlyTransaction;

    private boolean useTransactionName;


    private ArmyLocalTransactionManager(SyncSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
        this.supportSavePoints = sessionFactory.isSupportSavePoints();
    }


    @Override
    public void afterPropertiesSet() {
        // register transaction manager for read-write splitting
        //TransactionDefinitionHolder.registerTransactionManager(this.beanName, this.useSavepointForNestedTransaction());
    }

    public boolean isUseReadOnlyTransaction() {
        return this.useReadOnlyTransaction;
    }

    public void setUseReadOnlyTransaction(boolean useReadOnlyTransaction) {
        this.useReadOnlyTransaction = useReadOnlyTransaction;
    }


    public boolean isUseTransactionName() {
        return this.useTransactionName;
    }

    public void setUseTransactionName(boolean useTransactionName) {
        this.useTransactionName = useTransactionName;
    }

    public boolean isSupportSavePoints() {
        return this.supportSavePoints;
    }

    /*################################## blow AbstractPlatformTransactionManager template method ##################################*/

    @Override
    protected Object doGetTransaction() throws TransactionException {
        final ArmyTransactionObject txObject = new ArmyTransactionObject();
        final SyncLocalSession session;
        session = (SyncLocalSession) TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.setSession(session);
        }
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(final Object transaction) throws TransactionException {
        final SyncLocalSession session = ((ArmyTransactionObject) transaction).session;
        if (session == null) {
            return false;
        }
        try {
            return session.inTransaction();
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected Object doSuspend(final Object transaction) throws TransactionException {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        if (txObject.session == null) {
            throw transactionNoSession();
        }
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
        return txObject.suspend();
    }

    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition)
            throws TransactionException {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;

        SyncLocalSession session = null;
        try {
            //1.get transaction name
            final String txLabel;
            txLabel = definition.getName();

            //2. create session
            final boolean readOnly;
            readOnly = definition.isReadOnly();

            session = this.sessionFactory.localBuilder()
                    .name(txLabel)
                    .readonly(readOnly)
                    .build();

            // bind to txObject
            txObject.setSession(session);

            final int isolationLevel;
            isolationLevel = definition.getIsolationLevel();

            if (!readOnly || isolationLevel != TransactionDefinition.ISOLATION_DEFAULT || this.useReadOnlyTransaction) {

                //3 . create TransactionOption
                final TransactionOption.Builder builder;
                builder = TransactionOption.builder()
                        .option(Option.READ_ONLY, readOnly);


                if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
                    builder.option(Option.ISOLATION, SpringUtils.toArmyIsolation(isolationLevel));
                }

                if (txLabel != null) {
                    builder.option(Option.LABEL, txLabel);
                    if (this.useTransactionName) {
                        builder.option(Option.NAME, txLabel);
                    }
                }

                int timeoutSeconds;
                timeoutSeconds = definition.getTimeout();
                if (timeoutSeconds == TransactionDefinition.TIMEOUT_DEFAULT) {
                    timeoutSeconds = getDefaultTimeout();
                }
                if (timeoutSeconds > 0) {
                    final long timeoutMillis = timeoutSeconds * 1000L;
                    if (timeoutMillis > Integer.MAX_VALUE) {
                        throw new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE");
                    }
                    builder.option(Option.TIMEOUT, (int) timeoutMillis);
                }

                // 5. start transaction
                session.startTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS);


            } //    if (!readOnly  || isolationLevel != TransactionDefinition.ISOLATION_DEFAULT)


            // 6. bind current session
            TransactionSynchronizationManager.bindResource(this.sessionFactory, session);

        } catch (Exception e) {
            if (session != null) {
                session.close();
            }
            if (e instanceof SessionException) {
                throw SpringUtils.wrapSessionError((SessionException) e);
            }
            throw e;
        } catch (Throwable e) {
            if (session != null) {
                session.close();
            }
            throw e;
        }
    }

    @Override
    protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
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
        } catch (io.army.session.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected final void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
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
        } catch (io.army.session.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected final void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        final ArmyTransactionObject txObject;
        txObject = (ArmyTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
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
        } catch (io.army.session.TransactionException e) {
            throw SpringUtils.wrapTransactionError(e);
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected void doResume(final @Nullable Object transaction, final Object suspendedResources)
            throws TransactionException {

        final SyncLocalSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        final SyncLocalSession session = (SyncLocalSession) suspendedResources;
        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected final void doCleanupAfterCompletion(final Object transaction) {
        final ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        final SyncLocalSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
        try {
            session.close();
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }


    @Override
    protected boolean useSavepointForNestedTransaction() {
        return this.supportSavePoints;
    }




    /*################################## blow setter method ##################################*/


    private static TransactionUsageException transactionNoSession() {
        return new TransactionUsageException("current transaction no session.");
    }

    /*################################## blow static inner class ##################################*/

    private static final class ArmyTransactionObject implements SavepointManager, SmartTransactionObject {

        private SyncLocalSession session;

        private ArmyTransactionObject() {
        }


        private void setSession(final SyncLocalSession session) {
            if (this.session != null) {
                throw new IllegalStateException("session non-null.");
            }
            this.session = session;
        }

        private SyncLocalSession suspend() {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw new IllegalStateException("no session , couldn't suspend.");
            }
            this.session = null;
            return session;
        }


        /*################################## blow SavepointManager method ##################################*/

        @Override
        public Object createSavepoint() throws TransactionException {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                return session.currentTransaction().createSavePoint();
            } catch (io.army.session.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.currentTransaction().rollbackToSavePoint(savepoint);
            } catch (io.army.session.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.currentTransaction().releaseSavePoint(savepoint);
            } catch (io.army.session.TransactionException e) {
                throw SpringUtils.wrapTransactionError(e);
            }
        }


        @Override
        public boolean isRollbackOnly() {
            final SyncLocalSession session = this.session;
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
