package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface DialectEnvironment {

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    FieldValuesGenerator fieldValuesGenerator();


    default JsonCodec jsonCodec() {
        throw new UnsupportedOperationException();
    }


}
