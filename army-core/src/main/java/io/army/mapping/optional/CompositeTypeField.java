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

package io.army.mapping.optional;

import io.army.mapping.MappingType;
import io.army.util._StringUtils;

import java.util.Objects;

public final class CompositeTypeField {

    public static CompositeTypeField from(String name, MappingType type) {
        if (!_StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name must have text.");
        }
        Objects.requireNonNull(type);
        return new CompositeTypeField(name, type);
    }

    public final String name;

    public final MappingType type;

    private CompositeTypeField(String name, MappingType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof CompositeTypeField) {
            final CompositeTypeField o = (CompositeTypeField) obj;
            match = o.name.equals(this.name)
                    && o.type.equals(this.type);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(this.getClass().getSimpleName())
                .append("[name:")
                .append(this.name)
                .append(",type:")
                .append(this.type)
                .append(']')
                .toString();
    }


}
