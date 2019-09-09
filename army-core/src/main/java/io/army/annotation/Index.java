package io.army.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * created  on 2018/9/19.
 */
@Target({})
@Retention(RUNTIME)
public @interface Index {

    /**
     * (Optional) The name of the index; defaults to a provider-generated name.
     */
    String name();

    /**
     * (Required) The names of the columns to be included in the index,
     * in order.
     */
    String[] columnList();

    /**
     * (Optional) Whether the index is unique.
     */
    boolean unique() default false;

    String type() default "";

}
