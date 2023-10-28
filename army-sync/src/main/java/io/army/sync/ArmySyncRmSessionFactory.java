package io.army.sync;

import io.army.session.SessionFactoryException;
import io.army.util._Exceptions;

/**
 * <p>This class is a implementation of {@link SyncRmSessionFactory}.
 *
 * @see ArmySyncRmFactoryBuilder#createSessionFactory()
 * @since 1.0
 */
final class ArmySyncRmSessionFactory extends ArmySyncSessionFactory implements SyncRmSessionFactory {

    /**
     * @see ArmySyncRmFactoryBuilder#createSessionFactory()
     */
    static ArmySyncRmSessionFactory create(ArmySyncRmFactoryBuilder builder) {
        return new ArmySyncRmSessionFactory(builder);
    }

    /**
     * private constructor
     */
    private ArmySyncRmSessionFactory(ArmySyncRmFactoryBuilder builder) throws SessionFactoryException {
        super(builder);
    }


    @Override
    public SessionBuilder builder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new RmSessionBuilder(this);
    }


    static final class RmSessionBuilder extends SyncSessionBuilder<SessionBuilder, SyncRmSession>
            implements SessionBuilder {

        private RmSessionBuilder(ArmySyncRmSessionFactory factory) {
            super(factory);
        }

        /**
         * @see #builder()
         */
        @Override
        protected SyncRmSession createSession(String sessionName) {
            this.stmtExecutor = ((ArmySyncRmSessionFactory) this.factory).stmtExecutorFactory.rmExecutor(sessionName);
            return ArmySyncRmSession.create(this);
        }


    } // SyncRmSessionBuilder


}
