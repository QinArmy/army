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
     * (Optional) The name of the indexMap; defaults to a provider-generated name.
     */
    String name();

    /**
     * (Required) The names of the columnMap to be included in the indexMap,
     * in order.
     */
    String[] columnList();

    /**
     * (Optional) Whether the indexMap is unique.
     */
    boolean unique() default false;

    String type() default "";

}
