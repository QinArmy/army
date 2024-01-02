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

import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.util._Collections;

import java.util.Map;

final class DialectEnvImpl implements DialectEnv {


    static DialectEnv.Builder builder() {
        return new EnvBuilder();
    }

    private final String factoryName;

    private final ArmyEnvironment environment;

    private final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap;

    private final MappingEnv mappingEnv;

    private DialectEnvImpl(EnvBuilder builder) {
        this.factoryName = builder.factoryName;
        this.environment = builder.env;
        this.fieldGeneratorMap = _Collections.unmodifiableMap(builder.generatorMap);
        this.mappingEnv = builder.mappingEnv;
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
    public MappingEnv mappingEnv() {
        return this.mappingEnv;
    }

    @Override
    public String toString() {
        return String.format("%s factory:%s,mappingEnv:%s", DialectEnvImpl.class.getSimpleName()
                , this.factoryName, this.mappingEnv);
    }


    private static final class EnvBuilder implements DialectEnv.Builder {

        private String factoryName;

        private ArmyEnvironment env;

        private Map<FieldMeta<?>, FieldGenerator> generatorMap;

        private MappingEnv mappingEnv;

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
        public Builder mappingEnv(MappingEnv mappingEnv) {
            this.mappingEnv = mappingEnv;
            return this;
        }

        @Override
        public DialectEnv build() {
            assert this.factoryName != null
                    && this.env != null
                    && this.generatorMap != null
                    && this.mappingEnv != null;
            return new DialectEnvImpl(this);
        }


    }//EnvBuilder


}
