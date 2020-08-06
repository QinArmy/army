package io.army.boot.sync;

/**
 * This class instance created by super class method.
 *
 * @see SessionFactoryBuilderImpl#buildInstance(boolean)
 */
@SuppressWarnings("unused")
final class SessionFactoryBuilderForSpring extends SessionFactoryBuilderImpl {

    SessionFactoryBuilderForSpring() {
    }

    @Override
    final SessionFactoryImpl createSessionFactory() {
        return new SessionFactoryForSpring(this);
    }

    @Override
    final boolean initializeSessionFactory(SessionFactoryImpl sessionFactory) {
        // no-op, initialize lazy util org.springframework.beans.factory.InitializingBean.afterPropertiesSet
        return false;
    }

    @Override
    protected final boolean springApplication() {
        return true;
    }
}
