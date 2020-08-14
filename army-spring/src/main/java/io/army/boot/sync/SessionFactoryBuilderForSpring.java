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
    protected final boolean springApplication() {
        return true;
    }
}
