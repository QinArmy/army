package io.army.spring.sync;

import io.army.session.*;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncSessionContext;
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

    private final SyncSessionContext sessionContext;

    private boolean pseudoTransactionAllowed;

    private boolean useTransactionName;

    private boolean useTransactionLabel;

    private boolean useDataSourceTimeout;

    private boolean useDataSourceLabel;


    /**
     * private constructor
     */
    private ArmySyncLocalTransactionManager(SyncSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.sessionContext = SpringSyncSessionContext.create(sessionFactory);
    }


    @Override
    public void afterPropertiesSet() {
        // register transaction manager for read-write splitting
        //TransactionDefinitionHolder.registerTransactionManager(this.beanName, this.useSavepointForNestedTransaction());
    }


    public SyncSessionContext getSessionContext() {
        return this.sessionContext;
    }

    public boolean isPseudoTransactionAllowed() {
        return this.pseudoTransactionAllowed;
    }

    public ArmySyncLocalTransactionManager setPseudoTransactionAllowed(boolean pseudoTransactionAllowed) {
        this.pseudoTransactionAllowed = pseudoTransactionAllowed;
        return this;
    }


    public boolean isUseTransactionName() {
        return this.useTransactionName;
    }

    public ArmySyncLocalTransactionManager setUseTransactionName(boolean useTransactionName) {
        this.useTransactionName = useTransactionName;
        return this;
    }

    public boolean isUseTransactionLabel() {
        return this.useTransactionLabel;
    }

    public ArmySyncLocalTransactionManager setUseTransactionLabel(boolean useTransactionLabel) {
        this.useTransactionLabel = useTransactionLabel;
        return this;
    }

    public boolean isUseDataSourceTimeout() {
        return this.useDataSourceTimeout;
    }

    public ArmySyncLocalTransactionManager setUseDataSourceTimeout(boolean useDataSourceTimeout) {
        this.useDataSourceTimeout = useDataSourceTimeout;
        return this;
    }

    public boolean isUseDataSourceLabel() {
        return this.useDataSourceLabel;
    }

    public ArmySyncLocalTransactionManager setUseDataSourceLabel(boolean useDataSourceLabel) {
        this.useDataSourceLabel = useDataSourceLabel;
        return this;
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

        try {
            // 1. get transaction options
            final String txLabel;
            txLabel = definition.getName();

            final boolean readOnly;
            readOnly = definition.isReadOnly();

            final int timeoutMillis;
            timeoutMillis = timeoutMillis(definition.getTimeout());

            // 2. create session
            final SyncLocalSession session;
            session = this.sessionFactory.localBuilder()

                    .name(txLabel)
                    .readonly(readOnly)
                    .dataSourceOption(Option.LABEL, this.useDataSourceLabel ? txLabel : null)
                    .dataSourceOption(Option.TIMEOUT_MILLIS, (timeoutMillis > 0 && this.useDataSourceTimeout) ? timeoutMillis : null)

                    .build();

            // immediately  bind to txObject
            txObject.setSession(session);

            // 3. create transaction option

            final TransactionOption.Builder txOptionBuilder;
            txOptionBuilder = TransactionOption.builder()
                    .option(Option.READ_ONLY, readOnly);

            if (txLabel != null) {
                if (this.useTransactionLabel) {
                    txOptionBuilder.option(Option.LABEL, txLabel);
                }
                if (this.useTransactionName) {
                    txOptionBuilder.option(Option.NAME, txLabel);
                }
            }

            if (timeoutMillis > 0L) {
                txOptionBuilder.option(Option.TIMEOUT_MILLIS, timeoutMillis);
            }

            final int isolationLevel;
            isolationLevel = definition.getIsolationLevel();

            final TransactionInfo info;
            // 4. start transaction
            if (readOnly
                    && isolationLevel == TransactionDefinition.ISOLATION_DEFAULT
                    && this.pseudoTransactionAllowed) {

                txOptionBuilder.option(Option.ISOLATION, Isolation.PSEUDO);

                // start pseudo transaction
                info = session.pseudoTransaction(txOptionBuilder.build(), HandleMode.ERROR_IF_EXISTS);

                assert !info.inTransaction();
                assert info.isReadOnly();
                assert info.isolation() == Isolation.PSEUDO;
            } else {
                if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
                    txOptionBuilder.option(Option.ISOLATION, SpringUtils.toArmyIsolation(isolationLevel));
                }
                // start real transaction
                info = session.startTransaction(txOptionBuilder.build(), HandleMode.ERROR_IF_EXISTS);

                assert info.inTransaction();
            }

            // 6. bind current session
            TransactionSynchronizationManager.bindResource(this.sessionFactory, session);

        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }

    @Override
    protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw SpringUtils.transactionNoSession();
        } else if (session.hasTransactionInfo()) {
            try {
                session.commit(Option.EMPTY_OPTION_FUNC);
            } catch (SessionException e) {
                throw SpringUtils.wrapSessionError(e);
            }
        } else {
            // application developer end transaction by SyncLocalSession api
            throw SpringUtils.unexpectedTransactionEnd(session);
        }


    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw SpringUtils.transactionNoSession();
        } else if (session.hasTransactionInfo()) {
            try {
                session.rollback(Option.EMPTY_OPTION_FUNC);
            } catch (SessionException e) {
                throw SpringUtils.wrapSessionError(e);
            }
        } else {
            // application developer end transaction by SyncLocalSession api
            throw SpringUtils.unexpectedTransactionEnd(session);
        }

    }

    @Override
    protected void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        final LocalTransactionObject txObject;
        txObject = (LocalTransactionObject) status.getTransaction();
        final SyncLocalSession session = txObject.session;
        if (session == null) {
            throw SpringUtils.transactionNoSession();
        }

        session.markRollbackOnly();
    }


    @Override
    protected Object doSuspend(final Object transaction) throws TransactionException {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        if (txObject.session == null) {
            throw SpringUtils.transactionNoSession();
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
            txObject.session = null;
            session.close();
        } catch (SessionException e) {
            throw SpringUtils.wrapSessionError(e);
        }
    }


    /*-------------------below private methods -------------------*/

    private int timeoutMillis(final int timeoutSeconds) {

        final long timeoutMillis;
        if (timeoutSeconds == TransactionDefinition.TIMEOUT_DEFAULT) {
            timeoutMillis = getDefaultTimeout() * 1000L;
        } else if (timeoutSeconds > 0) {
            timeoutMillis = timeoutSeconds * 1000L;
        } else {
            timeoutMillis = 0L;
        }

        if (timeoutMillis > Integer.MAX_VALUE) {
            throw new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE");
        }
        return (int) timeoutMillis;
    }

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
                throw SpringUtils.transactionNoSession();
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
                throw SpringUtils.transactionNoSession();
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
                throw SpringUtils.transactionNoSession();
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
