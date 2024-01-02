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

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * <p>
     * Required
     * *
     *
     * @return the qualified class name of the implementation of {@code io.army.mapping.MappingType}.
     */
    String value();

    /**
     * <ul>
     *     <li>'enum' : enum name ,for example : postgre enum</li>
     * </ul>
     */
    String[] params() default {};

    String func() default "";

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.TextMappingType} and representing binary then required,
     * else ignore.
     *     * <p>
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
     *     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    String charset() default "";

    /**
     * <p>
     * If {@link #value()} is the class name
     * of the implementation of {@code io.army.mapping.ElementMappingType} then required,
     * else ignore.
     *     * <p>
     * example:
     * <pre>
     *         <br/><code>
     *              &#64;Mapping("io.army.mapping.mysql.MySQLSetType",elements=DayOfWeek.class)
     *              &#64;Column(comment="update day of week")
     *              private java.util.Set&lt;DayOfWeek> dayOfWeek;
     *
     *              &#64;Mapping("io.army.mapping.mysql.MySQLLongBlob",elements=byte[].class)
     *              &#64;Column(comment="user image")
     *              private reactor.core.publisher.Flux&lt;byte[]> image;
     *         </code>
     *     </pre>
     *     *
     * @return the name of {@link java.nio.charset.Charset}.
     */
    Class<?>[] elements() default {};


}
