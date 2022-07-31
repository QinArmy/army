package io.army.dialect;

import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.util._CollectionUtils;

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
        this.fieldGeneratorMap = _CollectionUtils.unmodifiableMap(builder.generatorMap);
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
