package io.army.annotation;


import io.army.meta.TableMeta;

import java.lang.annotation.*;

/**
 * Specifies the tableMeta for the annotated domain.
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
     * (Required) The name of the table.
     *
     * @see TableMeta#tableName()
     */
    String name();

    /**
     * (Optional) The catalog of the table.
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
     * tableMeta generation is in effect.  Note that it is not necessary
     * to specify an indexMap for a primary key, asType the primary key
     * indexMap will be created automatically.
     */
    Index[] indexes() default {};

    /**
     * (Required) The comment of the tableMeta.
     */
    String comment();

    /**
     * Specifies the tableMeta immutable,if true,singleUpdate dml of the tableMeta is allowed by army.
     * <p> Default value is false.
     */
    boolean immutable() default false;

    /**
     * (Optional) The charset of the tableMeta.
     * <p>
     * Default:
     *     <ul>
     *         <li>MySQL : utf8</li>
     *         <li>Oracle : utf8</li>
     *     </ul>
     * </p>
     */
    String charset() default "";
}
