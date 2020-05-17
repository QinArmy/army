package io.army.annotation;

import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Specifies the {@link PreFieldGenerator} for the mapping property of Entity.
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
 * @see FieldGenerator
 * @see PreFieldGenerator
 * @since Army 1.0
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Generator {

    /**
     * Specifies the class name of {@link FieldGenerator}.
     */
    String value() ;

    /**
     * Specifies the creation value(s) of {@link FieldGenerator}.
     */
    Params[] params() default {};
}
