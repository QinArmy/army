package io.army.sync.executor;

import io.army.codec.FieldCodec;
import io.army.codec.JsonCodec;
import io.army.env.ArmyEnvironment;
import io.army.meta.FieldMeta;

import java.time.ZoneOffset;
import java.util.Map;

public interface ExecutorEnvironment {

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap();

    ArmyEnvironment environment();

    boolean inBeanContainer();

    ZoneOffset zoneOffset();

    JsonCodec jsonCodec();

    boolean isReactive();

}
