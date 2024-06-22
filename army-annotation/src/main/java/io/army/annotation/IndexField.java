package io.army.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface IndexField {

    String name();

    SortOrder order() default SortOrder.DEFAULT;

}
