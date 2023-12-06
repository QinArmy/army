package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.meta.ServerMeta;

import javax.annotation.Nullable;
import java.time.ZoneOffset;

public interface MappingEnv {

    boolean isReactive();

    /**
     * @return always same instance
     */
    ServerMeta serverMeta();

    ZoneOffset zoneOffset();

    /**
     * @throws IllegalStateException throw when don't support  {@link JsonCodec}.
     */
    JsonCodec jsonCodec();

    /**
     * @throws IllegalStateException throw when don't support  {@link XmlCodec}.
     */
    XmlCodec xmlCodec();

    static Builder builder() {
        return ArmyMappingEnv.builder();
    }

    interface Builder {

        Builder reactive(boolean yes);

        Builder serverMeta(ServerMeta meta);

        Builder zoneOffset(@Nullable ZoneOffset zoneOffset);

        Builder jsonCodec(@Nullable JsonCodec codec);

        Builder xmlCodec(@Nullable XmlCodec codec);

        MappingEnv build();

    }


}
