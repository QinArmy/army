package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface MappingEnvironment {

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    JsonCodec jsonCodec();

}
