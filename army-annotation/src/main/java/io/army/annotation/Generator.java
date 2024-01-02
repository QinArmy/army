/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Specifies the {@code io.army.generator.PreFieldGenerator} for the mapping property of Entity.
 * <p>
 * <pre>
 *    Example:
 *    &#064;Table(name="u_user", schema="army",comment="storage user info")
 *    public class User {
 *      &#064;Generator("io.army.generator.snowflake.SnowflakeMultiGenerator")
 *      &#064;Column
 *      private Long id;
 *      &#064;Column
 *      private LocalDateTime createTime;
 *      &#064;Column
 *      private Boolean visible;
 *      &#064;Column
 *      private LocalDateTime updateTime;
 *      &#064;Column
 *      private Integer version;
 *      &#064;Generator(generator=SnowflakeMultiGenerator.class,params={&#064;GeneratorParam(name="startTime",value="1580224449498")})
 *      &#064;Column(updatable=false,comment="identifier of user")
 *      private String uid;
 *     }
 * </pre>
 * * <p>
 * see {@code io.army.generator.PreFieldGenerator} and {@code io.army.generator.FieldGenerator}
 * * @see Column
 *
 * @since 0.6.0
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Generator {

    GeneratorType type() default GeneratorType.PRECEDE;

    /**
     * Specifies the class name of {@code io.army.generator.PreFieldGenerator}.
     */
    String value() default "";

    /**
     * Specifies the creation value(s) of {@code io.army.generator.FieldGenerator}.
     */
    Param[] params() default {};
}
