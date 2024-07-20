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

package io.army.mapping.postgre;

import io.army.type.DaoLayer;

import io.army.lang.Nullable;

/**
 * <p>
 * This interface representing the function that create postgre range instance.
 * <p>
 * <strong>NOTE</strong> :This interface present only in DAO layer,not service layer,business layer,web layer.
 *
 * @see PostgreSingleRangeType
 * @since 0.6.0
 */
@DaoLayer
public interface RangeFunction<T, R> {

    /**
     * @param lower null representing infinity
     * @param upper null representing infinity
     */
    R apply(boolean includeLower, @Nullable T lower, @Nullable T upper, boolean includeUpper);

}
