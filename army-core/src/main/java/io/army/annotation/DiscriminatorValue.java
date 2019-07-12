package io.army.annotation;

import java.lang.annotation.*;

/**
 * created  on 2018/9/27.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DiscriminatorValue {

    int value();
}
