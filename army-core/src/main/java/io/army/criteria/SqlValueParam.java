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

package io.army.criteria;


import io.army.lang.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link  SQLParam}</li>
 *      <li>SQL literal expression</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SqlValueParam extends TypeInfer {


    interface SingleValue extends SqlValueParam {


    }

    interface SingleAnonymousValue extends SingleValue {

        @Nullable
        Object value();
    }


    interface NonNullValue extends SqlValueParam {

    }

    interface MultiValue extends SqlValueParam, NonNullValue {

        int columnSize();

    }


    interface NamedValue extends SqlValueParam {

        String name();

    }

    interface NamedMultiValue extends NamedValue, MultiValue {

    }


}
