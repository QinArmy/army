package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;

import java.time.ZoneId;

final class MappingEnvImpl implements MappingEnv {

    static MappingEnvImpl create(boolean reactive, ServerMeta serverMeta, @Nullable ZoneId zoneId, JsonCodec jsonCodec) {
        return new MappingEnvImpl(reactive, serverMeta, zoneId, jsonCodec);
    }

    private final boolean reactive;

    private final ServerMeta serverMeta;

    private final ZoneId zoneId;

    private final JsonCodec jsonCodec;

    private MappingEnvImpl(boolean reactive, ServerMeta serverMeta, @Nullable ZoneId zoneId, JsonCodec jsonCodec) {
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
    public ZoneId zoneId() {
        final ZoneId zoneId = this.zoneId;
        return zoneId == null ? ZoneId.systemDefault() : zoneId;
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
