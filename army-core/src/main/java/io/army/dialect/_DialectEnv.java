package io.army.dialect;


import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;

import java.util.Map;

public interface _DialectEnv {

    ServerMeta serverMeta();

    ArmyEnvironment environment();

    Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap();

    MappingEnv mappingEnv();


}
