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

package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.lang.Nullable;

/**
 * @since 0.6.0
 */
public abstract class _Assert {


    public static String assertHasText(@Nullable String text, String message) {
        if (!_StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

    public static void prepared(@Nullable Boolean prepared) {
        if (prepared == null || !prepared) {
            throw new CriteriaException(String.format("%s is non-prepared state.", Statement.class.getName()));
        }
    }

    public static void nonPrepared(@Nullable Boolean prepared) {
        if (prepared != null) {
            throw new CriteriaException(String.format("%s is prepared state.", Statement.class.getName()));
        }
    }



}
