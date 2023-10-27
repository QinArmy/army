package io.army.sync;

/**
 * <p>This class is a implementation of {@link SyncRmSessionFactoryBuilder}.
 *
 * @see ArmySyncRmSessionFactory
 * @since 1.0
 */
final class ArmySyncRmFactoryBuilder extends ArmySyncFactoryBuilder<SyncRmSessionFactoryBuilder, SyncRmSessionFactory>
        implements SyncRmSessionFactoryBuilder {


    static ArmySyncRmFactoryBuilder create() {
        return new ArmySyncRmFactoryBuilder();
    }


    private ArmySyncRmFactoryBuilder() {
    }


}
