package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface _DialectEnvironment {

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    FieldGenerator fieldValuesGenerator();


    default JsonCodec jsonCodec() {
        throw new UnsupportedOperationException();
    }


}
