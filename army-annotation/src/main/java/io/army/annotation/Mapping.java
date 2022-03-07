package io.army.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * <p>
     * Required
     * </p>
     *
     * @return the qualified class name of the implementation of {@code io.army.mapping.MappingType}.
     */
    String value();

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.TextMappingType} and representing binary then required,
     * else ignore.
     * </p>
     * <p>
     * example:
     * <pre>
     *         <br/><code>
     *              &#64;Mapping("io.army.mapping.mysql.MySQLLongTextType",charset="UTF-8")
     *              &#64;Column(comment="user article")
     *              private java.nio.file.Path article;
     *
     *              &#64;Mapping("io.army.mapping.mysql.MySQLLongTextType",charset="UTF-8")
     *              &#64;Column(comment="user info")
     *              private InputStream userInfo;
     *         </code>
     *     </pre>
     * </p>
     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    String charset() default "";

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.ElementMappingType} then required,
     * else ignore.
     * </p>
     * <p>
     * example:
     * <pre>
     *         <br/><code>
     *              &#64;Mapping("io.army.mapping.mysql.MySQLSetType",elementType=DayOfWeek.class)
     *              &#64;Column(comment="update day of week")
     *              private java.util.Set&lt;DayOfWeek> dayOfWeek;
     *
     *              &#64;Mapping("io.army.mapping.mysql.MySQLLongBlob",elementType=byte[].class)
     *              &#64;Column(comment="user image")
     *              private reactor.core.publisher.Flux&lt;byte[]></> image;
     *         </code>
     *     </pre>
     * </p>
     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    Class<?> elementType() default void.class;

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.MapMappingType} then required,
     * else ignore.
     * </p>
     * <p>
     * example:
     * <pre>
     *         <br/><code>
     *              &#64;Mapping("io.army.mapping.optional.JsonMapType",keyType=String.class,valueType=Object.class)
     *              &#64;Column(comment="client info data")
     *              private java.util.Map&lt;String,Object> clientInfO;
     *         </code>
     *     </pre>
     * </p>
     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    Class<?> keyType() default void.class;

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.MapMappingType} then required,
     * else ignore.
     * </p>
     * <p>
     * example:
     * <pre>
     *         <br/><code>
     *              &#64;Mapping("io.army.mapping.optional.JsonMapType",keyType=String.class,valueType=Object.class)
     *              &#64;Column(comment="client info data")
     *              private java.util.Map&lt;String,Object> clientInfO;
     *         </code>
     *     </pre>
     * </p>
     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    Class<?> valueType() default void.class;


}
