package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

final class MappingEnvImpl implements MappingEnv {

    static MappingEnvImpl create(boolean reactive, ServerMeta serverMeta, @Nullable ZoneOffset zoneId
            , JsonCodec jsonCodec) {
        return new MappingEnvImpl(reactive, serverMeta, zoneId, jsonCodec);
    }

    private final boolean reactive;

    private final ServerMeta serverMeta;

    private final ZoneOffset zoneId;

    private final JsonCodec jsonCodec;

    private MappingEnvImpl(boolean reactive, ServerMeta serverMeta, @Nullable ZoneOffset zoneId, JsonCodec jsonCodec) {
        this.reactive = reactive;
        this.serverMeta = serverMeta;
        this.zoneId = zoneId;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public boolean isReactive() {
        return this.reactive;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Override
    public ZoneOffset databaseZoneOffset() {
        ZoneOffset zoneId = this.zoneId;
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        }
        return zoneId;
    }

    @Override
    public JsonCodec jsonCodec() {
        return this.jsonCodec;
    }


    @Override
    public String toString() {
        return createBuilder()
                .append("[reactive:")
                .append(this.reactive)
                .append(",serverMeta:")
                .append(this.serverMeta)
                .append(",zoneId:")
                .append(this.zoneId)
                .append(",jsonCodec:")
                .append(this.jsonCodec)
                .toString();
    }

    private static StringBuilder createBuilder() {
        return new StringBuilder(MappingEnvImpl.class.getSimpleName());
    }


}
