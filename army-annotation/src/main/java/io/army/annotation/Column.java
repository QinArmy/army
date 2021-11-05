package io.army.annotation;


import java.io.InputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalTime;

import static java.lang.annotation.ElementType.FIELD;

/**
 *
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    /**
     * (Optional) field name of table
     *
     * <p>
     * see {@code io.army.util.StringUtils#camelToLowerCase(String)}
     * </p>
     */
    String name() default "";

    boolean nullable() default true;

    boolean alwaysNullable() default false;


    /**
     * (Optional) Whether the column is included in SQL INSERT
     * statements generated by the persistence provider.
     */
    boolean insertable() default true;

    /**
     * (Optional) Whether the column is included in SQL UPDATE
     * statements generated by the persistence provider.
     * <p>
     * blow ,army modify to false. see {@code io.army.criteria.impl.FieldMetaUtils#columnUpdatable(io.army.meta.TableMeta, java.lang.String, io.army.annotation.Column, boolean)}
     * <ol>
     *     <li>{@code TableMeta#immutable()}</li>
     *     <li>{@code TableMeta#id()}</li>
     *     <li>{@code TableMeta#CREATE_TIME}</li>
     *     <li>{@code TableMeta#discriminator()}</li>
     * </ol>
     * </p>
     */
    @Deprecated
    boolean updatable() default true;

    UpdateMode updateMode() default UpdateMode.ALWAYS;

    /**
     * (Optional) The columnSize for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int precision() default -1;

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int scale() default -1;

    /**
     * <p>
     * blow,allow to don't specify default value of column.
     *     <ol>
     *         <li>{@code TableMeta#RESERVED_PROPS}</li>
     *         <li>{@code TableMeta#discriminator()} </li>
     *         <li>mapping field java type is {@link String},because army specify default {@code ''}</li>
     *         <li>mapping field java type is {@link Long},because army specify default 0</li>
     *         <li>mapping field java type is {@link Integer},because army specify default 0</li>
     *         <li>mapping field java type is {@link java.math.BigDecimal} ,because army specify default 0.00 or 0</li>
     *         <li>mapping field java type is {@link java.math.BigInteger},because army specify default 0</li>
     *         <li>mapping field java type is {@code io.army.struct.CodeEnum},because army specify default 0</li>
     *         <li>mapping field java type is {@link InputStream},because can't specify</li>
     *         <li>mapping field java type is {@code byte[]},because can't specify</li>
     *         <li>mapping field java type is {@link Byte},because army specify default 0</li>
     *         <li>mapping field java type is {@link Short},because army specify default 0</li>
     *         <li>mapping field java type is {@link Double},because army specify default 0</li>
     *         <li>mapping field java type is {@link Float},because army specify default 0</li>
     *         <li>mapping field java type is {@link LocalTime},because army specify default {@link LocalTime#MIDNIGHT}</li>
     *     </ol>
     * </p>
     */
    String defaultValue() default "";

    /**
     * <p>
     * blow,don't specify comment of column.
     *     <ol>
     *         <li>{@code TableMeta#RESERVED_PROPS}</li>
     *         <li>{@code TableMeta#discriminator()} </li>
     *     </ol>
     * </p>
     */
    String comment() default "";

}
