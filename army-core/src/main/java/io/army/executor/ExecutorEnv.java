package io.army.executor;

import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;

import java.util.Map;

public interface ExecutorEnv {

    String factoryName();

    ServerMeta serverMeta();

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap();


    ArmyEnvironment environment();

    /**
     * @return always same instance
     */
    MappingEnv mappingEnv();

}
