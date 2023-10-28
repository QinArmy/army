package io.army.sync;

import io.army.env.ArmyEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is a implementation of {@link SyncRmSessionFactoryBuilder}.
 *
 * @see SyncRmSessionFactoryBuilder#builder()
 * @see ArmySyncRmSessionFactory#create(ArmySyncRmFactoryBuilder)
 * @since 1.0
 */
final class ArmySyncRmFactoryBuilder extends ArmySyncFactoryBuilder<SyncRmSessionFactoryBuilder, SyncRmSessionFactory>
        implements SyncRmSessionFactoryBuilder {


    /**
     * @see SyncRmSessionFactoryBuilder#builder()
     */
    static ArmySyncRmFactoryBuilder create() {
        return new ArmySyncRmFactoryBuilder();
    }


    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncRmFactoryBuilder.class);

    /**
     * private constructor
     */
    private ArmySyncRmFactoryBuilder() {
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /**
     * @see #buildAfterScanTableMeta(String, Object, ArmyEnvironment)
     */
    @Override
    SyncRmSessionFactory createSessionFactory() {
        return ArmySyncRmSessionFactory.create(this);
    }


}
