package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface MappingEnv {

    boolean isReactive();

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    JsonCodec jsonCodec();

}
