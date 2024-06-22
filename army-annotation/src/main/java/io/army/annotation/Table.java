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

/**
 * Specifies the tableMeta for the annotated domain.
 * <p>
 * <pre>
 *    Example:
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
     * <p>
     * see io.army.meta.TableMeta#tableName()
     */
    String name();

    /**
     * (Required) The comment of the tableMeta.
     */
    String comment();

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
     * Specifies the tableMeta immutable,if true,singleUpdate dml of the tableMeta is allowed by army.
     * <p> Default value is false.
     */
    boolean immutable() default false;


    /**
     * (Optional) The option clause of create table
     *
     */
    String tableOption() default "";

    /**
     * (Optional) The option clause of create table
     */
    String partitionOption() default "";
}
