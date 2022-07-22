package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.env.ArmyEnvironment;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface _DialectEnv {

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    ArmyEnvironment environment();

    _FieldValueGenerator fieldValuesGenerator();

    MappingEnv mappingEnvironment();


    default JsonCodec jsonCodec() {
        throw new UnsupportedOperationException();
    }


}
