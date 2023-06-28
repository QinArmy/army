package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface MappingEnv {

    boolean isReactive();

    /**
     * @return always same instance
     */
    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    JsonCodec jsonCodec();


    static MappingEnv create(boolean reactive, ServerMeta serverMeta, @Nullable ZoneOffset zoneId, JsonCodec jsonCodec) {
        return MappingEnvImpl.create(reactive, serverMeta, zoneId, jsonCodec);
    }

}
