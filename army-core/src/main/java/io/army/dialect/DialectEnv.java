package io.army.dialect;


import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;

import java.util.Map;

public interface DialectEnv {

    String factoryName();

    ArmyEnvironment environment();

    Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap();

    MappingEnv mappingEnv();


    static Builder builder() {
        return DialectEnvImpl.builder();
    }

    interface Builder {

        Builder factoryName(String name);

        Builder environment(ArmyEnvironment env);

        Builder fieldGeneratorMap(Map<FieldMeta<?>, FieldGenerator> generatorMap);


        Builder mappingEnv(MappingEnv mappingEnv);

        DialectEnv build();
    }


}
