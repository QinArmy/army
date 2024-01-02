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

package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.TableField;

import javax.annotation.Nullable;

public interface _Selection extends Selection, _SelectItem {


    @Nullable
    TableField tableField();

    /**
     * @return <ul>
     * <li>If this is {@link io.army.criteria.DerivedField}, then return {@link Selection} underlying expression</li>
     * <li>Else return this</li>
     * </ul>
     */
    @Nullable
    Expression underlyingExp();


}
