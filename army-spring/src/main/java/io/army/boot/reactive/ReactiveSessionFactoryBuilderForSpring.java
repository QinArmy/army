package io.army.boot.reactive;

import io.army.tx.sync.SpringUtils;

import java.util.function.Function;

/**
 * This class constructor will invoked by base class {@link ReactiveSessionFactoryBuilderImpl}
 */
@SuppressWarnings("unused")
final class ReactiveSessionFactoryBuilderForSpring extends ReactiveSessionFactoryBuilderImpl {

    ReactiveSessionFactoryBuilderForSpring() {
        super(true);
    }

    @Override
    protected Function<RuntimeException, RuntimeException> springExceptionFunction() {
        return SpringUtils::convertToSpringException;
    }
}
