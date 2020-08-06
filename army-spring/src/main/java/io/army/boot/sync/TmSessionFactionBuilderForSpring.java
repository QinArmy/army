package io.army.boot.sync;

/**
 * This class instance created by super class method.
 *
 * @see TmSessionFactionBuilderImpl#buildInstance(boolean)
 */
@SuppressWarnings("unused")
final class TmSessionFactionBuilderForSpring extends TmSessionFactionBuilderImpl {

    @Override
    final TmSessionFactoryImpl createSessionFactory() {
        return new TmSessionFactoryForSpring(this);
    }

    @Override
    final boolean initializeSessionFactory(TmSessionFactoryImpl sessionFactory) {
        // no-op, initialize lazy util org.springframework.beans.factory.InitializingBean.afterPropertiesSet
        return false;
    }

    @Override
    protected final boolean springApplication() {
        return true;
    }
}
