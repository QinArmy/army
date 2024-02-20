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

package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.session.FactoryBuilderSpec;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.util.Map;


/**
 * <p>The instance of this interface is created by the implementation of {@link FactoryBuilderSpec}.
 */
public interface DialectEnv {

    String factoryName();

    ArmyEnvironment environment();

    Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap();


    boolean isReactive();

    /**
     * @return always same instance
     */
    ServerMeta serverMeta();

    @Nullable
    ZoneOffset zoneOffset();

    /**
     * @throws IllegalStateException throw when don't support  {@link JsonCodec}.
     */
    @Nullable
    JsonCodec jsonCodec();

    /**
     * @throws IllegalStateException throw when don't support  {@link XmlCodec}.
     */
    @Nullable
    XmlCodec xmlCodec();


    static Builder builder() {
        return DialectEnvImpl.builder();
    }

    interface Builder {

        Builder factoryName(String name);

        Builder environment(ArmyEnvironment env);

        Builder fieldGeneratorMap(Map<FieldMeta<?>, FieldGenerator> generatorMap);

        Builder reactive(boolean yes);

        Builder serverMeta(ServerMeta meta);

        Builder zoneOffset(@Nullable ZoneOffset zoneOffset);

        Builder jsonCodec(@Nullable JsonCodec codec);

        Builder xmlCodec(@Nullable XmlCodec codec);

        DialectEnv build();
    }


}
