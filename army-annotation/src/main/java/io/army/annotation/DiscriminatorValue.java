package io.army.annotation;

import java.lang.annotation.*;

/**
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DiscriminatorValue {

    int value();

}
