package io.army.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>This class is a implementation of {@link ReactiveRmFactoryBuilder}.
 *
 * @see ReactiveRmFactoryBuilder#builder()
 * @see ArmyReactiveRmSessionFactory#create(ArmyRmSessionFactoryBuilder)
 * @since 1.0
 */
final class ArmyRmSessionFactoryBuilder extends ArmyReactiveFactorBuilder<ReactiveRmFactoryBuilder, ReactiveRmSessionFactory>
        implements ReactiveRmFactoryBuilder {


    static ArmyRmSessionFactoryBuilder create() {
        return new ArmyRmSessionFactoryBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmyRmSessionFactoryBuilder.class);


    /**
     * private constructor
     */
    private ArmyRmSessionFactoryBuilder() {
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }


    @Override
    ReactiveRmSessionFactory createSessionFactory() {
        return ArmyReactiveRmSessionFactory.create(this);
    }


}
