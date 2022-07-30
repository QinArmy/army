package io.army.sync.executor;

import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;

import java.util.Map;

public interface ExecutorEnvironment {

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap();


    ArmyEnvironment environment();

    boolean isReactive();

    /**
     * @return always same instance
     */
    MappingEnv mappingEnv();

}
