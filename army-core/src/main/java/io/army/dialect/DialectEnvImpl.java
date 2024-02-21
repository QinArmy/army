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
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.util.Map;

final class DialectEnvImpl implements DialectEnv {


    static DialectEnv.Builder builder() {
        return new EnvBuilder();
    }

    private final String factoryName;

    private final ArmyEnvironment environment;

    private final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap;

    private final boolean reactive;

    private final ServerMeta serverMeta;

    private final ZoneOffset zoneOffset;

    private final JsonCodec jsonCodec;

    private final XmlCodec xmlCodec;

    private DialectEnvImpl(EnvBuilder builder) {
        this.factoryName = builder.factoryName;
        this.environment = builder.env;
        this.fieldGeneratorMap = _Collections.unmodifiableMap(builder.generatorMap);
        this.reactive = builder.reactive;

        this.serverMeta = builder.serverMeta;
        this.zoneOffset = builder.zoneOffset;
        this.jsonCodec = builder.jsonCodec;
        this.xmlCodec = builder.xmlCodec;

        if (this.serverMeta == null) {
            throw new IllegalArgumentException();
        }

    }


    @Override
    public String factoryName() {
        return this.factoryName;
    }

    @Override
    public ArmyEnvironment environment() {
        return this.environment;
    }

    @Override
    public Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap() {
        return this.fieldGeneratorMap;
    }

    @Override
    public boolean isReactive() {
        return this.reactive;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Nullable
    @Override
    public ZoneOffset zoneOffset() {
        return this.zoneOffset;
    }

    @Nullable
    @Override
    public JsonCodec jsonCodec() {
        return this.jsonCodec;
    }

    @Nullable
    @Override
    public XmlCodec xmlCodec() {
        return this.xmlCodec;
    }

    @Override
    public String toString() {
        return String.format("%s factory:%s", DialectEnvImpl.class.getSimpleName(), this.factoryName);
    }


    private static final class EnvBuilder implements DialectEnv.Builder {

        private String factoryName;

        private ArmyEnvironment env;

        private Map<FieldMeta<?>, FieldGenerator> generatorMap;

        private boolean reactive;

        private ServerMeta serverMeta;

        private ZoneOffset zoneOffset;

        private JsonCodec jsonCodec;

        private XmlCodec xmlCodec;

        @Override
        public Builder factoryName(String name) {
            this.factoryName = name;
            return this;
        }

        @Override
        public Builder environment(ArmyEnvironment env) {
            this.env = env;
            return this;
        }

        @Override
        public Builder fieldGeneratorMap(Map<FieldMeta<?>, FieldGenerator> generatorMap) {
            this.generatorMap = generatorMap;
            return this;
        }

        @Override
        public Builder reactive(boolean yes) {
            this.reactive = yes;
            return this;
        }

        @Override
        public Builder serverMeta(ServerMeta meta) {
            this.serverMeta = meta;
            return this;
        }

        @Override
        public Builder zoneOffset(@Nullable ZoneOffset zoneOffset) {
            this.zoneOffset = zoneOffset;
            return this;
        }

        @Override
        public Builder jsonCodec(@Nullable JsonCodec codec) {
            this.jsonCodec = codec;
            return this;
        }

        @Override
        public Builder xmlCodec(@Nullable XmlCodec codec) {
            this.xmlCodec = codec;
            return this;
        }

        @Override
        public DialectEnv build() {
            return new DialectEnvImpl(this);
        }


    }//EnvBuilder


}
