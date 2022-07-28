package io.army.dialect;


import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;

import java.time.ZoneId;
import java.util.Map;

public interface _DialectEnv {

    ServerMeta serverMeta();

    /**
     * @return always return same value
     */
    @Nullable
    ZoneId envZoneId();

    ArmyEnvironment environment();

    Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap();

    MappingEnv mappingEnv();


}
