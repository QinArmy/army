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

package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.meta.ServerMeta;

import javax.annotation.Nullable;
import java.time.ZoneOffset;


/**
 * <p>The instance of this interface is created by the implementation of {@link io.army.dialect.DialectParser}.
 */
public interface MappingEnv {

    boolean isReactive();

    /**
     * @return always same instance
     */
    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    /**
     * @throws IllegalStateException throw when don't support  {@link JsonCodec}.
     */
    JsonCodec jsonCodec();

    /**
     * @throws IllegalStateException throw when don't support  {@link XmlCodec}.
     */
    XmlCodec xmlCodec();

    static Builder builder() {
        return ArmyMappingEnv.builder();
    }

    interface Builder {

        Builder reactive(boolean yes);

        Builder serverMeta(ServerMeta meta);

        Builder zoneOffset(@Nullable ZoneOffset zoneOffset);

        Builder jsonCodec(@Nullable JsonCodec codec);

        Builder xmlCodec(@Nullable XmlCodec codec);

        MappingEnv build();

    }


}
