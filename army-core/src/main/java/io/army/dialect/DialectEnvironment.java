package io.army.dialect;


import io.army.meta.ServerMeta;

import java.time.ZoneOffset;

public interface DialectEnvironment {

    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    FieldValuesGenerator fieldValuesGenerator();

}
