package io.army.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>This class is a implementation of {@link ReactiveLocalFactoryBuilder}.
 *
 * @see ReactiveLocalFactoryBuilder#builder()
 * @see ArmyReactiveLocalSessionFactory#create(ArmyLocalSessionFactoryBuilder)
 * @since 1.0
 */
final class ArmyLocalSessionFactoryBuilder extends ArmyReactiveFactorBuilder<ReactiveLocalFactoryBuilder, ReactiveLocalSessionFactory>
        implements ReactiveLocalFactoryBuilder {

    static ArmyLocalSessionFactoryBuilder create() {
        return new ArmyLocalSessionFactoryBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmyLocalSessionFactoryBuilder.class);


    /**
     * private constructor
     */
    private ArmyLocalSessionFactoryBuilder() {

    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    ReactiveLocalSessionFactory createSessionFactory() {
        return ArmyReactiveLocalSessionFactory.create(this);
    }


}
