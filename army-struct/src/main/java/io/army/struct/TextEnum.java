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

package io.army.struct;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This interface is base interface of the enum that mapping to {@code io.army.mapping.TextEnumType}.
 * <p>
 * Army will persist {@link #text()} to database table column not {@link Enum#name()}.
 * If you want to persist {@link Enum#name()},then you should use {@code io.army.mapping.NameEnumType},
 * but never persist {@link Enum#ordinal()}.
 *
 * @see CodeEnum
 * @since 0.6.0
 */
public interface TextEnum {

    /**
     * @see Enum#name()
     */
    String name();

    /**
     * @return text that can representing this enum instance
     */
    String text();

    default TextEnum family() {
        return this;
    }


    @Nullable
    static <T extends Enum<T> & TextEnum> T resolve(final Class<?> enumClass, final String text) {
        final Map<String, T> map;
        map = EnumHelper.getTextMap(enumClass);
        return map.get(text);
    }

    /**
     * <p>
     * see {@code io.army.mapping.TextEnumType#getTextMap(java.lang.Class)}
     *
     *
     * @return a unmodified map
     */
    static <T extends Enum<T> & TextEnum> Map<String, T> getInstanceMap(Class<T> javaType)
            throws IllegalArgumentException {
        return EnumHelper.getTextMap(javaType);
    }

    static Map<String, ? extends TextEnum> getTextToEnumMap(final Class<?> javaType) {
        if (!(Enum.class.isAssignableFrom(javaType) && TextEnum.class.isAssignableFrom(javaType))) {
            String m = String.format("%s not %s type", javaType.getName(), TextEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        return EnumHelper.getTextMap(javaType);
    }

}
