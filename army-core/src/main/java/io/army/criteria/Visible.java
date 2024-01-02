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

import io.army.util._Exceptions;

import javax.annotation.Nullable;

public enum Visible {

    ONLY_VISIBLE(Boolean.TRUE),
    ONLY_NON_VISIBLE(Boolean.FALSE),
    BOTH(null);

    public final Boolean value;

    Visible(@Nullable Boolean value) {
        this.value = value;
    }

    public static Visible from(final @Nullable Boolean visible) {
        final Visible visibleEnm;
        if (visible == null) {
            visibleEnm = Visible.BOTH;
        } else if (visible) {
            visibleEnm = Visible.ONLY_VISIBLE;
        } else {
            visibleEnm = Visible.ONLY_NON_VISIBLE;
        }
        return visibleEnm;
    }

    public final boolean isSupport(final Visible visible) {
        final boolean match;
        switch (this) {
            case ONLY_VISIBLE:
                match = visible == ONLY_VISIBLE;
                break;
            case ONLY_NON_VISIBLE:
                match = visible == ONLY_VISIBLE || visible == ONLY_NON_VISIBLE;
                break;
            case BOTH:
                match = true;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);

        }
        return match;
    }


}
