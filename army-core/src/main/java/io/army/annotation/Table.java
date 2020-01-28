package io.army.annotation;


import java.lang.annotation.*;

/**
 * Specifies the table for the annotated entity.
 *
 * <p>
 * <pre>
 *    Example:
 *
 *    &#064;Table(name="u_user", schema="army",comment="storage user info")
 *    public class User { ... }
 * </pre>
 *
 * @since Army 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MappedSuperclass
@Documented
public @interface Table {

    /**
     *  The name of the table.
     * <p> Defaults to the entity name.
     */
    String name();

    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
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
     */
    Index[] indexes() default {};

    /**
     * The comment of the table.
     */
    String comment() ;

    /**
     * Specifies the table immutable,if true,update sql of the table isn't allowed by army.
     * <p> Default value is false.
     */
    boolean immutable() default false;

    /** (Optional) The charset of the table.
     * <p>
     *     Default:
     *     <ul>
     *         <li>MySQL : utf8</li>
     *         <li>Oracle : utf8</li>
     *     </ul>
     *</p>
     */
    String charset() default "";

}
