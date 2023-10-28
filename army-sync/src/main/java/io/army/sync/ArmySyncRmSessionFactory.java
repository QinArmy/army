package io.army.sync;

import io.army.session.SessionException;
import io.army.session.SessionFactoryException;

/**
 * <p>This class is a implementation of {@link SyncRmSessionFactory}.
 *
 * @since 1.0
 */
final class ArmySyncRmSessionFactory extends ArmySyncSessionFactory implements SyncRmSessionFactory {

    static ArmySyncRmSessionFactory create(ArmySyncRmFactoryBuilder builder) {
        return new ArmySyncRmSessionFactory(builder);
    }


    private ArmySyncRmSessionFactory(ArmySyncRmFactoryBuilder builder) throws SessionFactoryException {
        super(builder);
    }


    @Override
    public SessionBuilder builder() {
        return null;
    }


    static final class SyncRmSessionBuilder extends SyncSessionBuilder<SessionBuilder, SyncRmSession>
            implements SessionBuilder {

        private SyncRmSessionBuilder(ArmySyncRmSessionFactory factory) {
            super(factory);
        }

        @Override
        protected SyncRmSession createSession(String sessionName) {
            this.stmtExecutor = ((ArmySyncRmSessionFactory) this.factory).stmtExecutorFactory.rmExecutor(sessionName);
            return ArmySyncRmSession.create(this);
        }

        @Override
        protected SyncRmSession handleError(SessionException cause) {
            throw cause;
        }


    } // SyncRmSessionBuilder


}
