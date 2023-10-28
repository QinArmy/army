package io.army.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is a implementation of {@link SyncLocalFactoryBuilder}.
 *
 * @see SyncLocalFactoryBuilder#builder()
 * @see ArmySyncLocalSessionFactory#create(ArmySyncLocalFactoryBuilder)
 * @since 1.0
 */
final class ArmySyncLocalFactoryBuilder extends ArmySyncFactoryBuilder<SyncLocalFactoryBuilder, SyncLocalSessionFactory>
        implements SyncLocalFactoryBuilder {

    /**
     * @see SyncLocalFactoryBuilder#builder()
     */
    static ArmySyncLocalFactoryBuilder create() {
        return new ArmySyncLocalFactoryBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncLocalFactoryBuilder.class);


    /**
     * private constructor
     */
    private ArmySyncLocalFactoryBuilder() {
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    SyncLocalSessionFactory createSessionFactory() {
        return ArmySyncLocalSessionFactory.create(this);
    }


}
