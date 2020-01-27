package io.army.annotation;


import java.lang.annotation.*;

/**
 * created  on 2018/9/19.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MappedSuperclass
@Documented
public @interface Table {


    String name();

    String catalog() default "";

    /**
     * (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";

    /**
     * (Optional) Indexes for the table.  These are only used if
     * table generation is in effect.  Note that it is not necessary
     * to specify an indexMap for a primary key, as the primary key
     * indexMap will be created automatically.
     *
     * @since Java Persistence 2.1
     */
    Index[] indexes() default {};

    String comment() default "";

    boolean immutable() default false;

    String charset() default "";

}
