package io.army.tx.sync;

import io.army.session.*;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.*;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * <p>This class is Army sync local transaction manager
 *
 * @since 1.0
 */
public final class ArmySyncLocalTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

    public static ArmySyncLocalTransactionManager create(SyncSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        return new ArmySyncLocalTransactionManager(sessionFactory);
    }

    private final SyncSessionFactory sessionFactory;


    private boolean useReadOnlyTransaction = true;

    private boolean useTransactionName;

    /**
     * private constructor
     */
    private ArmySyncLocalTransactionManager(SyncSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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


    /*################################## blow AbstractPlatformTransactionManager template method ##################################*/

    @Override
    protected Object doGetTransaction() throws TransactionException {
        final LocalTransactionObject txObject = new LocalTransactionObject();
        final SyncLocalSession session;
        session = (SyncLocalSession) TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.setSession(session);
        }
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(final Object transaction) throws TransactionException {
        final SyncLocalSession session = ((LocalTransactionObject) transaction).session;
        return session != null && session.hasTransactionInfo(); // here , use hasTransactionInfo() not inTransaction(),  because perhaps pseudo transaction
    }


    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition)
            throws TransactionException {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;

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

            final int timeoutSeconds;
            timeoutSeconds = definition.getTimeout();

            //3 . create TransactionOption
            final TransactionOption.Builder builder;
            builder = TransactionOption.builder()
                    .option(Option.READ_ONLY, readOnly);


            if (txLabel != null) {
                builder.option(Option.LABEL, txLabel);
                if (this.useTransactionName) {
                    builder.option(Option.NAME, txLabel);
                }
            }


            final long timeoutMillis;
            if (timeoutSeconds == TransactionDefinition.TIMEOUT_DEFAULT) {
                timeoutMillis = getDefaultTimeout() * 1000L;
            } else if (timeoutSeconds > 0) {
                timeoutMillis = timeoutSeconds * 1000L;
            } else {
                timeoutMillis = 0L;
            }

            if (timeoutMillis > 0L) {
                if (timeoutMillis > Integer.MAX_VALUE) {
                    throw new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE");
                }
                builder.option(Option.TIMEOUT_MILLIS, (int) timeoutMillis);
            }

            final TransactionInfo info;

            // 5. start transaction
            if (!readOnly
                    || isolationLevel != TransactionDefinition.ISOLATION_DEFAULT
                    || this.useReadOnlyTransaction) {

                if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
                    builder.option(Option.ISOLATION, SpringUtils.toArmyIsolation(isolationLevel));
                }
                // start real transaction
                info = session.startTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS);

                assert info.inTransaction();
                assert !info.isReadOnly();
            } else {
                builder.option(Option.ISOLATION, Isolation.PSEUDO);

                // start pseudo transaction
                info = session.pseudoTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS);

                assert !info.inTransaction();
                assert info.isReadOnly();
                assert info.isolation() == Isolation.PSEUDO;
            }

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
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        } else if (!session.hasTransactionInfo()) {
            // application developer end transaction by SyncLocalSession api
            throw SpringUtils.unexpectedTransactionEnd(session);
        }

        try {
            session.commit(Option.EMPTY_OPTION_FUNC);
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        } else if (!session.hasTransactionInfo()) {
            // application developer end transaction by SyncLocalSession api
            throw SpringUtils.unexpectedTransactionEnd(session);
        }
        try {
            session.rollback(Option.EMPTY_OPTION_FUNC);
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw transactionNoSession();
        }

        session.markRollbackOnly();
    }


    @Override
    protected Object doSuspend(final Object transaction) throws TransactionException {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        if (txObject.session == null) {
            throw transactionNoSession();
        }
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
        return txObject.suspend();
    }

    @Override
    protected void doResume(final @Nullable Object transaction, final Object suspendedResources)
            throws TransactionException {

        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        if (txObject == null) {
            // no bug never here
            throw new IllegalTransactionStateException("no transaction object");
        }

        final SyncSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // From non-transactional code running in active transaction synchronization
            // -> can be safely removed, will be closed on transaction completion.
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }

        final SyncLocalSession session = (SyncLocalSession) suspendedResources;
        txObject.setSession(session);

        TransactionSynchronizationManager.bindResource(sessionFactory, session);
    }

    @Override
    protected void doCleanupAfterCompletion(final Object transaction) {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;

        final SyncSessionFactory sessionFactory = this.sessionFactory;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }

        final SyncLocalSession session = txObject.session;
        if (session == null) {
            return;
        }
        try {
            session.close();
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }


    @Override
    protected boolean useSavepointForNestedTransaction() {
        return true;
    }




    /*################################## blow setter method ##################################*/


    private static TransactionUsageException transactionNoSession() {
        return new TransactionUsageException("current transaction no session.");
    }

    /*################################## blow static inner class ##################################*/

    private static final class LocalTransactionObject implements SavepointManager, SmartTransactionObject {

        private SyncLocalSession session;

        private LocalTransactionObject() {
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
                return session.setSavePoint(Option.EMPTY_OPTION_FUNC);
            } catch (SessionException e) {
                throw SpringUtils.wrapSessionError(e);
            }
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
            } catch (SessionException e) {
                throw SpringUtils.wrapSessionError(e);
            }
        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {
            final SyncLocalSession session = this.session;
            if (session == null) {
                throw transactionNoSession();
            }
            try {
                session.releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
            } catch (SessionException e) {
                throw SpringUtils.wrapSessionError(e);
            }
        }


        @Override
        public boolean isRollbackOnly() {
            final SyncLocalSession session = this.session;
            return session != null && session.isRollbackOnly();
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
