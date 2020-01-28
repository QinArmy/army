package io.army.annotation;

import io.army.generator.MultiGenerator;
import io.army.generator.SingleGenerator;

/**
 * Specifies the {@link MultiGenerator} for the mapping property of Entity.
 *
 * <p>
 * <pre>
 *    Example:
 *
 *    &#064;Table(name="u_user", schema="army",comment="storage user info")
 *    public class User {
 *
 *      &#064;Generator("io.army.generator.snowflake.SnowflakeMultiGenerator")
 *      &#064;Column
 *      private Long id;
 *
 *      &#064;Column
 *      private LocalDateTime createTime;
 *
 *      &#064;Column
 *      private Boolean visible;
 *
 *      &#064;Column
 *      private LocalDateTime updateTime;
 *
 *      &#064;Column
 *      private Integer version;
 *
 *      &#064;Generator(generator=SnowflakeMultiGenerator.class,params={&#064;GeneratorParam(name="startTime",value="1580224449498")})
 *      &#064;Column(updatable=false,comment="identifier of user")
 *      private String uid;
 *
 *     }
 * </pre>
 * </p>
 *
 * @see Column
 * @since Army 1.0
 */
public @interface Generator {

    /**
     * Specifies the class name of {@link MultiGenerator}.
     * the invocation of the property after {@link #generator()}.
     */
    String value() default "";

    /**
     * Specifies the class of {@link MultiGenerator}.
     * the invocation of the property before {@link #value()}.
     */
    Class<? extends MultiGenerator> generator() default MultiGenerator.class;

    /**
     * when generator isn't a implementation of {@link SingleGenerator}
     * and is a implementation of {@link MultiGenerator}
     * ,Specifies the creation param(s) of {@link MultiGenerator}.
     */
    GeneratorParam[] params() default {};
}
