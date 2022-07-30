package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;

import java.time.ZoneId;

public interface MappingEnv {

    boolean isReactive();

    /**
     * @return always same instance
     */
    ServerMeta serverMeta();

    ZoneId zoneId();

    JsonCodec jsonCodec();


    static MappingEnv create(boolean reactive, ServerMeta serverMeta, @Nullable ZoneId zoneId, JsonCodec jsonCodec) {
        return MappingEnvImpl.create(reactive, serverMeta, zoneId, jsonCodec);
    }

}
