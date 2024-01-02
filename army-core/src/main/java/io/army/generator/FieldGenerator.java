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

package io.army.generator;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

/**
 * this interface create value for the mapping property of Entity when persist a entity before persistence.
 * if you want creation before persistence.
 * <p>
 * there is two ways for implementation of MultiGenerator:
 * <pre>
 *    Example one:
 *    public class MyMultiGenerator implements PreMultiGenerator {
 *        private MyMultiGenerator(){}
 *        &#047;&#042;&#042;
 *         &#042; &#064;paramMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public static MyMultiGenerator debugSQL(FieldMeta&lt;?,?&gt; paramMeta, Map&lt;String,String&gt; paramMap){
 *           ...
 *        }
 *     }
 * </pre>
 * or
 * <pre>
 *    Example two:
 *    public class MyMultiGenerator implements PreMultiGenerator {
 *        &#047;&#042;&#042;
 *         &#042; &#064;paramMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public MyMultiGenerator(FieldMeta&lt;?,?&gt; paramMeta,  Map&lt;String,String&gt; paramMap){...}
 *     }
 * </pre>
 *
 * @see io.army.annotation.Generator
 * @see FieldGeneratorFactory
 * @since Army 1.0
 */
public interface FieldGenerator {

    /**
     * @see GeneratorMeta#params()
     */
    String DEPEND_FIELD_NAME = "dependFieldName";


    Object next(FieldMeta<?> field, ReadWrapper domain) throws GeneratorException;


}
