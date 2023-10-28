package io.army.sync;

import io.army.session.SessionFactoryException;
import io.army.util._Exceptions;

/**
 * <p>This class is a implementation of {@link SyncLocalSessionFactory}
 *
 * @see ArmySyncLocalSession
 * @see ArmySyncLocalFactoryBuilder#createSessionFactory()
 */
final class ArmySyncLocalSessionFactory extends ArmySyncSessionFactory implements SyncLocalSessionFactory {

    /**
     * @see ArmySyncLocalFactoryBuilder#createSessionFactory()
     */
    static ArmySyncLocalSessionFactory create(ArmySyncLocalFactoryBuilder builder) {
        return new ArmySyncLocalSessionFactory(builder);
    }

    /**
     * private constructor
     */
    private ArmySyncLocalSessionFactory(ArmySyncLocalFactoryBuilder builder) throws SessionFactoryException {
        super(builder);

    }


    @Override
    public SessionBuilder builder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new LocalSessionBuilder(this);
    }


    /*################################## blow instance inner class  ##################################*/

    static final class LocalSessionBuilder extends SyncSessionBuilder<SessionBuilder, SyncLocalSession>
            implements SyncLocalSessionFactory.SessionBuilder {

        private LocalSessionBuilder(ArmySyncLocalSessionFactory factory) {
            super(factory);
        }


        @Override
        protected SyncLocalSession createSession(String sessionName) {
            this.stmtExecutor = ((ArmySyncLocalSessionFactory) this.factory)
                    .stmtExecutorFactory.localExecutor(sessionName);
            return ArmySyncLocalSession.create(this);
        }


    }//LocalSessionBuilder


}
