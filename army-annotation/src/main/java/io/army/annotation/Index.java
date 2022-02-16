package io.army.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @since 1.0
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
     * in asSort.
     */
    String[] fieldList();

    /**
     * (Optional) Whether the indexMap is unique.
     */
    boolean unique() default false;

    String type() default "";

}
